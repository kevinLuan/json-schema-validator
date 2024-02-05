package io.github.jcv.test;

import io.github.jcv.core.JsonNumber;
import io.github.jcv.core.JsonObject;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.core.JsonString;
import io.github.jcv.core.JsonArray;
import io.github.jcv.utils.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SchemaTest {
    private static JsonSchema product = JsonObject.required("product", "商品对象", //
            JsonString.required("name", "商品名称").setExampleValue("IPhone7"),
            JsonNumber.required("price", "商品价格").setExampleValue(99.98),
            JsonArray.required("skus", "商品SKU属性列表", //
                    JsonObject.required(//
                            JsonNumber.required("id", "参数描述").setExampleValue(100), //
                            JsonString.required("name", "参数描述").setExampleValue("移动版"), //
                            JsonArray.required("code", "参数描述", //
                                    JsonObject.optional(//
                                            JsonNumber.optional("id", "id").setExampleValue(12345), //
                                            JsonString.optional("title", "标题").setExampleValue("土黄金色")//
                                    )//
                            )//
                    )//
            )//
    );

    @Test
    public void test() {
        String json = JsonSchemaCodec.toJsonDataExample(product);
        String expected = "{\"name\":\"IPhone7\",\"price\":99.98,\"skus\":[{\"id\":100,\"name\":\"移动版\",\"code\":[{\"id\":12345,\"title\":\"土黄金色\"}]}]}";
        Assert.assertEquals(expected, json);
        String javaCode = CodeGenerator.generateCode(json);
        System.out.println("根据json数据生成验证参数代码:" + javaCode);
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        JsonSchema generateJsonSchema = JsonObject.optional(//
                JsonString.optional("name", null).setExampleValue("IPhone7"),//
                JsonNumber.optional("price", null).setExampleValue(99.98),//
                JsonArray.optional("skus", null,//
                        JsonObject.optional(//
                                JsonNumber.optional("id", null).setExampleValue(100),//
                                JsonString.optional("name", null).setExampleValue("移动版"),//
                                JsonArray.optional("code", null,//
                                        JsonObject.optional(//
                                                JsonNumber.optional("id", null).setExampleValue(12345),//
                                                JsonString.optional("title", null).setExampleValue("土黄金色")//
                                        )//
                                )//
                        )//
                )//
        );
        Assert.assertEquals(CodeGenerator.generateCode(jsonSchema), CodeGenerator.generateCode(generateJsonSchema));
    }

    @Test
    public void serializationTest() throws IOException {
        String paramDefine = CodeGenerator.serialization(product);
        String fileData = IOUtils.readFile("product_param_define.json");
        Assert.assertEquals(CodeGenerator.serialization(CodeGenerator.deserialization(fileData)), paramDefine);
        JsonSchema jsonSchema = CodeGenerator.deserialization(paramDefine);
        System.out.println("生成数据示例:" + JsonSchemaCodec.toJsonDataExample(jsonSchema));
        String json = "{\"name\":\"IPhone7\",\"price\":99.98,\"skus\":[{\"id\":100,\"name\":\"移动版\",\"code\":[{\"id\":12345,\"title\":\"土黄金色\"}]}]}";
        Assert.assertEquals(json, JsonSchemaCodec.toJsonDataExample(JsonParser.parseJsonSchema(json)));
    }
}
