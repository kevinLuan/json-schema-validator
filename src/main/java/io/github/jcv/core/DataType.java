package io.github.jcv.core;

import io.github.jcv.exception.NotSupportedException;
import io.github.jcv.utils.JsvUtils;

public enum DataType {
    String {
        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public void check(Primitive primitive, String value) {
            if (primitive.isRequired()) {
                if (value == null) {
                    throw new IllegalArgumentException(primitive.getName() + "参数不能为空");
                }
            } else {
                if (value == null) {
                    return;
                }
            }
            if (primitive.getMin() != null) {
                if (primitive.getMin().intValue() > value.length()) {
                    throw new IllegalArgumentException(primitive.getTipMsg());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().intValue() < value.length()) {
                    throw new IllegalArgumentException(primitive.getTipMsg());
                }
            }
        }
    },
    Number {
        @Override
        public boolean isNumber() {
            return true;
        }
    },
    Boolean {
        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public void check(Primitive primitive, String value) {
            if (primitive.isRequired()) {
                if (value == null) {
                    throw new IllegalArgumentException(primitive.getName() + "参数不能为空");
                }
            } else {
                if (value == null) {
                    return;
                }
            }
            if (!"true".equals(value) && !"false".equals(value) && !"1".equals(value) && !"0".equals(value)) {
                throw new IllegalArgumentException(primitive.getName() + "参数只能是true或者false");
            }
        }
    },
    Array, Object;

    /**
     * 断言基本数据类型 (String,Number)
     *
     * @param dataType
     */
    public static boolean isPrimitive(DataType dataType) {
        if (dataType != null && (DataType.String == dataType || DataType.Number == dataType) || DataType.Boolean == dataType) {
            return true;
        }
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public void check(Primitive p, String value) {
        // TODO 子类实现
    }

    public static DataType parser(String dataType) {
        for (DataType type : values()) {
            if (type.name().equals(dataType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的dataType:" + dataType);
    }

    public String generatePrimitiveCode(boolean required) {
        switch (this) {
            case Number:
                if (required) {
                    return "JsonNumber.make()";
                } else {
                    return "JsonNumber.ofNullable()";
                }
            case String:
                if (required) {
                    return "JsonString.make()";
                } else {
                    return "JsonString.ofNullable()";
                }
            case Boolean:
                if (required) {
                    return "JsonBoolean.make()";
                } else {
                    return "JsonBoolean.ofNullable()";
                }
            default:
                throw new NotSupportedException("不支持的类型:" + this);
        }
    }

    public java.lang.String generatePrimitiveCode(boolean required, java.lang.String name, java.lang.String desc) {
        final java.lang.String code;
        switch (this) {
            case Number:
                if (required) {
                    code = java.lang.String.format("JsonNumber.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                } else {
                    code = java.lang.String.format("JsonNumber.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                }
                break;
            case String:
                if (required) {
                    code = java.lang.String.format("JsonString.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                } else {
                    code = java.lang.String.format("JsonString.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                }
                break;
            case Boolean:
                if (required) {
                    code = java.lang.String.format("JsonBoolean.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                } else {
                    code = java.lang.String.format("JsonBoolean.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                }
                break;
            default:
                throw new NotSupportedException("不支持的类型:" + this);
        }
        return code.replace('\'', '"');
    }
}
