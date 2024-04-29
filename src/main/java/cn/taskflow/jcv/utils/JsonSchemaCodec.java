package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import cn.taskflow.jcv.datatype.NumberParser;

/**
 * Param到json数据协议转换(注意不是序列化Param协议格式)
 *
 * @author KEVIN LUAN
 */
public class JsonSchemaCodec {
    public static String toJsonDataExample(JsonSchema jsonSchema) {
        JsonElement element = null;
        if (jsonSchema.isArray()) {
            element = array(jsonSchema.asArray());
        } else if (jsonSchema.isObject()) {
            element = object(jsonSchema.asObject());
        } else if (jsonSchema.isPrimitive()) {
            element = primitive(jsonSchema.asPrimitive());
        } else {
            throw new IllegalArgumentException("不支持的类型:" + jsonSchema);
        }
        return element.toString();
    }

    private static com.google.gson.JsonObject object(JsonObject object) {
        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
        JsonSchema[] jsonSchemas = object.getChildren();
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isArray()) {
                com.google.gson.JsonArray value = array(jsonSchema.asArray());
                jsonObject.add(jsonSchema.getName(), value);
            } else if (jsonSchema.isObject()) {
                com.google.gson.JsonObject value = object(jsonSchema.asObject());
                jsonObject.add(jsonSchema.getName(), value);
            } else if (jsonSchema.isPrimitive()) {
                jsonObject.add(jsonSchema.getName(), primitive(jsonSchema.asPrimitive()));
            }
        }
        return jsonObject;
    }

    private static com.google.gson.JsonArray array(JsonArray array) {
        com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
        JsonSchema[] jsonSchemas = array.getChildren();
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isArray()) {
                com.google.gson.JsonArray value = array(jsonSchema.asArray());
                jsonArray.add(value);
            } else if (jsonSchema.isObject()) {
                com.google.gson.JsonObject value = object(jsonSchema.asObject());
                jsonArray.add(value);
            } else if (jsonSchema.isPrimitive()) {
                JsonElement value = primitive(jsonSchema.asPrimitive());
                jsonArray.add(value);
            }
        }
        return jsonArray;
    }

    private static JsonElement primitive(Primitive primitive) {
        if (primitive.getDataType() == DataType.Number) {
            if (primitive.getExampleValue() != null) {
                NumberParser numberParser = NumberParser.parse(primitive.getExampleValue(), false);
                return new JsonPrimitive(numberParser.value);
            }
            return JsonNull.INSTANCE;
        } else {
            if (primitive.getExampleValue() != null) {
                return new JsonPrimitive(primitive.getExampleValue());
            } else {
                return JsonNull.INSTANCE;
            }
        }
    }
}
