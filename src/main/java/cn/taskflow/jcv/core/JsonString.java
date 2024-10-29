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
import cn.taskflow.jcv.validation.EnumValidation;

import java.util.Objects;

/**
 * Represents a JSON string type within the validation framework.
 * This class extends the Primitive class to provide additional functionality
 * specific to JSON string handling.
 * 
 * @autor SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonString extends Primitive {
    /**
     * Constructs a JsonString object with the specified parameters.
     *
     * @param name        the name of the JSON string
     * @param require     whether the JSON string is required
     * @param dataType    the data type of the JSON string
     * @param description a description of the JSON string
     * @param min         the minimum value constraint (not applicable for strings)
     * @param max         the maximum value constraint (not applicable for strings)
     */
    public JsonString(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * Creates a required JsonString parameter.
     *
     * @param name the name of the JSON string
     * @return a new instance of JsonString marked as required
     */
    public static JsonString required(String name) {
        return new JsonString(name, true, DataType.String, null, null, null);
    }

    /**
     * Creates a required JsonString parameter with a description.
     *
     * @param name        the name of the JSON string
     * @param description a description of the JSON string
     * @return a new instance of JsonString marked as required with a description
     */
    public static JsonString required(String name, String description) {
        return new JsonString(name, true, DataType.String, description, null, null);
    }

    /**
     * Creates a non-null JsonString parameter.
     * This basic type can only be used when the parent node is an Array, e.g., array[0,1,2].
     *
     * @return a new instance of JsonString marked as non-null
     */
    public static JsonString ofNonNull() {
        return new JsonString("", true, DataType.String, null, null, null);
    }

    /**
     * Creates a nullable JsonString parameter.
     *
     * @return a new instance of JsonString marked as nullable
     */
    public static JsonString ofNullable() {
        return new JsonString("", false, DataType.String, null, null, null);
    }

    /**
     * Creates an optional JsonString parameter with a description.
     *
     * @param name        the name of the JSON string
     * @param description a description of the JSON string
     * @return a new instance of JsonString marked as optional with a description
     */
    public static JsonString optional(String name, String description) {
        return new JsonString(name, false, DataType.String, description, null, null);
    }

    /**
     * Creates an optional JsonString parameter.
     *
     * @param name the name of the JSON string
     * @return a new instance of JsonString marked as optional
     */
    public static JsonString optional(String name) {
        return new JsonString(name, false, DataType.String, null, null, null);
    }

    /**
     * Specifies that the value of this JsonString must be within the given enum range.
     *
     * @param enumClass the enum class to validate against
     * @param <E>       the type of the enum
     * @return this JsonString instance, for method chaining
     */
    public <E extends Enum<E>> JsonString inEnum(Class<E> enumClass) {
        return this.withValidator((schema, node) -> {
            if (node == null || node.isNull()) {
                return true; // Supports optional fields, null is valid
            }
            try {
                Enum.valueOf(enumClass, node.asText());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        });
    }

    /**
     * Specifies that the value of this JsonString must be within the given array of enum values.
     *
     * @param values the array of enum values to validate against
     * @param <E>    the type of the enum
     * @return this JsonString instance, for method chaining
     * @throws IllegalArgumentException if the values array is null or empty
     */
    public <E extends Enum<E>> JsonString inEnum(E[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("The parameter cannot be null or empty");
        }
        return withValidator(EnumValidation.of(values));
    }
}
