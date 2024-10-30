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
 * NodeFactory 是一个用于在 Java 对象和 JSON 节点之间进行转换的工具类。
 * 它使用 JsonNodeConverter 来执行转换。
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class NodeFactory {
    // 用于 JSON 操作的静态 JsonNodeConverter 实例
    private static JsonNodeConverter jsonNodeConverter = new DefaultJsonNodeConverter(new CamelCaseObjectMapper());

    /**
     * 设置自定义的 JsonNodeConverter。
     * 
     * @param jsonNodeConverter 要使用的 JsonNodeConverter
     */
    public static void setJsonNodeConverter(JsonNodeConverter jsonNodeConverter) {
        NodeFactory.jsonNodeConverter = jsonNodeConverter;
    }

    /**
     * 检查给定的 JsonNode 是否为 null。
     * 
     * @param jsonNode 要检查的 JsonNode
     * @return 如果节点为 null 则返回 true，否则返回 false
     */
    public static boolean isNull(JsonNode jsonNode) {
        return jsonNodeConverter.isNull(jsonNode);
    }

    /**
     * 将对象转换为其 JSON 字符串表示形式。
     * 
     * @param value 要转换的对象
     * @return 对象的 JSON 字符串表示形式
     */
    public static String toString(Object value) {
        return jsonNodeConverter.toString(value);
    }

    /**
     * 将 JsonNode 转换为其 JSON 字符串表示形式。
     * 
     * @param node 要转换的 JsonNode
     * @return 节点的 JSON 字符串表示形式
     */
    public static String toString(JsonNode node) {
        return jsonNodeConverter.toString(node);
    }

    /**
     * 解析 JsonNode 以提取其值为 Java 对象。
     * 
     * @param node 要解析的 JsonNode
     * @return 提取的 Java 对象
     */
    public static Object parserValue(JsonNode node) {
        return jsonNodeConverter.parserValue(node);
    }

    /**
     * 解析 ObjectNode 以将其值提取到 Map 中。
     * 
     * @param node 要解析的 ObjectNode
     * @return 包含节点键值对的 Map
     */
    public static Map<String, Object> parserMap(ObjectNode node) {
        return jsonNodeConverter.parserMap(node);
    }

    /**
     * 解析 ArrayNode 以将其元素提取到 List 中。
     * 
     * @param arrayNode 要解析的 ArrayNode
     * @return 包含数组节点元素的 List
     */
    public static List<Object> parserArrayNode(ArrayNode arrayNode) {
        return jsonNodeConverter.parserArrayNode(arrayNode);
    }

    /**
     * 将 JSON 字符串解析为 JsonNode。
     * 
     * @param json 要解析的 JSON 字符串
     * @return 生成的 JsonNode
     */
    public static JsonNode parser(String json) {
        return jsonNodeConverter.parser(json);
    }

    /**
     * 将 Java 对象转换为 JsonNode。
     * 
     * @param value 要转换的 Java 对象
     * @return 生成的 JsonNode
     */
    public static JsonNode convert(Object value) {
        return jsonNodeConverter.convert(value);
    }

    /**
     * 将 Java 对象转换为 JSON 字符串。
     * 
     * @param pojo 要转换的 Java 对象
     * @return 对象的 JSON 字符串表示形式
     */
    public static String stringify(Object pojo) {
        return jsonNodeConverter.stringify(pojo);
    }

    /**
     * 将 JSON 字符串解析为指定类型的 Java 对象。
     * 
     * @param json 要解析的 JSON 字符串
     * @param type 要返回的类型的类
     * @param <T> 所需对象的类型
     * @return 解析后的 Java 对象
     */
    public static <T> T parse(String json, Class<T> type) {
        return jsonNodeConverter.parse(json, type);
    }

    /**
     * 将 JSON 字符串解析为指定类型引用的 Java 对象。
     * 
     * @param json 要解析的 JSON 字符串
     * @param typeRef 所需对象的类型引用
     * @param <T> 所需对象的类型
     * @return 解析后的 Java 对象
     */
    public static <T> T parse(String json, TypeReference<T> typeRef) {
        return jsonNodeConverter.parse(json, typeRef);
    }

    /**
     * 将 JsonNode 转换为格式化的 JSON 字符串。
     * 
     * @param jsonNode 要转换的 JsonNode
     * @return 格式化的 JSON 字符串
     */
    public static String prettyPrinter(JsonNode jsonNode) {
        return jsonNodeConverter.prettyPrinter(jsonNode);
    }
}
