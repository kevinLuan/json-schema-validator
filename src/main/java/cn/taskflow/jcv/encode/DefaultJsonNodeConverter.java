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

public class DefaultJsonNodeConverter implements JsonNodeConverter {
    public final ObjectMapper mapper;

    public DefaultJsonNodeConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public boolean isNull(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull() || jsonNode.isMissingNode()) {
            return true;
        }
        return false;
    }

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
            } else {// 其他类型
                return node.textValue();
            }
        }
    }

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

    public JsonNode parser(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Java对象转化到JsonNode
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

    public String stringify(Object pojo) {
        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T parse(String json, Class<T> a) {
        T result = null;
        try {
            result = mapper.readValue(json, a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public <T> T parse(String json, TypeReference<T> a) {
        T result = null;
        try {
            result = mapper.readValue(json, a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String prettyPrinter(JsonNode jsonNode) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
