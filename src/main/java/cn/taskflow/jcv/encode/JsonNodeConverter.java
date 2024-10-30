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
 * 用于在 Java 对象和 JsonNode 表示之间进行转换的接口。
 * 提供解析、转换和字符串化 JSON 数据的方法。
 * 
 * @autor SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public interface JsonNodeConverter {

    /**
     * 检查给定的 JsonNode 是否为 null。
     * 
     * @param jsonNode 要检查的 JsonNode
     * @return 如果 JsonNode 为 null，则返回 true，否则返回 false
     */
    boolean isNull(JsonNode jsonNode);

    /**
     * 将对象转换为其 JSON 字符串表示。
     * 
     * @param value 要转换的对象
     * @return 对象的 JSON 字符串表示
     */
    String toString(Object value);

    /**
     * 将 JsonNode 转换为其字符串表示。
     * 
     * @param node 要转换的 JsonNode
     * @return JsonNode 的字符串表示
     */
    String toString(JsonNode node);

    /**
     * 解析 JsonNode 以提取其值作为对象。
     * 
     * @param node 要解析的 JsonNode
     * @return 提取的对象值
     */
    Object parserValue(JsonNode node);

    /**
     * 解析 ObjectNode 以将其值提取到 Map 中。
     * 
     * @param node 要解析的 ObjectNode
     * @return 包含提取值的 Map
     */
    Map<String, Object> parserMap(ObjectNode node);

    /**
     * 解析 ArrayNode 以将其元素提取到 List 中。
     * 
     * @param arrayNode 要解析的 ArrayNode
     * @return 包含提取元素的 List
     */
    List<Object> parserArrayNode(ArrayNode arrayNode);

    /**
     * 解析 JSON 字符串以创建 JsonNode。
     * 
     * @param json 要解析的 JSON 字符串
     * @return 生成的 JsonNode
     */
    JsonNode parser(String json);

    /**
     * 将 Java 对象转换为 JsonNode。
     * 
     * @param value 要转换的 Java 对象
     * @return 生成的 JsonNode
     */
    JsonNode convert(Object value);

    /**
     * 将 Java 对象转换为其 JSON 字符串表示。
     * 
     * @param pojo 要转换的 Java 对象
     * @return 对象的 JSON 字符串表示
     */
    String stringify(Object pojo);

    /**
     * 将 JSON 字符串解析为指定类类型的对象。
     * 
     * @param <T> 对象的类型
     * @param json 要解析的 JSON 字符串
     * @param a 对象的类
     * @return 解析后的对象
     */
    <T> T parse(String json, Class<T> a);

    /**
     * 将 JSON 字符串解析为指定类型引用的对象。
     * 
     * @param <T> 对象的类型
     * @param json 要解析的 JSON 字符串
     * @param a 对象的类型引用
     * @return 解析后的对象
     */
    <T> T parse(String json, TypeReference<T> a);

    /**
     * 将 JsonNode 转换为格式化的 JSON 字符串。
     * 
     * @param jsonNode 要转换的 JsonNode
     * @return 格式化的 JSON 字符串
     */
    String prettyPrinter(JsonNode jsonNode);
}
