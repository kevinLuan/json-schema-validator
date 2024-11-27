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
package cn.taskflow.jcv.test;

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.encode.GsonEncoder;
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.codegen.CodeGenerationUtils;
import cn.taskflow.jcv.utils.JsonParser;
import cn.taskflow.jcv.utils.JsonSchemaCodec;
import cn.taskflow.jcv.validation.DataVerifyHandler;
import cn.taskflow.jcv.validation.Validator;
import com.github.javaparser.StaticJavaParser;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class ResponseValidatorTest {

    @Test
    public void testFromJsonConvertParam_and_CheckParam() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
        JsonSchema jsonSchema = JsonParser.parseJsonSchema(json);
        // 修改参数验证范围
        jsonSchema.asObject().getChildren()[0].asPrimitive().between(10, 20);
        try {
            JsonNode node = NodeFactory.parser(json);
            Validator.fromSchema(DataVerifyHandler.getInstance(), jsonSchema).validate(node);
            Assert.fail("没有出现预期的错误");
        } catch (Exception e) {
            Assert.assertEquals("`name` between character size [ 10~20 ]", e.getMessage());
        }
        // 重置验证逻辑
        jsonSchema.asObject().getChildren()[0].asPrimitive().between(1, 10);
        // 验证数据范围
        Optional<JsonSchema> optional = jsonSchema.asObject().getChildren()[1].asArray().getSchemaForFirstChildren();
        if (optional.isPresent()) {
            optional.get().asPrimitive().between(1, 50);
        }
        try {
            JsonNode node = NodeFactory.parser(json);
            Validator.fromSchema(DataVerifyHandler.getInstance(), jsonSchema).validate(node);
            Assert.fail("没有出现预期的错误");
        } catch (Exception e) {
            Assert.assertEquals("`ids` between [1 ~ 50]", e.getMessage());
        }
    }

    @Test
    public void test_jsonToParam() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
        String desc = null;//(JsonParser.DESCRIPTION = "参数描述");
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
        JsonSchema jsonSchema = JsonObject.required(JsonString.optional("name", "张三丰"),
            JsonArray.required("ids", "Array[100]", JsonNumber.ofNullable()),
            JsonArray.required("items", "Array[{Object}]", JsonObject.required(JsonString.optional("name", "手机"), //
                JsonNumber.optional("id", "2")//
                )), JsonNumber.optional("age", "100.11")//
            );
        StaticJavaParser.parseStatement(CodeGenerationUtils.generateSchemaCode(jsonSchema));
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
        System.out.println(CodeGenerationUtils.serialization(JsonParser.parseJsonSchema(expected)));
    }

    @Test
    public void test_fromParamAsJavaCode() {
        String json = "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
        JsonSchema jsonSchema = GsonEncoder.INSTANCE.decode(json, JsonBasicSchema.class);
        String actual = CodeGenerationUtils.generateSchemaCode(jsonSchema);
        System.out.println(StaticJavaParser.parseStatement(actual).toString());
    }

}
