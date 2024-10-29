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
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for defining JSON schema parameters.
 * This interface provides methods to interact with and validate JSON schema elements.
 * It supports operations for checking types, converting types, and setting or getting parent nodes.
 * It also includes methods for custom validation of JSON nodes.
 * 
 * Author: KEVIN LUAN
 */
public interface JsonSchema {
    Logger LOG = LoggerFactory.getLogger(JsonSchema.class);

    /**
     * Retrieves the name of the parameter.
     *
     * @return the name of the parameter
     */
    String getName();

    /**
     * Checks if the parameter is of a primitive type.
     *
     * @return true if the parameter is primitive, false otherwise
     */
    boolean isPrimitive();

    /**
     * Converts the parameter to a Primitive type.
     *
     * @return the parameter as a Primitive
     */
    Primitive asPrimitive();

    /**
     * Checks if the parameter is implemented as a {@link JsonArray}.
     *
     * @return true if the parameter is a JsonArray, false otherwise
     */
    boolean isArray();

    /**
     * Checks if the parameter is implemented as an ObjectParam.
     *
     * @return true if the parameter is an ObjectParam, false otherwise
     */
    boolean isObject();

    /**
     * Converts the parameter to a JsonArray type.
     *
     * @return the parameter as a JsonArray
     */
    JsonArray asArray();

    /**
     * Converts the parameter to a JsonObject type.
     *
     * @return the parameter as a JsonObject
     */
    JsonObject asObject();

    /**
     * Sets the parent node for the current node.
     *
     * @param parentNode the parent node to set
     */
    void setParentNode(JsonSchema parentNode);

    /**
     * Retrieves the parent node of the current node.
     *
     * @return the parent node
     */
    JsonSchema getParentNode();

    /**
     * Checks if the current node is the root node.
     *
     * @return true if it is the root node, false otherwise
     */
    boolean isRootNode();

    /**
     * Checks if the parameter is an ObjectNode value type (e.g., Array[{ObjectNode},{ObjectNode}]).
     *
     * @return true if it is an ObjectNode value type, false otherwise
     */
    boolean isObjectValue();

    /**
     * Retrieves the data type of the parameter.
     *
     * @return the data type
     */
    DataType getDataType();

    /**
     * Checks if the parameter is required.
     *
     * @return true if the parameter is required, false otherwise
     */
    boolean isRequired();

    /**
     * Retrieves the path of the parameter.
     *
     * @return the path as a String
     */
    String getPath();

    /**
     * Performs custom validation on the given JSON node.
     *
     * @param jsonNode the JSON node to validate
     * @throws ValidationException if validation fails
     */
    void verify(JsonNode jsonNode) throws ValidationException;
}
