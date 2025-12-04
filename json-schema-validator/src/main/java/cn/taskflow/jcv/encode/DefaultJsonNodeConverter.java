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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * DefaultJsonNodeConverter 是一个用于在 Java 对象和 JsonNode 对象之间进行转换的实用类。
 * 它提供了解析 JSON 字符串、将 Java 对象转换为 JsonNode 以及处理各种 JSON 节点类型的方法。
 */
public class DefaultJsonNodeConverter implements JsonNodeConverter {
    // 用于 JSON 处理的 ObjectMapper 实例
    public final ObjectMapper mapper;

    /**
     * 构造函数，用给定的 ObjectMapper 初始化 DefaultJsonNodeConverter。
     * 
     * @param mapper 用于 JSON 操作的 ObjectMapper
     */
    public DefaultJsonNodeConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 检查给定的 JsonNode 是否为 null、缺失或显式设置为 null。
     * 
     * @param jsonNode 要检查的 JsonNode
     * @return 如果节点为 null、缺失或显式为 null，则返回 true；否则返回 false
     */
    public boolean isNull(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull() || jsonNode.isMissingNode()) {
            return true;
        }
        return false;
    }

    /**
     * 将对象转换为其字符串表示形式。如果对象是 JsonNode，
     * 则对数组和对象的处理方式不同于值节点。
     * 
     * @param value 要转换的对象
     * @return 对象的字符串表示形式
     */
    public String toString(Object value) {
        if (value != null) {
            if (JsonNode.class.isAssignableFrom(value.getClass())) {
                JsonNode node = (JsonNode) value;
                if (node.isArray() || node.isObject()) {
                    return node.toString();
                } else {
                    return node.asText();
                }
            } else {
                return value.toString();
            }
        }
        return null;
    }

    /**
     * 将 JsonNode 转换为其字符串表示形式，处理不同的节点类型。
     * 
     * @param node 要转换的 JsonNode
     * @return JsonNode 的字符串表示形式
     */
    public String toString(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isValueNode()) {
            if (node.isBoolean()) {
                return String.valueOf(node.asBoolean());
            } else if (node.isBigInteger()) {
                return String.valueOf(node.bigIntegerValue());
            } else if (node.isDouble()) {
                return String.valueOf(node.asDouble());
            } else if (node.isInt()) {
                return String.valueOf(node.intValue());
            } else if (node.isLong()) {
                return String.valueOf(node.asLong());
            } else if (node.isShort()) {
                return String.valueOf(node.shortValue());
            } else {
                return node.asText();
            }
        }
        return node.toString();
    }

    /**
     * 解析 JsonNode 并返回其对应的 Java 对象表示。
     * 
     * @param node 要解析的 JsonNode
     * @return JsonNode 的 Java 对象表示
     */
    public Object parserValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isArray()) {
            return parserArrayNode((ArrayNode) node);
        } else if (node.isObject()) {
            return parserMap((ObjectNode) node);
        } else {
            if (node.isBigDecimal() || node.isBigInteger() || node.isLong()) {
                return node.asLong();
            } else if (node.isFloat() || node.isDouble()) {
                return node.asDouble();
            } else if (node.isInt() || node.isNumber() || node.isShort()) {
                return node.asInt();
            } else if (node.isBoolean()) {
                return node.asBoolean();
            } else if (node.isTextual()) {
                return node.asText();
            } else { // 其他类型
                return node.textValue();
            }
        }
    }

    /**
     * 解析 ObjectNode 并返回其字段的映射表示。
     * 
     * @param node 要解析的 ObjectNode
     * @return 包含 ObjectNode 字段的映射
     */
    public Map<String, Object> parserMap(ObjectNode node) {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> iterable = node.fieldNames();
        while (iterable.hasNext()) {
            String key = iterable.next();
            JsonNode jsonNode = node.get(key);
            if (jsonNode.isValueNode()) {
                map.put(key, parserValue(jsonNode));
            } else if (jsonNode.isArray()) {
                map.put(key, parserArrayNode((ArrayNode) jsonNode));
            } else if (jsonNode.isObject()) {
                map.put(key, parserMap((ObjectNode) jsonNode));
            }
        }
        return map;
    }

    /**
     * 解析 ArrayNode 并返回其元素的列表表示。
     * 
     * @param arrayNode 要解析的 ArrayNode
     * @return 包含 ArrayNode 元素的列表
     */
    public List<Object> parserArrayNode(ArrayNode arrayNode) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode n = arrayNode.get(i);
            if (n.isValueNode()) {
                list.add(parserValue(n));
            } else if (n.isObject()) {
                list.add(parserMap((ObjectNode) n));
            }
        }
        return list;
    }

    /**
     * 解析 JSON 字符串并返回相应的 JsonNode。
     * 
     * @param json 要解析的 JSON 字符串
     * @return JSON 字符串的 JsonNode 表示
     */
    public JsonNode parser(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 Java 对象转换为 JsonNode。
     * 
     * @param value 要转换的 Java 对象
     * @return Java 对象的 JsonNode 表示
     */
    public JsonNode convert(Object value) {
        if (value == null) {
            return null;
        }
        if (JsonNode.class.isAssignableFrom(value.getClass())) {
            return (JsonNode) value;
        }
        String valueStr = stringify(value);
        return NodeFactory.parser(valueStr);
    }

    /**
     * 将 Java 对象转换为其 JSON 字符串表示。
     * 
     * @param pojo 要转换的 Java 对象
     * @return 对象的 JSON 字符串表示
     */
    public String stringify(Object pojo) {
        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类类型的 Java 对象。
     * 
     * @param <T> 所需对象的类型
     * @param json 要解析的 JSON 字符串
     * @param a T 的类
     * @return 解析后的 Java 对象
     */
    public <T> T parse(String json, Class<T> a) {
        T result = null;
        try {
            result = mapper.readValue(json, a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 将 JSON 字符串解析为指定类型引用的 Java 对象。
     * 
     * @param <T> 所需对象的类型
     * @param json 要解析的 JSON 字符串
     * @param a T 的类型引用
     * @return 解析后的 Java 对象
     */
    public <T> T parse(String json, TypeReference<T> a) {
        T result = null;
        try {
            result = mapper.readValue(json, a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 将 JsonNode 转换为格式化的 JSON 字符串。
     * 
     * @param jsonNode 要转换的 JsonNode
     * @return 格式化的 JSON 字符串
     */
    @Override
    public String prettyPrinter(JsonNode jsonNode) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
