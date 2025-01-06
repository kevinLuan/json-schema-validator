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
package cn.taskflow.jcv.validation;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static cn.taskflow.jcv.utils.JsvUtils.f;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-12-20
 */
public class RegexValidation implements CustomValidationRule {
    private final List<Pattern> patterns = new ArrayList<>();

    /**
     * 创建一个新的正则验证规则实例
     *
     * @param regex 正则表达式
     * @return RegexValidation实例
     * @throws PatternSyntaxException 如果正则表达式语法无效
     */
    public static RegexValidation fromRegex(String regex) {
        RegexValidation validation = new RegexValidation();
        validation.addPattern(regex);
        return validation;
    }

    /**
     * 创建一个新的正则验证规则实例
     *
     * @param pattern 已编译的Pattern对象
     * @return RegexValidation实例
     */
    public static RegexValidation fromPattern(Pattern pattern) {
        RegexValidation validation = new RegexValidation();
        validation.patterns.add(pattern);
        return validation;
    }

    /**
     * 添加新的正则表达式模式
     *
     * @param regex 正则表达式字符串
     * @return 当前RegexValidation实例
     * @throws PatternSyntaxException 如果正则表达式语法无效
     */
    public RegexValidation addPattern(String regex) {
        try {
            this.patterns.add(Pattern.compile(regex));
            return this;
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regex, e);
        }
    }

    /**
     * 添加已编译的Pattern对象
     *
     * @param pattern 编译好的Pattern对象
     * @return 当前RegexValidation实例
     */
    public RegexValidation addPattern(Pattern pattern) {
        this.patterns.add(pattern);
        return this;
    }

    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        if (node != null && !node.isNull()) {
            if (!node.isValueNode()) {
                throw new ValidationException(f("The field '%s' must be a string value", schema.getPath()),
                    schema.getPath()).append(schema, node);
            }

            String value = node.asText();
            boolean matchAny = false;
            for (Pattern pattern : patterns) {
                if (pattern.matcher(value).matches()) {
                    matchAny = true;
                    break;
                }
            }

            if (!matchAny) {
                String path = schema.getPath();
                if (path != null && !path.isEmpty()) {
                    throw new ValidationException(f("The field '%s' does not match the required pattern(s)",
                        schema.getPath()), schema.getPath()).append(schema, node);
                } else {
                    throw new IllegalArgumentException("The value does not match the required pattern(s)");
                }
            }
        }
        return true;
    }
}
