package io.github.jcv.test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import io.github.jcv.core.*;
import io.github.jcv.encode.GsonEncoder;
import io.github.jcv.encode.JsonUtils;

import io.github.jcv.utils.*;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseValidatorTest {

    @Test
    public void testFromJsonConvertParam_and_CheckParam() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        // 修改参数验证范围
        jsonSchema.asObject().getChildren()[0].asPrimitive().between(10, 20);
        try {
            JsonNode node = JsonUtils.parser(json);
            Validator.create(DataVerifyHandler.getInstance(), jsonSchema).validate(node);
            Assert.fail("没有出现预期的错误");
        } catch (Exception e) {
            Assert.assertEquals("`name`长度限制在10~20", e.getMessage());
        }
        // 重置验证逻辑
        jsonSchema.asObject().getChildren()[0].asPrimitive().between(1, 10);
        // 验证数据范围
        jsonSchema.asObject().getChildren()[1].asArray().getChildrenAsParam().asPrimitive().between(1, 50);
        try {
            JsonNode node = JsonUtils.parser(json);
            Validator.create(DataVerifyHandler.getInstance(), jsonSchema).validate(node);
            Assert.fail("没有出现预期的错误");
        } catch (Exception e) {
            Assert.assertEquals("`ids`限制范围1~50", e.getMessage());
        }
    }

    @Test
    public void test_jsonToParam() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
        String desc = (JsonParser.DESCRIPTION = "参数描述");
        JsonSchema actual = JsonParser.parseJsonSchema(json);
        JsonSchema expected = JsonObject.optional(//
                JsonString.optional("name", desc).setExampleValue("张三丰"), //
                JsonArray.optional("ids", desc, //
                        JsonNumber.ofNullable().setExampleValue(100)), //
                JsonArray.optional("items", desc, //
                        JsonObject.optional(//
                                JsonString.optional("name", desc).setExampleValue("手机"), //
                                JsonNumber.optional("id", desc).setExampleValue(2)//
                        )//
                ), //
                JsonNumber.optional("age", desc).setExampleValue(100.11)//
        );
        System.out.println(GsonEncoder.INSTANCE.encode(expected));
        System.out.println(GsonEncoder.INSTANCE.encode(actual));
        Assert.assertTrue(expected.equals(actual));
    }

    @Test
    public void test_jsonToCode() {
        JsonSchema jsonSchema = JsonObject.required(
                JsonString.optional("name", "张三丰"),
                JsonArray.required("ids", "Array[100]",
                        JsonNumber.ofNullable()),
                JsonArray.required("items", "Array[{Object}]",
                        JsonObject.required(
                                JsonString.optional("name", "手机"), //
                                JsonNumber.optional("id", "2")//
                        )
                ),
                JsonNumber.optional("age", "100.11")//
        );
        String expected = ("JsonObject.required(" +
                "                JsonString.optional('name', '张三丰')," +
                "                JsonArray.required('ids', 'Array[100]'," +
                "                        JsonNumber.ofNullable())," +
                "                JsonArray.required('items', 'Array[{Object}]'," +
                "                        JsonObject.required(" +
                "                                JsonString.optional('name', '手机')," +
                "                                JsonNumber.optional('id', '2')" +
                "                        ))," +
                "                JsonNumber.optional('age', '100.11')" +
                "        );").replace("'", "\"");
        expected = expected.replace("'", "\"");
        Assert.assertEquals(StaticJavaParser.parseStatement(expected), StaticJavaParser.parseStatement(CodeGenerator.generateCode(jsonSchema)));
    }

    @Test
    public void test_fromParamAsJsonData() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
        System.out.println("原始数据JSON:");
        System.out.println(json);
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        String actual = JsonSchemaCodec.toJsonDataExample(jsonSchema);
        System.out.println("反向解析到JSON:");
        System.out.println(actual);
        Assert.assertEquals(json, actual);
    }

    @Test
    public void test_fromParamAsJsonData1() {
        String json = "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
        JsonSchema jsonSchema = GsonEncoder.INSTANCE.decode(json, JsonBasicSchema.class);
        String expected = "{\"status\":{\"statusCode\":1500,\"statusReason\":\"参数错误\"},\"items\":[1,2,3]}";
        System.out.println(CodeGenerator.serialization(JsonParser.parseJsonSchema(expected)));
    }

    @Test
    public void test_fromParamAsJavaCode() {
        String json = "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
        JsonSchema jsonSchema = GsonEncoder.INSTANCE.decode(json, JsonBasicSchema.class);
        String expected = "JsonObject.optional(\n" +
                "                JsonObject.optional(\"status\", \"状态\",\n" +
                "                        JsonNumber.optional(\"statusCode\", \"状态码\").setExampleValue(1500),\n" +
                "                        JsonString.optional(\"statusReason\", \"状态描述\").setExampleValue(\"参数错误\")),\n" +
                "                JsonObject.optional(\"result\", \"结果\",\n" +
                "                        JsonString.optional(\"id\", \"ID\").setExampleValue(\"1234\"),\n" +
                "                        JsonString.optional(\"name\", \"名称\").setExampleValue(\"xxx\")\n" +
                "                )\n" +
                "        );";
        String actual = CodeGenerator.generateCode(jsonSchema);
        System.out.println(StaticJavaParser.parseStatement(actual).toString(new DefaultPrinterConfiguration()));
        Assert.assertEquals(StaticJavaParser.parseStatement(expected).toString(), StaticJavaParser.parseStatement(actual).toString());
    }
}
