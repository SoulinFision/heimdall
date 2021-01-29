package com.luter.heimdall.sample.common.util;

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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@Slf4j
public final class JacksonUtils {
    private final static ObjectMapper OBJECT_MAPPER;
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {
        OBJECT_MAPPER = initObjectMapper(new ObjectMapper());
    }


    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static ObjectMapper initObjectMapper(ObjectMapper objectMapper) {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
        log.warn("初始化 Jackson 工具类");
        return doInitObjectMapper(objectMapper);
    }

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


    public static Consumer<HttpMessageConverter<?>> wrapperObjectMapper() {
        return converter -> {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter httpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                httpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
                registerModule(httpMessageConverter.getObjectMapper());
            }
        };
    }

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

    public static Boolean isCharSequence(Object object) {
        return !Objects.isNull(object) && isCharSequence(object.getClass());
    }

    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    public static Object parse(String json) {
        Object object = null;
        try {
            object = getObjectMapper().readValue(json, Object.class);
        } catch (Exception ignored) {
        }
        return object;
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, clazz);
        } catch (Exception ignored) {
        }
        return t;
    }

    public static <T> T readValue(String json, TypeReference valueTypeRef) {
        T t = null;
        try {
            t = (T) getObjectMapper().readValue(json, valueTypeRef);
        } catch (Exception ignored) {
        }
        return t;
    }

    public static String mapToJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON转换错误:" + e.getMessage());

        }
        return null;
    }

    public static String listToJSON(List list) {
        try {
            return getObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {

            log.error("JSON转换错误:" + e.getMessage());

        }
        return null;
    }

    public static String objectToJson(Object object) {
        return toJson(object);
    }

    public static <T> T mapToPojo(Map map, Class<T> clazz) {
        return getObjectMapper().convertValue(map, clazz);
    }

    public static <T> T jsonToObject(String jsonArrayStr, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(jsonArrayStr, clazz);
        } catch (IOException e) {
            log.error("JSON转换错误:" + e.getMessage());
            return null;
        }
    }

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
