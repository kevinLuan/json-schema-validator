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
package cn.taskflow.jcv.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Objects;

/**
 * JSON节点遍历工具类
 * 支持深度优先遍历JSON的任意节点，可以处理对象和数组类型的节点
 *
 * @author KEVIN.LUAN
 * @since 2025-01-05
 */
public class JsonNodeTraverser {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static String       ROOT         = "$";

    /**
     * JSON节点处理器接口
     * 用于在遍历过程中对每个节点进行自定义处理
     */
    public interface JsonNodeHandler {
        /**
         * 处理JSON节点
         *
         * @param path 节点在JSON结构中的完整路径，使用点号分隔对象层级，方括号表示数组索引
         * @param node 当前正在处理的JSON节点
         */
        void handle(String path, JsonNode node);
    }

    /**
     * 遍历JSON字符串
     *
     * @param json    JSON字符串
     * @param handler 节点处理器
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static void traverse(String json, JsonNodeHandler handler) throws JsonProcessingException {
        Objects.requireNonNull(json, "json cannot be null");
        Objects.requireNonNull(handler, "handler cannot be null");
        JsonNode jsonNode = objectMapper.readTree(json);
        traverseNode(jsonNode, ROOT, handler);
    }

    /**
     * 遍历JSON节点
     * 从指定的JSON节点开始进行深度优先遍历
     *
     * @param node    要遍历的JSON节点
     * @param handler 节点处理器
     */
    public static void traverse(JsonNode node, JsonNodeHandler handler) {
        Objects.requireNonNull(node, "node cannot be null");
        Objects.requireNonNull(handler, "handler cannot be null");
        traverseNode(node, ROOT, handler);
    }

    /**
     * 递归遍历JSON节点
     * 采用深度优先遍历策略，支持对象和数组类型的节点处理
     *
     * @param node    当前正在处理的节点
     * @param path    当前节点的路径表示
     * @param handler 节点处理器
     */
    private static void traverseNode(JsonNode node, String path, JsonNodeHandler handler) {
        // 先处理当前节点
        handler.handle(path, node);

        // 处理对象类型节点
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String newPath = path.isEmpty() ? fieldName : path + "." + fieldName;
                traverseNode(objectNode.get(fieldName), newPath, handler);
            }
        }
        // 处理数组类型节点
        else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                String newPath = path + "[" + i + "]";
                traverseNode(arrayNode.get(i), newPath, handler);
            }
        }
    }

}
