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

import com.fasterxml.jackson.databind.*;

import java.util.TimeZone;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class SnakeCaseObjectMapper extends ObjectMapper {
    public SnakeCaseObjectMapper() {
        // 默认下划线命名法
        setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        // 配置反序列化时忽略未知属性
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置ObjectMapper在序列化时不因空Bean而失败
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 设置时区为亚洲/上海
        setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}