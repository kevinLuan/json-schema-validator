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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * Interface for converting between Java objects and JsonNode representations.
 * Provides methods for parsing, converting, and stringifying JSON data.
 * 
 * @autor SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public interface JsonNodeConverter {

    /**
     * Checks if the given JsonNode is null.
     * 
     * @param jsonNode the JsonNode to check
     * @return true if the JsonNode is null, false otherwise
     */
    boolean isNull(JsonNode jsonNode);

    /**
     * Converts an Object to its JSON string representation.
     * 
     * @param value the Object to convert
     * @return the JSON string representation of the Object
     */
    String toString(Object value);

    /**
     * Converts a JsonNode to its string representation.
     * 
     * @param node the JsonNode to convert
     * @return the string representation of the JsonNode
     */
    String toString(JsonNode node);

    /**
     * Parses a JsonNode to extract its value as an Object.
     * 
     * @param node the JsonNode to parse
     * @return the extracted value as an Object
     */
    Object parserValue(JsonNode node);

    /**
     * Parses an ObjectNode to extract its values into a Map.
     * 
     * @param node the ObjectNode to parse
     * @return a Map containing the extracted values
     */
    Map<String, Object> parserMap(ObjectNode node);

    /**
     * Parses an ArrayNode to extract its elements into a List.
     * 
     * @param arrayNode the ArrayNode to parse
     * @return a List containing the extracted elements
     */
    List<Object> parserArrayNode(ArrayNode arrayNode);

    /**
     * Parses a JSON string to create a JsonNode.
     * 
     * @param json the JSON string to parse
     * @return the resulting JsonNode
     */
    JsonNode parser(String json);

    /**
     * Converts a Java object to a JsonNode.
     * 
     * @param value the Java object to convert
     * @return the resulting JsonNode
     */
    JsonNode convert(Object value);

    /**
     * Converts a Java object to its JSON string representation.
     * 
     * @param pojo the Java object to convert
     * @return the JSON string representation of the object
     */
    String stringify(Object pojo);

    /**
     * Parses a JSON string into an object of the specified class type.
     * 
     * @param <T> the type of the object
     * @param json the JSON string to parse
     * @param a the class of the object
     * @return the parsed object
     */
    <T> T parse(String json, Class<T> a);

    /**
     * Parses a JSON string into an object of the specified type reference.
     * 
     * @param <T> the type of the object
     * @param json the JSON string to parse
     * @param a the type reference of the object
     * @return the parsed object
     */
    <T> T parse(String json, TypeReference<T> a);

    /**
     * Converts a JsonNode to a pretty-printed JSON string.
     * 
     * @param jsonNode the JsonNode to convert
     * @return the pretty-printed JSON string
     */
    String prettyPrinter(JsonNode jsonNode);
}
