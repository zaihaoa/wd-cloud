package com.wd.common.server.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author huangwenda
 * @date 2022/12/5 11:41
 **/
@Configuration
public class JacksonSerializerConfig {

    private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String LOCAL_TIME_FORMAT = "HH:mm:ss";

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper om = new ObjectMapper();
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        //序列化
//
//        javaTimeModule.addSerializer(LocalDateTime.class,
//                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)));
//        javaTimeModule.addSerializer(LocalDate.class,
//                new LocalDateSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
//        javaTimeModule.addSerializer(LocalTime.class,
//                new LocalTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT)));
//
//        //反序列化
//        javaTimeModule.addDeserializer(LocalDateTime.class,
//                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)));
//        javaTimeModule.addDeserializer(LocalDate.class,
//                new LocalDateDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
//        javaTimeModule.addDeserializer(LocalTime.class,
//                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT)));
//
//        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        om.registerModule(javaTimeModule);
//        return om;
//    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {

            // 时间反序列化（接收数据）
            builder.deserializerByType(LocalDateTime.class,
                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)));
            builder.deserializerByType(LocalDate.class,
                    new LocalDateDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
            builder.deserializerByType(LocalTime.class,
                    new LocalTimeDeserializer(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT)));

            // 时间序列化（返回数据）
            builder.serializerByType(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)));
            builder.serializerByType(LocalDate.class,
                    new LocalDateSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
            builder.serializerByType(LocalTime.class,
                    new LocalTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT)));


            // 数值序列化（返回数据）
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
            builder.serializerByType(BigDecimal.class, new JsonSerializer<BigDecimal>() {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
                    gen.writeString(value.stripTrailingZeros().toPlainString());
                }
            });
        };
    }
}
