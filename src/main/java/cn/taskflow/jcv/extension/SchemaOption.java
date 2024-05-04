package cn.taskflow.jcv.extension;

import cn.taskflow.jcv.core.DataType;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.Primitive;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public enum SchemaOption {
    REQUIRED,
    OPTIONAL;

    public boolean isRequired() {
        return this == REQUIRED;
    }

    public boolean isOptional() {
        return this == OPTIONAL;
    }

    public SchemaProcess getSchemaProcess() {
        return new SchemaProcess() {
            @Override
            public boolean isRequired(String name, DataType type, JsonSchema valueSchema) {
                return SchemaOption.this.isRequired();
            }

            @Override
            public boolean isRequired(String name, DataType type, JsonSchema... valueSchemas) {
                return SchemaOption.this.isRequired();
            }

            @Override
            public boolean isRequired(String name, DataType type, Primitive valueSchema) {
                return SchemaOption.this.isRequired();
            }

            @Override
            public boolean isOptional(String name, DataType dataType) {
                return SchemaOption.this.isOptional();
            }
        };
    }
}
