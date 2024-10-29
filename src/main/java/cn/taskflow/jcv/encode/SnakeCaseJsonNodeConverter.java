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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The SnakeCaseJsonNodeConverter class is responsible for converting JSON nodes
 * to a format where all keys are in snake_case. This is useful for systems that
 * require consistent key formatting, especially when interfacing with databases
 * or APIs that use snake_case naming conventions.
 * 
 * This class extends the DefaultJsonNodeConverter, inheriting its basic JSON
 * conversion functionalities, and adds specific logic to transform key names
 * to snake_case.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class SnakeCaseJsonNodeConverter extends DefaultJsonNodeConverter {
    /**
     * Constructs a new SnakeCaseJsonNodeConverter with the specified ObjectMapper.
     * The ObjectMapper is used for JSON parsing and generation, allowing for
     * customization of the JSON processing.
     *
     * @param mapper the ObjectMapper to be used for JSON operations
     */
    public SnakeCaseJsonNodeConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
