package com.team03.monew.config;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.Binary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(UUIDToBinaryConverter.INSTANCE);
        converters.add(BinaryToUUIDConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    @WritingConverter
    enum UUIDToBinaryConverter implements Converter<UUID, Binary> {
        INSTANCE;

        @Override
        public Binary convert(UUID source) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(source.getMostSignificantBits());
            bb.putLong(source.getLeastSignificantBits());
            return new Binary((byte) 4, bb.array()); // subtype 4로 UUID 저장
        }
    }

    @ReadingConverter
    enum BinaryToUUIDConverter implements Converter<Binary, UUID> {
        INSTANCE;

        @Override
        public UUID convert(Binary source) {
            ByteBuffer bb = ByteBuffer.wrap(source.getData());
            long mostSig = bb.getLong();
            long leastSig = bb.getLong();
            return new UUID(mostSig, leastSig);
        }
    }
}
