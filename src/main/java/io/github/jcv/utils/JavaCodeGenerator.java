package io.github.jcv.utils;

import io.github.jcv.core.*;

/**
 * 将Param转换到JavaCode
 *
 * @author KEVIN LUAN
 */
class JavaCodeGenerator {
    private final static String NEW_LINE = "";

    public static String generateCode(JsonSchema jsonSchema) {
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

    private static void parserArray(JsonArray array, StringBuilder builder) {
        String name = array.getName();
        String description = array.getDescription();
        JsonBasicSchema children = (JsonBasicSchema) array.getChildrenAsParam();
        if (children.isObject()) {
            StringBuilder stringBuilder = new StringBuilder();
            parserObject(children.asObject(), stringBuilder);
            if (array.isRequired()) {
                builder.append("JsonArray.required(" + formatParam(name) + "," + formatParam(description) + ",");
            } else {
                builder.append("JsonArray.optional(" + formatParam(name) + "," + formatParam(description) + ",");
            }
            builder.append(NEW_LINE);
            builder.append(stringBuilder + ")");
        } else if (children.isPrimitive()) {
            // 子节点的父级节点
            String arrayNode;
            if (array.isRequired()) {
                arrayNode = "JsonArray.required(" + formatParam(name) + "," + formatParam(description) + ",";
            } else {
                arrayNode = "JsonArray.optional(" + formatParam(name) + "," + formatParam(description) + ",";
            }
            // 子节点
            String childrenCode = children.getDataType().generatePrimitiveCode(children.isRequired());
            if (children.isPrimitive()) {
                String example = children.asPrimitive().getExampleValue();
                if (example != null) {
                    if (children.getDataType().isNumber() || children.getDataType().isBoolean()) {
                        childrenCode += ".setExampleValue(" + example + ")";
                    } else {
                        childrenCode += ".setExampleValue(\"" + example + "\")";
                    }
                }
            }
            arrayNode += NEW_LINE + childrenCode + ")";
            builder.append(arrayNode);
        } else {
            throw new IllegalArgumentException("不支持的类型:" + children);
        }
    }

    private static void parserObject(JsonObject object, StringBuilder builder) {
        String name = object.getName();
        String description = object.getDescription();
        JsonSchema[] childrens = object.getChildren();
        StringBuilder nodeBuilder = new StringBuilder();
        for (int i = 0; i < childrens.length; i++) {
            JsonSchema jsonSchema = childrens[i];
            if (jsonSchema.isArray()) {
                newLine(nodeBuilder);
                StringBuilder stringBuilder = new StringBuilder();
                parserArray(jsonSchema.asArray(), stringBuilder);
                nodeBuilder.append(stringBuilder.toString());
                nodeBuilder.append(",");
                newLine(nodeBuilder);
            } else if (jsonSchema.isObject()) {
                if (nodeBuilder.length() > 0) {
                    nodeBuilder.append(",");
                    newLine(nodeBuilder);
                }
                StringBuilder childrenObjectBuilder = new StringBuilder();
                parserObject(jsonSchema.asObject(), childrenObjectBuilder);
                nodeBuilder.append(childrenObjectBuilder.toString());
            } else if (jsonSchema.isPrimitive()) {
                StringBuilder stringBuilder = new StringBuilder();
                parserPrimitive(jsonSchema.asPrimitive(), stringBuilder);
                newLine(nodeBuilder);
                nodeBuilder.append(stringBuilder.toString());
                nodeBuilder.append(",");
                newLine(nodeBuilder);
            }
        }
        if (name != null && name.length() > 0) {
            if (object.isRequired()) {
                builder.append("JsonObject.required(" + formatParam(name) + "," + formatParam(description) + ","
                        + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
            } else {
                builder.append("JsonObject.optional(" + formatParam(name) + "," + formatParam(description) + ","
                        + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
            }

        } else {
            if (object.isRequired()) {
                builder.append("JsonObject.required(" + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
            } else {
                builder.append("JsonObject.optional(" + remoteLastComma(newLine(nodeBuilder)) + NEW_LINE + ")");
            }
        }
        newLine(builder);
    }

    private static String remoteLastComma(String str) {
        int lastCommaIndex = str.lastIndexOf(",");
        String endWith = str.substring(lastCommaIndex);
        if (endWith.equals(",") || endWith.equals("," + NEW_LINE)) {
            str = str.substring(0, lastCommaIndex);
        }
        return str;
    }

    private static String newLine(StringBuilder builder) {
        if (!builder.toString().endsWith(NEW_LINE)) {
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }

    private static void parserPrimitive(Primitive primitive, StringBuilder builder) {
        String name = primitive.getName();
        DataType type = primitive.getDataType();
        String description = primitive.getDescription();
        if (name.length() > 0) {
            builder.append(type.generatePrimitiveCode(primitive.isRequired(), name, description));
        } else {
            builder.append(type.generatePrimitiveCode(primitive.isRequired()));
        }
        builder.append(buildExampleValue(primitive));
    }

    private static String buildExampleValue(Primitive primitive) {
        if (primitive.getExampleValue() != null) {
            if (primitive.getDataType().isNumber() || primitive.getDataType().isBoolean()) {
                return ".setExampleValue(" + primitive.getExampleValue() + ")";
            } else {
                return ".setExampleValue(\"" + primitive.getExampleValue() + "\")";
            }
        } else {
            return "";
        }
    }

    private static String formatParam(String description) {
        if (description != null) {
            return "\"" + description + "\"";
        }
        return description;
    }

    public static String getType(DataType type) {
        return "DataType." + type;
    }
}
