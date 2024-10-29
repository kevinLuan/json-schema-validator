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

/**
 * Defines the JsonAny type, which extends the JsonBasicSchema class.
 * This class represents a JSON schema type that can accept any data type.
 * It provides constructors and methods to specify whether the schema is required or optional.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-08-27
 */
public class JsonAny extends JsonBasicSchema {

    /**
     * Default constructor for JsonAny.
     */
    public JsonAny() {
    }

    /**
     * Constructs a JsonAny object with the specified name, requirement status, and description.
     *
     * @param name        the name of the JSON schema
     * @param required    a boolean indicating if the schema is required
     * @param description a description of the JSON schema
     */
    public JsonAny(String name, boolean required, String description) {
        super(name, required, DataType.Any, description);
    }

    /**
     * Creates a required JsonAny schema with the specified name and description.
     *
     * @param name        the name of the JSON schema
     * @param description a description of the JSON schema
     * @return a new instance of JsonAny marked as required
     */
    public static JsonAny required(String name, String description) {
        return new JsonAny(name, true, description);
    }

    /**
     * Creates an optional JsonAny schema with the specified name and description.
     *
     * @param name        the name of the JSON schema
     * @param description a description of the JSON schema
     * @return a new instance of JsonAny marked as optional
     */
    public static JsonAny optional(String name, String description) {
        return new JsonAny(name, false, description);
    }

    /**
     * Retrieves the child schemas of this JsonAny instance.
     *
     * @return an array of JsonSchema objects representing the children
     */
    public JsonSchema[] getChildren() {
        return children;
    }

    /**
     * Sets the child schemas for this JsonAny instance.
     *
     * @param children an array of JsonBasicSchema objects to be set as children
     */
    public void setChildren(JsonBasicSchema[] children) {
        super.children = children;
    }
}
