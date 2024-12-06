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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.TimeZone;

/**
 * 这是一个自定义的ObjectMapper类，使用蛇形命名策略。
 *
 * @作者 SHOUSHEN.LUAN
 * @自 2024-10-30
 */
public class CamelCaseObjectMapper extends ObjectMapper {
    /**
     * 构造函数，配置ObjectMapper的属性。
     */
    public CamelCaseObjectMapper() {
        // 默认驼峰命名法
        setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        // 配置ObjectMapper在反序列化时不因未知属性而失败
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 配置ObjectMapper在序列化时不因空Bean而失败
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 设置时区为亚洲/上海
        setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
