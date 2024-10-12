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
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.JsvUtils;
import cn.taskflow.jcv.utils.StringUtils;
import cn.taskflow.jcv.validation.CustomValidationRule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.taskflow.jcv.utils.NodeHelper;
import cn.taskflow.jcv.encode.GsonEncoder;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class JsonBasicSchema implements JsonSchema {
    private String                                    name;
    private boolean                                   required;
    private DataType                                  dataType;
    private String                                    description;
    // 父亲节点
    public transient JsonSchema                       parentNode;

    // 子节点(ParamArray,ParamObject)
    JsonBasicSchema[]                                 children               = new JsonBasicSchema[0];
    // 限制最小输入值(Primitive)
    Number                                            min;
    // 限制最大输入值(Primitive)
    Number                                            max;
    /**
     * 示例值(只有Primitive类型节点才会有效)
     */
    String                                            exampleValue;
    @JsonIgnore
    transient volatile Optional<CustomValidationRule> validationRuleOptional = Optional.empty();

    public JsonBasicSchema() {
    }

    public JsonBasicSchema(String name, boolean required, DataType dataType, String description) {
        this.name = name;
        this.required = required;
        this.dataType = dataType;
        this.description = description;
    }

    public JsonBasicSchema(String name, boolean required, DataType dataType) {
        this.name = name;
        this.required = required;
        this.dataType = dataType;
    }

    public <T extends JsonBasicSchema> T withValidator(CustomValidationRule customValidationRule) {
        this.validationRuleOptional = Optional.of(customValidationRule);
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public JsonBasicSchema setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public final void setParentNode(JsonSchema parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    @JsonIgnore
    public final JsonSchema getParentNode() {
        return parentNode;
    }

    public final String getPath() {
        return NodeHelper.parser(this).getPath();
    }

    @Override
    public final boolean isRootNode() {
        return parentNode == null;
    }

    @Override
    public boolean isPrimitive() {
        return DataType.isPrimitive(dataType);
    }

    @Override
    public boolean isArray() {
        return dataType == DataType.Array;
    }

    @Override
    public boolean isObject() {
        return dataType == DataType.Object;
    }

    @Override
    public boolean isObjectValue() {
        if (isObject() && StringUtils.isBlank(getName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public JsonArray asArray() {
        if (isArray()) {
            JsonBasicSchema param = null;
            if (children != null && children.length > 0) {
                param = children[0];
            }
            return new JsonArray(name, required, description, param);
        }
        throw JsvUtils.newClassCastException(this.getClass(), JsonArray.class);
    }

    @Override
    public JsonObject asObject() {
        if (isObject()) {
            return new JsonObject(name, required, description, children);
        }
        throw JsvUtils.newClassCastException(this.getClass(), JsonObject.class);
    }

    @Override
    public Primitive asPrimitive() {
        if (isPrimitive()) {
            return new Primitive(name, required, dataType, description, min, max).setExampleValue(exampleValue);
        }
        throw JsvUtils.newClassCastException(this.getClass(), Primitive.class);
    }

    @Override
    public boolean equals(Object obj) {
        String this_json = GsonEncoder.INSTANCE.encode(this);
        String input_json = GsonEncoder.INSTANCE.encode(obj);
        return this_json.equals(input_json);
    }

    /**
     * 设置示例值
     *
     * @param exampleValue
     * @return
     */
    public JsonBasicSchema setExampleValue(Object exampleValue) {
        this.exampleValue = NodeFactory.stringify(exampleValue);
        return this;
    }

    @Override
    public void verify(JsonNode jsonNode) {
        validationRuleOptional.ifPresent((func) -> {
            if (!func.validate(this, jsonNode)) {
                throw new ValidationException("Invalid parameter `" + getPath() + "`", getPath());
            }
        });
    }
}
