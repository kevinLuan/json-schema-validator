/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.encode;

import java.io.IOException;
import java.util.TimeZone;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * JacksonEncoder is a utility class for encoding and decoding JSON using Jackson library.
 * It implements the Encoder interface, providing methods to serialize and deserialize objects.
 */
public class JacksonEncoder implements Encoder {
    // A static ObjectMapper instance for JSON processing
    public static final ObjectMapper mapper   = new ObjectMapper();

    static {
        // Configure the ObjectMapper to ignore unknown properties during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Configure the ObjectMapper to not fail on empty beans during serialization
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // Uncomment the following line to disable writing null map values
        // mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // Uncomment the following line to set a custom date format
        // mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // Create a SimpleModule for custom serializers and deserializers
        SimpleModule module = new SimpleModule();
        // Uncomment the following line to add a custom deserializer for Date class
        // module.addDeserializer(Date.class, new DateDeserializer());

        // Register the module with the ObjectMapper
        mapper.registerModule(module);

        // Set the default time zone to Asia/Shanghai to address the 8-hour offset issue with UTC
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    // Singleton instance of JacksonEncoder
    public static JacksonEncoder     INSTANCE = new JacksonEncoder();

    /**
     * Encodes an object into a JSON string.
     * 
     * @param obj the object to encode
     * @return the JSON string representation of the object
     * @throws RuntimeException if encoding fails
     */
    public String encode(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("encode(" + obj + ") error", e);
        }
    }

    /**
     * Decodes a JSON string into an object of the specified type.
     * 
     * @param <T> the type of the object to decode
     * @param json the JSON string to decode
     * @param type the class of the type to decode into
     * @return the decoded object
     * @throws RuntimeException if decoding fails
     */
    @Override
    public <T> T decode(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("decode(" + json + ") error", e);
        }
    }
}
