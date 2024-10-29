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
 * Interface for processing schema requirements and options.
 * Provides methods to determine if a schema element is required or optional.
 * 
 * @autor SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public interface SchemaProcess {

    /**
     * Determines if a schema element is required.
     *
     * @param name the name of the schema element
     * @param type the data type of the schema element
     * @param valueSchema the JSON schema associated with the element
     * @return true if the element is required, false otherwise
     */
    boolean isRequired(String name, DataType type, JsonSchema valueSchema);

    /**
     * Determines if a schema element is required for multiple JSON schemas.
     *
     * @param name the name of the schema element
     * @param type the data type of the schema element
     * @param valueSchemas the array of JSON schemas associated with the element
     * @return true if the element is required in any of the provided schemas, false otherwise
     */
    boolean isRequired(String name, DataType type, JsonSchema... valueSchemas);

    /**
     * Determines if a schema element is required for a primitive value.
     *
     * @param name the name of the schema element
     * @param type the data type of the schema element
     * @param valueSchema the primitive schema associated with the element
     * @return true if the element is required, false otherwise
     */
    boolean isRequired(String name, DataType type, Primitive valueSchema);

    /**
     * Determines if a schema element is optional.
     *
     * @param name the name of the schema element
     * @param dataType the data type of the schema element
     * @return true if the element is optional, false otherwise
     */
    boolean isOptional(String name, DataType dataType);
}
