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

import cn.taskflow.jcv.codegen.CodeGenerationUtils;
import cn.taskflow.jcv.codegen.GenerateOptional;
import cn.taskflow.jcv.utils.*;
import cn.taskflow.jcv.core.JsonNumber;
import cn.taskflow.jcv.core.JsonObject;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.JsonString;
import cn.taskflow.jcv.core.JsonArray;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SchemaTest {
    private static JsonSchema product = JsonObject.required("product", "商品对象", //
                                          JsonString.required("name", "商品名称").setExampleValue("IPhone7"), JsonNumber
                                              .required("price", "商品价格").setExampleValue(99.98), JsonArray.required(
                                              "skus", "商品SKU属性列表", //
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
        String javaCode = CodeGenerationUtils.generateSchemaCode(json);
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
        Assert.assertEquals(CodeGenerationUtils.generateSchemaCode(jsonSchema),
            CodeGenerationUtils.generateSchemaCode(generateJsonSchema));
    }

    @Test
    public void testOptional() {
        String json = "{\n" + "    \"name\": \"IPhone7\",\n" + "    \"price\": 99.98,\n" + "    \"skus\": [\n"
                      + "        {\n" + "            \"id\": 100,\n" + "            \"name\": \"移动版\",\n"
                      + "            \"code\": [\n" + "                {\n" + "                    \"id\": 12345,\n"
                      + "                    \"title\": \"土黄金色\"\n" + "                }\n" + "            ]\n"
                      + "        }\n" + "    ]\n" + "}";
        System.out.println(json);
        String javaCode = CodeGenerationUtils.generateSchemaCode(json, new GenerateOptional());
        System.out.println("生成代码: " + javaCode);
    }

    @Test
    public void serializationTest() throws IOException {
        String paramDefine = CodeGenerationUtils.serialization(product);
        String fileData = IOUtils.readFile("product_param_define.json");
        Assert.assertEquals(CodeGenerationUtils.serialization(CodeGenerationUtils.deserialization(fileData)),
            paramDefine);
        JsonSchema jsonSchema = CodeGenerationUtils.deserialization(paramDefine);
        System.out.println("生成数据示例:" + JsonSchemaCodec.toJsonDataExample(jsonSchema));
        String json = "{\"name\":\"IPhone7\",\"price\":99.98,\"skus\":[{\"id\":100,\"name\":\"移动版\",\"code\":[{\"id\":12345,\"title\":\"土黄金色\"}]}]}";
        Assert.assertEquals(json, JsonSchemaCodec.toJsonDataExample(JsonParser.parseJsonSchema(json)));
    }
}
