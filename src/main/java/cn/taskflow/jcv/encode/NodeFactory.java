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
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class NodeFactory {
    private static JsonNodeConverter jsonNodeConverter = new DefaultJsonNodeConverter(JacksonEncoder.mapper);

    public static void setJsonNodeConverter(JsonNodeConverter jsonNodeConverter) {
        NodeFactory.jsonNodeConverter = jsonNodeConverter;
    }

    public static boolean isNull(JsonNode jsonNode) {
        return jsonNodeConverter.isNull(jsonNode);
    }

    public static String toString(Object value) {
        return jsonNodeConverter.toString(value);
    }

    public static String toString(JsonNode node) {
        return jsonNodeConverter.toString(node);
    }

    public static Object parserValue(JsonNode node) {
        return jsonNodeConverter.parserValue(node);
    }

    public static Map<String, Object> parserMap(ObjectNode node) {
        return jsonNodeConverter.parserMap(node);
    }

    public static List<Object> parserArrayNode(ArrayNode arrayNode) {
        return jsonNodeConverter.parserArrayNode(arrayNode);
    }

    public static JsonNode parser(String json) {
        return jsonNodeConverter.parser(json);
    }

    /**
     * 将Java对象转化到JsonNode
     */
    public static JsonNode convert(Object value) {
        return jsonNodeConverter.convert(value);
    }

    public static String stringify(Object pojo) {
        return jsonNodeConverter.stringify(pojo);
    }

    public static <T> T parse(String json, Class<T> type) {
        return jsonNodeConverter.parse(json, type);
    }

    public static <T> T parse(String json, TypeReference<T> typeRef) {
        return jsonNodeConverter.parse(json, typeRef);
    }

    public static String prettyPrinter(JsonNode jsonNode) {
        return jsonNodeConverter.prettyPrinter(jsonNode);
    }
}
