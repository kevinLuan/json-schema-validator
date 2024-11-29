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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The {@code GsonEncoder} class provides methods to encode objects into JSON strings
 * and decode JSON strings back into objects using the Gson library.
 * It implements the {@code Encoder} interface.
 */
public class GsonEncoder implements Encoder {

    /**
     * Singleton instance of {@code GsonEncoder}.
     */
    public static GsonEncoder INSTANCE = new GsonEncoder();

    /**
     * Gson instance used for JSON serialization and deserialization.
     */
    private static Gson       gson     = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
                                           .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * Encodes an object into its JSON representation.
     *
     * @param t the object to be encoded
     * @return a JSON string representing the object
     */
    public String encode(Object t) {
        return gson.toJson(t);
    }

    /**
     * Decodes a JSON string into an object of the specified class.
     *
     * @param json  the JSON string to be decoded
     * @param clazz the class of T
     * @param <T>   the type of the desired object
     * @return an object of type T from the JSON string
     */
    public <T> T decode(String json, Class<T> clazz) {
        return (T) gson.fromJson(json, clazz);
    }
}
