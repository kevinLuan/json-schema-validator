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
package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.JsonBasicSchema;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.encode.GsonEncoder;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-04-18
 */
public class Encoder {
    /**
     * 序列化参数定义
     *
     * @param jsonSchema
     * @return
     */
    public static String encode(JsonSchema jsonSchema) {
        return GsonEncoder.INSTANCE.encode(jsonSchema);
    }

    /**
     * 根据参数定义反序列化
     *
     * @param paramJson
     * @return
     */
    public static JsonSchema decode(String paramJson) {
        return GsonEncoder.INSTANCE.decode(paramJson, JsonBasicSchema.class);
    }
}
