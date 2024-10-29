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

import cn.taskflow.jcv.core.DataType;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.Primitive;

/**
 * Enum representing schema options for JSON validation.
 * It defines whether a schema is REQUIRED or OPTIONAL.
 * 
 * Each option provides methods to check its state and to obtain a SchemaProcess
 * that can be used to determine the requirement status of schema elements.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public enum SchemaOptions {
    REQUIRED, OPTIONAL;

    /**
     * Checks if the current option is REQUIRED.
     * 
     * @return true if the option is REQUIRED, false otherwise.
     */
    public boolean isRequired() {
        return this == REQUIRED;
    }

    /**
     * Checks if the current option is OPTIONAL.
     * 
     * @return true if the option is OPTIONAL, false otherwise.
     */
    public boolean isOptional() {
        return this == OPTIONAL;
    }

    /**
     * Provides a SchemaProcess instance that uses the current option to determine
     * the requirement status of schema elements.
     * 
     * @return a SchemaProcess instance with methods to check if elements are required or optional.
     */
    public SchemaProcess getSchemaProcess() {
        return new SchemaProcess() {
            /**
             * Determines if a schema element is required based on the current option.
             * 
             * @param name the name of the schema element.
             * @param type the data type of the schema element.
             * @param valueSchema the JSON schema of the element.
             * @return true if the element is required, false otherwise.
             */
            @Override
            public boolean isRequired(String name, DataType type, JsonSchema valueSchema) {
                return SchemaOptions.this.isRequired();
            }

            /**
             * Determines if multiple schema elements are required based on the current option.
             * 
             * @param name the name of the schema element.
             * @param type the data type of the schema element.
             * @param valueSchemas the JSON schemas of the elements.
             * @return true if the elements are required, false otherwise.
             */
            @Override
            public boolean isRequired(String name, DataType type, JsonSchema... valueSchemas) {
                return SchemaOptions.this.isRequired();
            }

            /**
             * Determines if a primitive schema element is required based on the current option.
             * 
             * @param name the name of the schema element.
             * @param type the data type of the schema element.
             * @param valueSchema the primitive schema of the element.
             * @return true if the element is required, false otherwise.
             */
            @Override
            public boolean isRequired(String name, DataType type, Primitive valueSchema) {
                return SchemaOptions.this.isRequired();
            }

            /**
             * Determines if a schema element is optional based on the current option.
             * 
             * @param name the name of the schema element.
             * @param dataType the data type of the schema element.
             * @return true if the element is optional, false otherwise.
             */
            @Override
            public boolean isOptional(String name, DataType dataType) {
                return SchemaOptions.this.isOptional();
            }
        };
    }
}
