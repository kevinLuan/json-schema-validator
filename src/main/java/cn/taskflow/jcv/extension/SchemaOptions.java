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
 * @author SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public enum SchemaOptions {
    REQUIRED, OPTIONAL;

    public boolean isRequired() {
        return this == REQUIRED;
    }

    public boolean isOptional() {
        return this == OPTIONAL;
    }

    public SchemaProcess getSchemaProcess() {
        return new SchemaProcess() {
            @Override
            public boolean isRequired(String name, DataType type, JsonSchema valueSchema) {
                return SchemaOptions.this.isRequired();
            }

            @Override
            public boolean isRequired(String name, DataType type, JsonSchema... valueSchemas) {
                return SchemaOptions.this.isRequired();
            }

            @Override
            public boolean isRequired(String name, DataType type, Primitive valueSchema) {
                return SchemaOptions.this.isRequired();
            }

            @Override
            public boolean isOptional(String name, DataType dataType) {
                return SchemaOptions.this.isOptional();
            }
        };
    }
}
