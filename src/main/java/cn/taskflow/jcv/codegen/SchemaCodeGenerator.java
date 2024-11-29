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
package cn.taskflow.jcv.codegen;

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.StringUtils;

import java.util.Optional;

/**
 * 将Param转换到JavaCode
 * 该类用于将JsonSchema对象转换为Java代码表示
 *
 * @author KEVIN LUAN
 */
class SchemaCodeGenerator {
    private final static String NEW_LINE = ""; // 定义一个常量用于表示新行

    /**
     * 根据JsonSchema生成相应的Java代码
     *
     * @param jsonSchema 输入的JsonSchema对象
     * @return 生成的Java代码字符串
     */
    public static String generate(JsonSchema jsonSchema) {
        StringBuilder builder = new StringBuilder();
        if (jsonSchema.isArray()) {
            parserArray(jsonSchema.asArray(), builder);
        } else if (jsonSchema.isObject()) {
            parserObject(jsonSchema.asObject(), builder);
        } else if (jsonSchema.isPrimitive()) {
            parserPrimitive(jsonSchema.asPrimitive(), builder);
        }
        return builder + ";";
    }

    /**
     * 根据传入的参数构造者，判断是否应该插入逗号分割符，
     * @param builder 字符串构造者
     * @return 插入返回：true,否则返回：false
     */
    private static boolean shouldAppendComma(StringBuilder builder) {
        // 从后向前遍历，跳过空格
        for (int i = builder.length() - 1; i >= 0; i--) {
            char c = builder.charAt(i);
            // 跳过空格继续向前查找
            if (Character.isWhitespace(c)) {
                continue;
            }
            // 找到第一个非空格字符，判断是否为逗号
            if (c != ',') {
                if (c == '(') {
                    return false;
                } else {
                    builder.append(',');
                    return true;
                }
            }
            break;
        }
        return false;
    }

    /**
     * 解析JsonArray对象并生成相应的Java代码
     *
     * @param array   输入的JsonArray对象
     * @param builder 用于构建Java代码的StringBuilder
     */
    private static void parserArray(JsonArray array, StringBuilder builder) {
        String name = array.getName(); // 获取数组名称
        String description = array.getDescription(); // 获取数组描述
        Optional<JsonSchema> optional = array.getSchemaForFirstChildren(); // 获取第一个子元素的Schema
        boolean includeDesc = CodeGenerationUtils.getOptional().isGenerateDesc() && description != null
                              && !description.isEmpty(); // 判断是否需要生成描述
        if (!optional.isPresent()) {
            builder.append("JsonArray.");
            builder.append(array.isRequired() || CodeGenerationUtils.getOptional().isRequire() ? "required"
                : "optional");
            builder.append("(");
            if (StringUtils.isNotBlank(name)) {
                builder.append(formatParam(name));
            }
            if (includeDesc) {
                shouldAppendComma(builder);
                builder.append(formatParam(description));
            }
            builder.append(")");
        } else {
            JsonBasicSchema children = (JsonBasicSchema) optional.get();
            if (!children.isObject() && !children.isPrimitive()) {
                throw new ValidationException("Unsupported type: " + children, children.getPath());
            }
            builder.append("JsonArray.");
            builder.append(children.isRequired() || CodeGenerationUtils.getOptional().isRequire() ? "required"
                : "optional");
            builder.append("(");
            if (StringUtils.isNotBlank(name)) {
                builder.append(formatParam(name));
            }
            if (CodeGenerationUtils.getOptional().isGenerateDesc() && description != null && !description.isEmpty()) {
                shouldAppendComma(builder);
                builder.append(formatParam(description));
            }

            if (children.isObject()) {
                shouldAppendComma(builder);
                parserObject(children.asObject(), builder);
            } else if (CodeGenerationUtils.getOptional().isGenerateExample() && children.isPrimitive()) {
                String example = children.asPrimitive().getExampleValue();
                if (example != null && example.trim().length() > 0) {
                    String childrenCode = children.getDataType().generatePrimitiveCode(children.isRequired());
                    if (children.getDataType().isNumber() || children.getDataType().isBoolean()) {
                        childrenCode += ".setExampleValue(" + example + ")";
                    } else {
                        childrenCode += ".setExampleValue(\"" + example + "\")";
                    }
                    builder.append(NEW_LINE).append(childrenCode);
                }
            }
            builder.append(")");
        }
    }

    /**
     * 解析JsonObject对象并生成相应的Java代码
     *
     * @param object  输入的JsonObject对象
     * @param builder 用于构建Java代码的StringBuilder
     */
    private static void parserObject(JsonObject object, StringBuilder builder) {
        String name = object.getName(); // 获取对象名称
        String description = object.getDescription(); // 获取对象描述
        JsonSchema[] childrens = object.getChildren(); // 获取对象的子元素
        StringBuilder nodeBuilder = new StringBuilder();
        for (int i = 0; i < childrens.length; i++) {
            JsonSchema jsonSchema = childrens[i];
            if (i != 0) {
                shouldAppendComma(builder);
                newLine(nodeBuilder);
            }
            if (jsonSchema.isArray()) {
                newLine(nodeBuilder);
                shouldAppendComma(nodeBuilder);
                parserArray(jsonSchema.asArray(), nodeBuilder);
            } else if (jsonSchema.isObject()) {
                shouldAppendComma(nodeBuilder);
                parserObject(jsonSchema.asObject(), nodeBuilder);
            } else if (jsonSchema.isPrimitive()) {
                shouldAppendComma(nodeBuilder);
                parserPrimitive(jsonSchema.asPrimitive(), nodeBuilder);
                newLine(nodeBuilder);
            }
        }
        builder.append("JsonObject.");
        builder.append(object.isRequired() || CodeGenerationUtils.getOptional().isRequire() ? "required" : "optional");
        builder.append("(");
        //生成字段名称
        if (name != null && name.length() > 0) {
            builder.append(formatParam(name)).append(",");
        }
        //生成描述
        if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
            builder.append(formatParam(description)).append(",");
        }
        if (StringUtils.isNotBlank(nodeBuilder.toString())) {
            builder.append(remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
        } else {//Object对象下没有任何属性的情况下，删除多余的逗号
            int index = builder.lastIndexOf(",");
            if (index != -1) {//删除最后逗号及以后得元素
                builder.delete(index, builder.length());
            }
            builder.append(NEW_LINE).append(")");
        }
        newLine(builder);
    }

    /**
     * 删除字符串末尾的逗号
     *
     * @param str 输入的字符串
     * @return 删除逗号后的字符串
     */
    private static String remoteLastComma(String str) {
        int lastCommaIndex = str.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            String endWith = str.substring(lastCommaIndex);
            if (endWith.equals(",") || endWith.equals("," + NEW_LINE)) {
                str = str.substring(0, lastCommaIndex);
            }
        }
        return str;
    }

    /**
     * 在StringBuilder末尾添加新行
     *
     * @param builder 输入的StringBuilder
     * @return 添加新行后的StringBuilder内容
     */
    private static String newLine(StringBuilder builder) {
        if (!builder.toString().endsWith(NEW_LINE)) {
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * 解析Primitive对象并生成相应的Java代码
     *
     * @param primitive 输入的Primitive对象
     * @param builder   用于构建Java代码的StringBuilder
     */
    private static void parserPrimitive(Primitive primitive, StringBuilder builder) {
        String name = primitive.getName(); // 获取基本类型名称
        DataType type = primitive.getDataType(); // 获取数据类型
        String description = primitive.getDescription(); // 获取描述
        if (name.length() > 0) {
            builder.append(type.generatePrimitiveCode(primitive.isRequired(), name, description));
        } else {
            builder.append(type.generatePrimitiveCode(primitive.isRequired()));
        }
        builder.append(buildExampleValue(primitive));
    }

    /**
     * 构建示例值的代码
     *
     * @param primitive 输入的Primitive对象
     * @return 示例值的代码字符串
     */
    private static String buildExampleValue(Primitive primitive) {
        if (primitive.getExampleValue() != null && primitive.getExampleValue().length() > 0) {
            if (CodeGenerationUtils.getOptional().isGenerateExample()) {
                if (primitive.getDataType().isNumber() || primitive.getDataType().isBoolean()) {
                    return ".setExampleValue(" + primitive.getExampleValue() + ")";
                } else {
                    return ".setExampleValue(\"" + primitive.getExampleValue() + "\")";
                }
            }
        }
        return "";
    }

    /**
     * 格式化参数为字符串
     *
     * @param description 输入的描述
     * @return 格式化后的字符串
     */
    private static String formatParam(String description) {
        if (description != null) {
            return "\"" + description + "\"";
        }
        return description;
    }

    /**
     * 获取数据类型的字符串表示
     *
     * @param type 输入的数据类型
     * @return 数据类型的字符串表示
     */
    public static String getType(DataType type) {
        return "DataType." + type;
    }
}
