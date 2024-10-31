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
package cn.taskflow.jcv.spring;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.validation.Validator;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * JsonSchemaFactory 负责管理和提供对 JSON 模式定义的访问。
 * 它从 Spring 应用程序上下文中检索 JSON 模式 bean，并提供方法来
 * 验证 JSON 数据是否符合这些模式。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
public class JsonSchemaFactory {
    private final Map<String, JsonSchema> schemaMap;

    /**
     * 使用从给定应用程序上下文中检索到的模式构造 JsonSchemaFactory。
     * 模式存储在不可修改的映射中以确保不可变性。
     *
     * @param context 从中检索 JSON 模式 bean 的 Spring 应用程序上下文
     */
    public JsonSchemaFactory(ApplicationContext context) {
        schemaMap = Collections.unmodifiableMap(context.getBeansOfType(JsonSchema.class));
    }

    /**
     * 检索与给定模式名称关联的 JsonSchema 的 Optional。
     * 如果未找到模式，则返回一个空的 Optional。
     *
     * @param schemaName 要检索的模式的名称
     * @return 如果找到则包含 JsonSchema 的 Optional，否则为空的 Optional
     */
    public Optional<JsonSchema> getSchema(String schemaName) {
        return Optional.ofNullable(schemaMap.get(schemaName));
    }

    /**
     * 根据 JsonSchemaValidate 注解指定的模式验证提供的 JSON 数据。
     * 如果未找到模式或验证失败，则抛出 IllegalArgumentException。
     *
     * @param schemaValidate 包含要验证的模式名称的注解
     * @param json 要验证的 JSON 数据
     * @throws IllegalArgumentException 如果未找到模式或验证失败
     */
    public void validate(JsonSchemaValidate schemaValidate, String json) {
        Optional<JsonSchema> optional = getSchema(schemaValidate.value());
        if (optional.isPresent()) {
            try {
                Validator.fromSchema(optional.get()).validate(json);
            } catch (Exception e) {
                if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
                    throw (IllegalArgumentException) e;
                } else {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        } else {
            throw new IllegalArgumentException(
                String.format("schema:'%s' definition not found", schemaValidate.value()));
        }
    }

    public void validate(JsonSchemaValidate jsv, Object body) {
        Optional<JsonSchema> optional = getSchema(jsv.value());
        if (optional.isPresent()) {
            try {
                Validator.fromSchema(optional.get()).validate(body);
            } catch (Exception e) {
                if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
                    throw (IllegalArgumentException) e;
                } else {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("schema:'%s' definition not found", jsv.value()));
        }
    }
}
