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

import cn.taskflow.jcv.utils.JsvUtils;

/**
 * Represents a JSON object node parameter.
 * This class extends JsonBasicSchema to provide additional functionality specific to JSON objects.
 * It allows for the creation of required or optional JSON objects with specified child schemas.
 * 
 * @author KEVIN LUAN
 */
public class JsonObject extends JsonBasicSchema {

    /**
     * Default constructor for JsonObject.
     * Initializes a new instance of the JsonObject class.
     */
    JsonObject() {
        super();
    }

    /**
     * Constructs a JsonObject with specified parameters.
     * 
     * @param name        The name of the JSON object.
     * @param required    Indicates if the JSON object is required.
     * @param description A description of the JSON object.
     * @param children    An array of child JsonSchema objects.
     * @throws IllegalArgumentException if any child schema is an object value.
     */
    public JsonObject(String name, boolean required, String description, JsonSchema[] children) {
        super(name, required, DataType.Object, description);
        if (children != null) {
            this.children = new JsonBasicSchema[children.length];
            for (int i = 0; i < children.length; i++) {
                JsonSchema jsonSchema = children[i];
                this.children[i] = (JsonBasicSchema) jsonSchema;
                if (jsonSchema.isObjectValue()) {
                    throw JsvUtils.throwParamException(jsonSchema.getPath());
                }
            }
        }
    }

    /**
     * Creates a required JsonObject with a name, description, and child schemas.
     * 
     * @param name        The name of the JSON object.
     * @param description A description of the JSON object.
     * @param children    An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject required(String name, String description, JsonSchema... children) {
        return new JsonObject(name, true, description, children);
    }

    /**
     * Creates a required JsonObject with a name and child schemas.
     * 
     * @param name     The name of the JSON object.
     * @param children An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject required(String name, JsonSchema... children) {
        return new JsonObject(name, true, null, children);
    }

    /**
     * Creates a required JsonObject with child schemas.
     * 
     * @param children An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject required(JsonSchema... children) {
        return new JsonObject("", true, null, children);
    }

    /**
     * Creates an optional JsonObject with child schemas.
     * 
     * @param children An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject optional(JsonSchema... children) {
        return new JsonObject("", false, null, children);
    }

    /**
     * Creates an optional JsonObject with a name, description, and child schemas.
     * 
     * @param name        The name of the JSON object.
     * @param description A description of the JSON object.
     * @param children    An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject optional(String name, String description, JsonSchema... children) {
        return new JsonObject(name, false, description, children);
    }

    /**
     * Creates an optional JsonObject with a name and child schemas.
     * 
     * @param name     The name of the JSON object.
     * @param children An array of child JsonSchema objects.
     * @return A new instance of JsonObject.
     */
    public static JsonObject optional(String name, JsonSchema... children) {
        return new JsonObject(name, false, null, children);
    }

    /**
     * Checks if this schema represents a JSON object.
     * 
     * @return true, as this is a JsonObject.
     */
    public boolean isObject() {
        return true;
    }

    /**
     * Returns this JsonObject instance.
     * 
     * @return This JsonObject instance.
     */
    @Override
    public JsonObject asObject() {
        return this;
    }

    /**
     * Checks if this JsonObject has any child schemas.
     * 
     * @return true if there are child schemas, false otherwise.
     */
    public final boolean existsChildren() {
        return this.children != null && children.length > 0;
    }

    /**
     * Gets the child schemas of this JsonObject.
     * 
     * @return An array of child JsonSchema objects.
     */
    public JsonSchema[] getChildren() {
        return children;
    }
}
