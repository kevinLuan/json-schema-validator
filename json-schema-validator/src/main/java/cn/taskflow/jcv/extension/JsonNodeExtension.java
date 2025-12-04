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
package cn.taskflow.jcv.extension;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The ExtendNode enum represents different types of nodes that can be extended.
 * Each enum constant provides a specific implementation of the matchesType method
 * to determine if a given JsonNode matches the expected type.
 */
public enum JsonNodeExtension {
    /**
     * Represents a remark node, which is expected to be a textual JsonNode
     * with a maximum length of 500 characters.
     */
    remark("remark") {
        @Override
        public boolean matchesType(JsonNode jsonNode) {
            if (jsonNode != null && jsonNode.isTextual()) {
                return jsonNode.textValue().length() <= 500;
            }
            return false;
        }
    },
    /**
     * Represents an extendProps node, which is expected to be a JsonNode
     * of object type.
     */
    extendProps("extendProps") {
        @Override
        public boolean matchesType(JsonNode jsonNode) {
            return jsonNode != null && jsonNode.isObject();
        }
    };

    /**
     * The name of the node type.
     */
    private String name;

    /**
     * Constructor for the ExtendNode enum.
     *
     * @param name The name of the node type.
     */
    private JsonNodeExtension(String name) {
        this.name = name();
    }

    /**
     * Gets the name of the node type.
     *
     * @return The name of the node type.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if a given name corresponds to a defined ExtendNode.
     *
     * @param name The name to check.
     * @return True if the name corresponds to a defined ExtendNode, false otherwise.
     */
    public static boolean isDefinition(String name) {
        for (JsonNodeExtension node : values()) {
            if (node.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the ExtendNode corresponding to a given name.
     *
     * @param name The name of the node type.
     * @return The ExtendNode corresponding to the name, or null if not found.
     */
    public static JsonNodeExtension getDefinition(String name) {
        for (JsonNodeExtension node : values()) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Abstract method to be implemented by each enum constant to determine
     * if a given JsonNode matches the expected type.
     *
     * @param jsonNode The JsonNode to check.
     * @return True if the JsonNode matches the expected type, false otherwise.
     */
    public abstract boolean matchesType(JsonNode jsonNode);
}
