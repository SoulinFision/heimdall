/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.boot.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Jackson工具类
 *
 * @author Luter
 */
@SuppressWarnings("unchecked")
@Slf4j
public final class JacksonUtils {
    /**
     * The constant OBJECT_MAPPER.
     */
    private final static ObjectMapper OBJECT_MAPPER;
    /**
     * The constant DEFAULT_DATE_TIME_FORMAT.
     */
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * The constant DEFAULT_DATE_FORMAT.
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * The constant DEFAULT_TIME_FORMAT.
     */
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {
        OBJECT_MAPPER = initObjectMapper(new ObjectMapper());
    }


    /**
     * 获取ObjectMapper
     *
     * @return the object mapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 以默认参数 初始化 ObjectMapper
     *
     * @param objectMapper the object mapper
     * @return the object mapper
     */
    public static ObjectMapper initObjectMapper(ObjectMapper objectMapper) {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
        return doInitObjectMapper(objectMapper);
    }

    /**
     * 初始化 ObjectMapper 时间方法
     *
     * @param objectMapper the object mapper
     * @return the object mapper
     */
    private static ObjectMapper doInitObjectMapper(ObjectMapper objectMapper) {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //不显示为null的字段
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //枚举值处理
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        //不存在属性处理
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 过滤对象的null属性.始终包含null属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //忽略transient标记的字段
        objectMapper.enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
        return registerModule(objectMapper);
    }

    /**
     * 为 ObjectMapper 注册模块，包括常见日期时间 参数名称，时区设置等
     *
     * @param objectMapper the object mapper
     * @return the object mapper
     */
    public static ObjectMapper registerModule(ObjectMapper objectMapper) {
        // 指定时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 日期类型字符串处理
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT));
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer((DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT))));
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }


    /**
     * 对象转换成JSON字符串
     *
     * @param object 对象
     * @return json字符串 string
     */
    public static String toJson(Object object) {
        if (isCharSequence(object)) {
            return (String) object;
        }
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换Json
     *
     * @param object 对象
     * @return 字符串 string
     */
    public static String toPrettyJson(Object object) {
        if (isCharSequence(object)) {
            return (String) object;
        }
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * 是否为CharSequence类型
     * </p>
     *
     * @param object the object
     * @return the boolean
     */
    public static Boolean isCharSequence(Object object) {
        return !Objects.isNull(object) && isCharSequence(object.getClass());
    }

    /**
     * Is char sequence boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param json the json
     * @return the object
     */
    public static Object parse(String json) {
        Object object = null;
        try {
            object = getObjectMapper().readValue(json, Object.class);
        } catch (Exception ignored) {
        }
        return object;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param <T>   the type parameter
     * @param json  the json
     * @param clazz the clazz
     * @return the t
     */
    public static <T> T readValue(String json, Class<T> clazz) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, clazz);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * Json转换为对象 转换失败返回null
     *
     * @param <T>          the type parameter
     * @param json         the json
     * @param valueTypeRef the value type ref
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public static <T> T readValue(String json, TypeReference valueTypeRef) {
        T t = null;
        try {
            t = (T) getObjectMapper().readValue(json, valueTypeRef);
        } catch (Exception ignored) {
        }
        return t;
    }

    /**
     * Map to jon string.
     *
     * @param object the object
     * @return the string
     */
    public static String mapToJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON转换错误:" + e.getMessage());

        }
        return null;
    }

    /**
     * List to jon string.
     *
     * @param list the list
     * @return the string
     */
    @SuppressWarnings("unchecked")
    public static String listToJson(List list) {
        try {
            return getObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {

            log.error("JSON转换错误:" + e.getMessage());

        }
        return null;
    }

    /**
     * Object to json string.
     *
     * @param object the object
     * @return the string
     */
    public static String objectToJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON转换错误:" + e.getMessage());

        }
        return null;
    }

    /**
     * Map to pojo t.
     *
     * @param <T>   the type parameter
     * @param map   the map
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public static <T> T mapToPojo(Map map, Class<T> clazz) {
        return getObjectMapper().convertValue(map, clazz);
    }

    /**
     * Json to object t.
     *
     * @param <T>          the type parameter
     * @param jsonArrayStr the json array str
     * @param clazz        the clazz
     * @return the t
     */
    public static <T> T jsonToObject(String jsonArrayStr, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(jsonArrayStr, clazz);
        } catch (IOException e) {
            log.error("JSON转换错误:" + e.getMessage());
            return null;
        }
    }

    /**
     * Json to object list list.
     *
     * @param <T>          the type parameter
     * @param jsonArrayStr the json array str
     * @param clazz        the clazz
     * @return the list
     */
    public static <T> List<T> jsonToObjectList(String jsonArrayStr, Class<T> clazz) {
        List<Map<String, Object>> list;
        try {
            list = (List<Map<String, Object>>) getObjectMapper().readValue(jsonArrayStr,
                    new TypeReference<List<T>>() {
                    });
            List<T> result = new ArrayList<>();
            for (Map<String, Object> map : list) {
                result.add(mapToPojo(map, clazz));
            }
            return result;
        } catch (IOException e) {
            log.error("JSON转换错误:" + e.getMessage());
        }
        return null;
    }
}
