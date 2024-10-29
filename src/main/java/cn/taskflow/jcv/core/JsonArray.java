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
import cn.taskflow.jcv.utils.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an array parameter type in JSON schema.
 * This class is used to define the structure and validation rules for JSON arrays.
 * It extends the JsonBasicSchema class to inherit common schema properties.
 * 
 * @author KEVIN LUAN
 */
public class JsonArray extends JsonBasicSchema {

    /**
     * Default constructor for JsonArray.
     */
    public JsonArray() {
    }

    /**
     * Constructs a JsonArray with specified properties.
     *
     * @param name        the name of the array
     * @param required    whether the array is required
     * @param description a description of the array
     * @param child       the schema of the child element
     */
    public JsonArray(String name, boolean required, String description, JsonBasicSchema child) {
        super(name, required, DataType.Array, description);
        check(child);
        if (child != null) {
            this.children = new JsonBasicSchema[] { child };
        }
    }

    /**
     * Validates the child schema.
     * Ensures that the child schema is not an array and does not have a name.
     *
     * @param childrens the child schema to validate
     * @throws ValidationException if the child schema is invalid
     */
    private void check(JsonSchema childrens) {
        if (childrens != null) {
            if (childrens.getDataType() == DataType.Array) {
                throw new ValidationException("illegal parameter", childrens.getPath());
            } else {
                if (StringUtils.isNotBlank(childrens.getName())) {
                    throw new ValidationException("parameter error", childrens.getPath());
                }
            }
        }
    }

    /**
     * Creates a required JsonArray with a specified name, description, and child schema.
     *
     * @param name        the name of the array
     * @param description a description of the array
     * @param childrens   the schema of the child element
     * @return a new instance of JsonArray
     */
    public static JsonArray required(String name, String description, JsonBasicSchema childrens) {
        return new JsonArray(name, true, description, childrens);
    }

    /**
     * Creates a required JsonArray with a specified name and child schema.
     *
     * @param name  the name of the array
     * @param child the schema of the child element
     * @return a new instance of JsonArray
     */
    public static JsonArray required(String name, JsonBasicSchema child) {
        return new JsonArray(name, true, null, child);
    }

    /**
     * Creates a required JsonArray with a specified name.
     * The array can have any type of child node.
     *
     * @param name the name of the array
     * @return a new instance of JsonArray
     */
    public static JsonArray required(String name) {
        return new JsonArray(name, true, null, null);
    }

    /**
     * Creates a required JsonArray with a specified name and description.
     *
     * @param name        the name of the array
     * @param description a description of the array
     * @return a new instance of JsonArray
     */
    public static JsonArray required(String name, String description) {
        return new JsonArray(name, true, description, null);
    }

    /**
     * Creates an optional JsonArray with a specified name, description, and child schema.
     *
     * @param name        the name of the array
     * @param description a description of the array
     * @param child       the schema of the child element
     * @return a new instance of JsonArray
     */
    public static JsonArray optional(String name, String description, JsonBasicSchema child) {
        return new JsonArray(name, false, description, child);
    }

    /**
     * Creates an optional JsonArray with a specified name and child schema.
     *
     * @param name  the name of the array
     * @param child the schema of the child element
     * @return a new instance of JsonArray
     */
    public static JsonArray optional(String name, JsonBasicSchema child) {
        return new JsonArray(name, false, null, child);
    }

    /**
     * Creates an optional JsonArray with a specified name.
     * The array can have any type of child node.
     *
     * @param name the name of the array
     * @return a new instance of JsonArray
     */
    public static JsonArray optional(String name) {
        return new JsonArray(name, false, null, null);
    }

    /**
     * Creates an optional JsonArray with a specified name and description.
     *
     * @param name        the name of the array
     * @param description a description of the array
     * @return a new instance of JsonArray
     */
    public static JsonArray optional(String name, String description) {
        return new JsonArray(name, false, description, null);
    }

    /**
     * Returns this instance as a JsonArray.
     *
     * @return this JsonArray instance
     */
    @Override
    public JsonArray asArray() {
        return this;
    }

    /**
     * Retrieves the child schemas of this JsonArray.
     *
     * @return an array of child schemas
     */
    public JsonSchema[] getChildren() {
        return children;
    }

    /**
     * Retrieves the schema for the first child, if it exists.
     *
     * @return an Optional containing the first child schema, or empty if no children exist
     */
    public final Optional<JsonSchema> getSchemaForFirstChildren() {
        if (existsChildren()) {
            return Optional.of(children[0]);
        }
        return Optional.empty();
    }

    /**
     * Checks if this JsonArray has any child schemas.
     *
     * @return true if there are child schemas, false otherwise
     */
    public final boolean existsChildren() {
        return this.children != null && children.length > 0;
    }
}
