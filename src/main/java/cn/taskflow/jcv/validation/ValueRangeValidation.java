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

import java.util.*;

import static cn.taskflow.jcv.utils.JsvUtils.f;

public class ValueRangeValidation implements CustomValidationRule {
    private final Set<String> withinValues  = new HashSet<>();
    private final Set<String> excludeValues = new HashSet<>();

    public static ValueRangeValidation fromWithinValues(Object... values) {
        ValueRangeValidation rangeValidation = new ValueRangeValidation();
        for (Object value : values) {
            rangeValidation.withinValues.add(value.toString());
        }
        return rangeValidation;
    }

    public static ValueRangeValidation fromExcludeValues(Object... values) {
        ValueRangeValidation rangeValidation = new ValueRangeValidation();
        for (Object value : values) {
            rangeValidation.excludeValues.add(value.toString());
        }
        return rangeValidation;
    }

    /**
     * 添加数据限定范围
     *
     * @param values
     * @return
     */
    public ValueRangeValidation addWithinValues(Object... values) {
        for (Object value : values) {
            this.withinValues.add(value.toString());
        }
        return this;
    }

    /**
     * 添加排除范围
     *
     * @param values
     * @return
     */
    public ValueRangeValidation addExcludeValues(Object... values) {
        for (Object value : values) {
            this.excludeValues.add(value.toString());
        }
        return this;
    }

    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        if (node != null && !node.isNull()) {
            String value = node.asText();
            if (withinValues.size() > 0 && !withinValues.contains(value)) {
                String path = schema.getPath();
                if (path != null && path.length() > 0) {
                    throw new ValidationException(f("The parameter field:'%s' is not in the definition scope",
                        schema.getPath()), schema.getPath()).append(schema, node);
                } else {
                    throw new IllegalArgumentException("The parameter is not in the definition scope");
                }
            }
            if (excludeValues.size() > 0 && excludeValues.contains(value)) {
                String path = schema.getPath();
                if (path != null && path.length() > 0) {
                    throw new ValidationException(f("The parameter field:'%s' is out of the legal range",
                        schema.getPath()), schema.getPath()).append(schema, node);
                } else {
                    throw new IllegalArgumentException("The parameter is out of the legal range");
                }
            }
        }
        return true;
    }
}
