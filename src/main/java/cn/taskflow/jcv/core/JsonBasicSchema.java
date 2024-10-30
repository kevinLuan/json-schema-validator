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
/*
 * 根据一个或多个贡献者许可协议，授权给Apache软件基金会（ASF）。请参阅NOTICE文件以获取有关版权归属的其他信息。
 * ASF根据Apache许可证2.0版（“许可证”）授权您使用此文件；除非符合许可证，否则您不得使用此文件。您可以在以下位置获取许可证副本：
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，软件按“原样”分发，不附带任何明示或暗示的担保或条件。
 * 请参阅许可证以了解管理权限和限制的特定语言。
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

/**
 * 表示具有属性和验证功能的基本JSON模式。
 */
public class JsonBasicSchema implements JsonSchema {
    private String                                    name;                                           // The name of the schema element
    private boolean                                   required;                                       // Indicates if the element is required
    private DataType                                  dataType;                                       // The data type of the element
    private String                                    description;                                    // A description of the schema element

    // Parent node in the schema hierarchy
    public transient JsonSchema                       parentNode;

    // Child nodes (for ParamArray, ParamObject)
    JsonBasicSchema[]                                 children               = new JsonBasicSchema[0];

    // Minimum value constraint (for Primitive types)
    Number                                            min;

    // Maximum value constraint (for Primitive types)
    Number                                            max;

    /**
     * 示例值（仅对原始类型节点有效）
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

    /**
     * 将自定义验证规则与此模式关联。
     *
     * @param customValidationRule 要应用的自定义验证规则
     * @return 应用验证器的当前模式实例
     */
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

    /**
     * 检索此模式节点在层次结构中的路径。
     *
     * @return 路径作为字符串
     */
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
        return isObject() && StringUtils.isBlank(getName());
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
     * 为此模式设置示例值。
     *
     * @param exampleValue 要设置的示例值
     * @return 设置了示例值的当前模式实例
     */
    public JsonBasicSchema setExampleValue(Object exampleValue) {
        this.exampleValue = NodeFactory.stringify(exampleValue);
        return this;
    }

    @Override
    public void verify(JsonNode jsonNode) {
        validationRuleOptional.ifPresent((func) -> {
            if (!func.validate(this, jsonNode)) {
                String path = getPath();
                if (StringUtils.isNotBlank(path)) {
                    throw new ValidationException("Invalid parameter `" + path + "`", path);
                } else {
                    throw new IllegalArgumentException("Parameter validation failure");
                }
            }
        });
    }
}
