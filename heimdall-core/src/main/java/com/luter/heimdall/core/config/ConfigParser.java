/*
 *
 *  *    Copyright 2020-2021 Luter.me
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.luter.heimdall.core.config;

import com.luter.heimdall.core.config.property.AuthorityProperty;
import com.luter.heimdall.core.config.property.CookieProperty;
import com.luter.heimdall.core.config.property.SchedulerProperty;
import com.luter.heimdall.core.config.property.SessionProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件解析器,用以解析默认配置文件
 * <p>
 * 集成spring boot starter 后不需要此类，将由 spring 自动注入配置
 *
 * @author Luter
 */
@Slf4j
public final class ConfigParser {

    /**
     * 配置文件名称
     */
    public static final String CONFIG_PATH = "heimdall.properties";

    /**
     * 解析配置文件
     *
     * @return the config
     */
    public static Config parseConfig() {
        Map<String, String> map = parseProps();
        if (map == null) {
            throw new RuntimeException("找不到配置文件：" + CONFIG_PATH, null);
        }
        final CookieProperty cookieConfig = (CookieProperty) readPropValue(map, "heimdall.security.cookie.", new CookieProperty());
        final SessionProperty sessionConfig = (SessionProperty) readPropValue(map, "heimdall.security.session.", new SessionProperty());
        final SchedulerProperty schedulerConfig = (SchedulerProperty) readPropValue(map, "heimdall.security.scheduler.", new SchedulerProperty());
        final AuthorityProperty authorityProperty = (AuthorityProperty) readPropValue(map, "heimdall.security.authority.", new AuthorityProperty());
        Config config = new Config();
        config.setCookie(cookieConfig);
        config.setSession(sessionConfig);
        config.setScheduler(schedulerConfig);
        config.setAuthority(authorityProperty);
        log.warn("从配置文件:{} 加载配置参数完毕，配置\n{}", CONFIG_PATH, config.toString());
        return config;
    }

    /**
     * 把配置文件解析成 Map
     *
     * @return the map
     */
    private static Map<String, String> parseProps() {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream is = ConfigParser.class.getClassLoader().getResourceAsStream(ConfigParser.CONFIG_PATH);
            if (is == null) {
                return null;
            }
            Properties prop = new Properties();
            prop.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            for (String key : prop.stringPropertyNames()) {
                map.put(key.toLowerCase(), prop.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException("配置文件(" + ConfigParser.CONFIG_PATH + ")加载失败", e);
        }
        return map;
    }


    /**
     * 根据配置前缀，通过反射读取属性值
     *
     * @param map    配置文件解析后的 Map
     * @param prefix 解析的配置项前缀，如:xxx.yyy.name
     * @param obj    解析后转换成的实体对象
     * @return 配置实体类
     */
    private static Object readPropValue(Map<String, String> map, String prefix, Object obj) {
        if (map == null) {
            map = new HashMap<>();
        }
        Class<?> cs;
        if (obj instanceof Class) {
            cs = (Class<?>) obj;
            obj = null;
        } else {
            cs = obj.getClass();
        }
        for (Field field : cs.getDeclaredFields()) {
            String value = map.get((prefix + field.getName()).toLowerCase());
            if (value == null) {
                continue;
            }
            try {
                Object valueConvert = getPropValueByType(value, field.getType());
                field.setAccessible(true);
                field.set(obj, valueConvert);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalArgumentException("解析配置参数错误:" + prefix + field.getName(), e);
            }
        }
        return obj;
    }


    /**
     * 判断配置属性类型，获取参数值
     *
     * @param <T>    目标配置类型
     * @param str    参数名称
     * @param aClass 类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    private static <T> T getPropValueByType(String str, Class<T> aClass) {
        Object value;
        if (str == null) {
            value = null;
        } else if (aClass.equals(String.class)) {
            value = str;
        } else if (aClass.equals(int.class) || aClass.equals(Integer.class)) {
            value = Integer.parseInt(str);
        } else if (aClass.equals(long.class) || aClass.equals(Long.class)) {
            value = Long.parseLong(str);
        } else if (aClass.equals(short.class) || aClass.equals(Short.class)) {
            value = Short.parseShort(str);
        } else if (aClass.equals(float.class) || aClass.equals(Float.class)) {
            value = Float.parseFloat(str);
        } else if (aClass.equals(double.class) || aClass.equals(Double.class)) {
            value = Double.parseDouble(str);
        } else if (aClass.equals(boolean.class) || aClass.equals(Boolean.class)) {
            value = Boolean.valueOf(str);
        } else {
            throw new IllegalArgumentException("参数配置错误: " + str, null);
        }
        return (T) value;
    }
}
