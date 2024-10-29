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
 * NodeFactory is a utility class for converting between Java objects and JSON nodes.
 * It uses a JsonNodeConverter to perform the conversions.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class NodeFactory {
    // Static instance of JsonNodeConverter used for JSON operations
    private static JsonNodeConverter jsonNodeConverter = new DefaultJsonNodeConverter(JacksonEncoder.mapper);

    /**
     * Sets a custom JsonNodeConverter.
     * 
     * @param jsonNodeConverter the JsonNodeConverter to be used
     */
    public static void setJsonNodeConverter(JsonNodeConverter jsonNodeConverter) {
        NodeFactory.jsonNodeConverter = jsonNodeConverter;
    }

    /**
     * Checks if the given JsonNode is null.
     * 
     * @param jsonNode the JsonNode to check
     * @return true if the node is null, false otherwise
     */
    public static boolean isNull(JsonNode jsonNode) {
        return jsonNodeConverter.isNull(jsonNode);
    }

    /**
     * Converts an object to its JSON string representation.
     * 
     * @param value the object to convert
     * @return the JSON string representation of the object
     */
    public static String toString(Object value) {
        return jsonNodeConverter.toString(value);
    }

    /**
     * Converts a JsonNode to its JSON string representation.
     * 
     * @param node the JsonNode to convert
     * @return the JSON string representation of the node
     */
    public static String toString(JsonNode node) {
        return jsonNodeConverter.toString(node);
    }

    /**
     * Parses a JsonNode to extract its value as a Java object.
     * 
     * @param node the JsonNode to parse
     * @return the extracted Java object
     */
    public static Object parserValue(JsonNode node) {
        return jsonNodeConverter.parserValue(node);
    }

    /**
     * Parses an ObjectNode to extract its values into a Map.
     * 
     * @param node the ObjectNode to parse
     * @return a Map containing the node's key-value pairs
     */
    public static Map<String, Object> parserMap(ObjectNode node) {
        return jsonNodeConverter.parserMap(node);
    }

    /**
     * Parses an ArrayNode to extract its elements into a List.
     * 
     * @param arrayNode the ArrayNode to parse
     * @return a List containing the elements of the array node
     */
    public static List<Object> parserArrayNode(ArrayNode arrayNode) {
        return jsonNodeConverter.parserArrayNode(arrayNode);
    }

    /**
     * Parses a JSON string into a JsonNode.
     * 
     * @param json the JSON string to parse
     * @return the resulting JsonNode
     */
    public static JsonNode parser(String json) {
        return jsonNodeConverter.parser(json);
    }

    /**
     * Converts a Java object to a JsonNode.
     * 
     * @param value the Java object to convert
     * @return the resulting JsonNode
     */
    public static JsonNode convert(Object value) {
        return jsonNodeConverter.convert(value);
    }

    /**
     * Converts a Java object to a JSON string.
     * 
     * @param pojo the Java object to convert
     * @return the JSON string representation of the object
     */
    public static String stringify(Object pojo) {
        return jsonNodeConverter.stringify(pojo);
    }

    /**
     * Parses a JSON string into a Java object of the specified type.
     * 
     * @param json the JSON string to parse
     * @param type the class of the type to return
     * @param <T> the type of the desired object
     * @return the parsed Java object
     */
    public static <T> T parse(String json, Class<T> type) {
        return jsonNodeConverter.parse(json, type);
    }

    /**
     * Parses a JSON string into a Java object of the specified type reference.
     * 
     * @param json the JSON string to parse
     * @param typeRef the type reference of the desired object
     * @param <T> the type of the desired object
     * @return the parsed Java object
     */
    public static <T> T parse(String json, TypeReference<T> typeRef) {
        return jsonNodeConverter.parse(json, typeRef);
    }

    /**
     * Converts a JsonNode to a pretty-printed JSON string.
     * 
     * @param jsonNode the JsonNode to convert
     * @return the pretty-printed JSON string
     */
    public static String prettyPrinter(JsonNode jsonNode) {
        return jsonNodeConverter.prettyPrinter(jsonNode);
    }
}
