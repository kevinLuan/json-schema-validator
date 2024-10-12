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
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.pojo.DataResult;
import cn.taskflow.jcv.encode.GsonEncoder;
import cn.taskflow.jcv.validation.ArgumentVerifyHandler;
import cn.taskflow.jcv.validation.Validator;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;

public class TestJsonSchema {

    private JsonSchema buildParam() {
        return JsonObject.required("objParam", "对象参数", //
            JsonString.required("name", "姓名").setMax(5), //
            JsonNumber.required("age", "年龄").setMin(0).setMax(120), //
            JsonArray.required("items", "商品列表", //
                JsonObject.required(//
                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                    JsonString.required("name", "商品名称").setMax(50)//
                    )//
                ), //
            JsonArray.required("ids", "id列表", //
                JsonNumber.make().setMax(100) //
                )//
            );
    }

    private HttpServletRequest buildHttpRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "100.11");
        List<Object> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "2");
        item.put("name", "手机");
        items.add(item);
        map.put("items", items);
        List<Object> ids = new ArrayList<>();
        ids.add("100");
        map.put("ids", ids);
        String json = NodeFactory.stringify(map);
        request.addParameter("objParam", json);
        return request;
    }

    @Before
    public void before() {
    }

    @Test
    public void test_filter_object() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "100.11");
        map.put("A1", "没有定义的参数参数");
        map.put("A2", "没有定义的参数参数");
        List<Object> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "2");
        item.put("A3", "没有定义的参数参数");
        item.put("name", "手机");
        items.add(item);
        map.put("items", items);
        List<Object> ids = new ArrayList<>();
        ids.add("100");
        map.put("ids", ids);
        String json = NodeFactory.stringify(map);
        request.addParameter("objParam", json);
        JsonSchema jsonSchema = buildParam();
        Map<String, Object> data = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(request::getParameter).extract(request::getParameter);
        String actual = NodeFactory.stringify(data);
        HttpServletRequest myRequest = buildHttpRequest();
        data = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(myRequest::getParameter).extract(myRequest::getParameter);
        String expected = NodeFactory.stringify(data);
        Assert.assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void testValidateFail() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "160");
        map.put("sql", "CSRF漏洞");
        request.addParameter("userInfo", NodeFactory.stringify(map));
        JsonSchema jsonSchema = JsonObject.optional("userInfo", "用户信息", JsonString.required("name", "姓名").setMin(2).setMax(32), JsonNumber.optional("age", null).between(18, 65));
        try {
            Map<String, Object> extractData = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(request::getParameter).extract(request::getParameter);
            System.out.println(extractData);
            Assert.fail("未出现逾期结果");
        } catch (ValidationException e) {
            Assert.assertEquals("`userInfo.age` between [18 ~ 65]", e.getMessage());
        }
    }

    @Test
    public void testDataExtract() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("sql", "CSRF漏洞");//各位传递的参数，在经过提取提取时，将会自动忽略
        request.addParameter("userInfo", NodeFactory.stringify(map));
        JsonSchema jsonSchema = JsonObject.optional("userInfo", JsonString.required("name", null), JsonNumber.optional("age", null));
        Map<String, Object> extractData = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(request::getParameter).extract(request::getParameter);
        ObjectNode userInfo = (ObjectNode) extractData.get("userInfo");
        Assert.assertEquals(userInfo.get("name").textValue(), "张三丰");
        Assert.assertFalse(userInfo.has("sql"));//字段被忽略
    }

    @Test
    public void test_filter_object_max() {
        try {
            String a1 = DataResult.make("T", new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})}).toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            System.out.println(a1);
            System.out.println(b1);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            JsonSchema A1 = JsonObject.required("A1", "参数描述", //
                    JsonObject.required("result", "参数描述", //
                            JsonArray.required("T", "参数描述", //
                                    JsonObject.required(//
                                            JsonObject.required("status", "参数描述", //
                                                    JsonNumber.required("statusCode", "参数描述")//
                                            ), //
                                            JsonArray.required("result", "参数描述", //
                                                    JsonObject.required(//
                                                            JsonString.required("result", "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            JsonSchema B1 = JsonObject.required("B1", "X", JsonObject.required("result", "X"));
            Map<String, Object> map = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), A1, B1).validate(request::getParameter).extract(request::getParameter);
            Assert.fail("没有出现预期错误");
            System.out.println(map);
        } catch (Exception e) {
            Assert.assertEquals("`A1.result.T.result.result` parameter error", e.getMessage());
        }
        System.out.println("------------");
        {
            DataResult<?> data = DataResult.make("T", new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})});
            String a1 = data.toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            request.addParameter("list", NodeFactory.stringify(new Object[]{data, data}));
            System.out.println(a1);
            System.out.println(b1);
            System.out.println(request.getParameter("list"));
            JsonSchema A1 = JsonObject.required("A1", "参数描述", //
                    JsonObject.required("result", "参数描述", //
                            JsonArray.required("T", "参数描述", //
                                    JsonObject.required(//
                                            JsonObject.required("status", "参数描述", //
                                                    JsonNumber.required("statusCode", "参数描述")//
                                            ), //
                                            JsonArray.required("result", "参数描述", //
                                                    JsonObject.required(//
                                                            JsonObject.required("status", "参数描述", //
                                                                    JsonString.required("statusReason", "参数描述")//
                                                            ), //
                                                            JsonObject.required("result", "参数描述", //
                                                                    JsonString.required("city", "参数描述")//
                                                            )//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            JsonSchema list = JsonArray.required("list", "参数描述", //
                    JsonObject.required(//
                            JsonObject.required("result", "参数描述", //
                                    JsonArray.required("T", "参数描述", //
                                            JsonObject.required(//
                                                    JsonObject.required("status", "参数描述", //
                                                            JsonNumber.required("statusCode", "参数描述")//
                                                    ), //
                                                    JsonArray.required("result", "参数描述", //
                                                            JsonObject.required(//
                                                                    JsonObject.required("status", "参数描述", //
                                                                            JsonString.required("statusReason", "参数描述")//
                                                                    ), //
                                                                    JsonObject.required("result", "参数描述", //
                                                                            JsonString.required("city", "参数描述")//
                                                                    )//
                                                            )//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );

            JsonSchema B1 = JsonObject.required("B1", "参数描述", //
                    JsonObject.required("result", "参数描述", //
                            JsonObject.required("B", "参数描述", //
                                    JsonObject.required("status", "参数描述", //
                                            JsonNumber.optional("statusCode", "参数描述")//
                                    ), //
                                    JsonArray.optional("result", "参数描述", //
                                            JsonObject.required(//
                                                    JsonString.optional("result", "参数描述"), //
                                                    JsonObject.optional("status", "", //
                                                            JsonNumber.required("statusCode", "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    ), //
                    JsonObject.required("status", "参数描述", //
                            JsonString.required("statusReason", "statusReason")//
                    )//
            );
            Map<String, Object> map = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), A1, B1).validate(request::getParameter).extract(request::getParameter);
            System.out.println(map);
            String string = "{'result':{'T':[{'status':{'statusCode':0},'result':[{'status':{'statusReason':''},'result':{'city':'北京'}}]}]}}".replace("'", "\"");
            Assert.assertEquals(string, NodeFactory.stringify(map.get("A1")));
            String expected = "{'status':{'statusReason':''},'result':{'B':{'status':{'statusCode':0},'result':[{'status':{'statusCode':0},'result':'OK'}]}}}".replace("'", "\"");
            Assert.assertEquals(expected, NodeFactory.stringify(map.get("B1")));

            map = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), list).validate(request::getParameter).extract(request::getParameter);
            ArrayNode arrayNode = (ArrayNode) map.get("list");
            Assert.assertEquals(NodeFactory.stringify(arrayNode.get(0)), NodeFactory.stringify(arrayNode.get(1)));
            Assert.assertEquals(NodeFactory.stringify(arrayNode.get(0)), string);
            System.out.println("List-->>>" + NodeFactory.stringify(map.get("list")));
            long start = System.currentTimeMillis();
            for (int i = 0; i < 50000; i++) {
                Validator.fromSchema(ArgumentVerifyHandler.getInstance(), A1, B1, list).validate(request::getParameter).extract(request::getParameter);
            }
            System.out.println("使用耗时:" + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void test_checkParams() {
        JsonSchema jsonSchema = buildParam();
        // 输出参数定义
        Gson gson = new Gson();
        System.out.println(gson.toJson(jsonSchema));
        HttpServletRequest request = buildHttpRequest();
        System.out.println("objParam:" + request.getParameter("objParam"));
        Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(request::getParameter);
    }

    @Test
    public void test_deserialize() {
        JsonSchema jsonSchema = buildParam();
        // 输出参数定义
        Gson gson = new Gson();
        String json = gson.toJson(jsonSchema);
        System.out.println(json);
        MockHttpServletRequest mock_request = new MockHttpServletRequest();
        jsonSchema = gson.fromJson(json, JsonBasicSchema.class);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "100.11");
        List<Object> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "2");
        item.put("name", "手机");
        items.add(item);
        map.put("items", items);
        List<Object> ids = new ArrayList<>();
        ids.add("10x0");
        map.put("ids", ids);
        String json1 = NodeFactory.stringify(map);
        mock_request.addParameter("objParam", json1);
        try {
            Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(mock_request::getParameter);
        } catch (Exception e) {
            Assert.assertEquals("`objParam.ids[]` less than or equal to 100", e.getMessage());
        }
    }

    @Test
    public void test_string_length() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("password", "zhangsanfeng---------");
        Primitive param = JsonString.required("password", "密码").setMin(8).setMax(20);
        try {
            Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals(param.getTipMsg(), e.getMessage());
        }
    }

    @Test
    public void test_number_length() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("price", "7.99999999");
        request.addParameter("price_min", "7.19");
        request.addParameter("price_max", "20.00001");
        {
            Primitive param = JsonNumber.required("price", "价格").setMin(8).setMax(20);
            try {
                Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals(param.getTipMsg(), e.getMessage());
            }
        }
        {
            Primitive param = JsonNumber.required("price_min", "价格").setMin(7.18).setMax(20);
            Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
        }

        {
            Primitive param = JsonNumber.required("price_max", "价格").setMin(7.18).setMax(20);
            try {
                Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals(param.getTipMsg(), e.getMessage());
            }
        }
    }

    @Test
    public void test_err_param_definds() {
        try {
            JsonObject.required("o", "描述", //
                JsonArray.optional("a", "描述", //
                    JsonArray.required("a1", "描述", //
                        JsonObject.required(//
                            JsonString.required("", "")//
                            )//
                        )//
                    )//
                );
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals("illegal parameter", e.getMessage());
        }
    }

    @Test
    public void test_error() {
        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonObject.required("obj1", "对象", JsonString.required("name", "姓名")));
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"obj1\":{}}");
                    Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("Missing `objParam.obj1.name` parameter", e.getMessage());
                }
            }
        }

        {
            try {
                JsonArray.required("ids", "id列表", //
                        JsonNumber.required("id", "商品ID").setMax(100) //
                );
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals("parameter error", e.getMessage());
            }
        }
        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonObject.required("obj1", "对象", //
                            JsonString.required("name", "姓名")//
                    ), //
                    JsonArray.required("items", "商品列表", //
                            JsonObject.required(//
                                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                    JsonString.required("name", "商品名称").setMax(50)//
                            )//
                    ), //
                    JsonArray.required("ids", "id列表", //
                            JsonNumber.make().setMax(100) //
                    )//
            );
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"obj1\":{\"name\":\"张三\"}}");
                    Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("Missing `objParam.items` parameter", e.getMessage());
                }
            }
        }

        {
            {
                JsonObject param = JsonObject.required("objParam", "对象参数", //
                        JsonArray.required("items", "商品列表", //
                                JsonObject.required(//
                                        JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                        JsonString.required("name", "商品名称").setMax(50)//
                                )//
                        ), //
                        JsonArray.required("ids", "id列表", //
                                JsonNumber.make().setMax(100) //
                        )//
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":{\"name\":\"张三\"}}");
                        Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items` parameter error", e.getMessage());
                    }
                }
            }
        }

        {
            {
                JsonObject param = JsonObject.required("objParam", "对象参数", //
                        JsonArray.required("items", "商品列表", //
                                JsonObject.required(//
                                        JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                        JsonString.required("name", "商品名称").setMax(50), //
                                        JsonArray.required("ids", "id列表", //
                                                JsonNumber.make().setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"name\":\"张三\"}]}");
                        Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("Missing `objParam.items.id` parameter", e.getMessage());
                    }
                }
            }
        }

        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonArray.required("items", "商品列表", //
                            JsonObject.required(//
                                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                    JsonString.required("name", "商品名称").setMax(50), //
                                    JsonArray.required("ids", "id列表", //
                                            JsonNumber.make().setMax(100) //
                                    )//
                            )//
                    ) //
            );
            MockHttpServletRequest request = new MockHttpServletRequest();
            try {
                request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":null}]}");
                Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals("Missing `objParam.items.ids` parameter", e.getMessage());
            }
        }
        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonArray.required("items", "商品列表", //
                            JsonObject.required(//
                                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                    JsonString.required("name", "商品名称").setMax(50), //
                                    JsonArray.required("ids", "id列表", //
                                            JsonNumber.make().setMax(100) //
                                    )//
                            )//
                    ) //
            );
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[]}]}");
                    Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("`objParam.items.ids` parameter error", e.getMessage());
                }
            }
        }

        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonArray.required("items", "商品列表", //
                            JsonObject.required(//
                                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                    JsonString.required("name", "商品名称").setMax(50), //
                                    JsonArray.required("ids", "id列表", //
                                            JsonNumber.make().setMin(10).setMax(100) //
                                    )//
                            )//
                    ) //
            );
            MockHttpServletRequest request = new MockHttpServletRequest();
            try {

                request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[10000]}]}");
                Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals("`objParam.items.ids` between [10 ~ 100]", e.getMessage());
            }
        }

        {
            JsonObject param = JsonObject.required("objParam", "对象参数", //
                    JsonArray.required("items", "商品列表", //
                            JsonObject.required(//
                                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                    JsonString.required("name", "商品名称").setMax(50), //
                                    JsonArray.required("ids", "id列表", //
                                            JsonNumber.make().setMin(10).setMax(100) //
                                    )//
                            )//
                    ) //
            );
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[true,false]}]}");
                    Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("`objParam.items.ids[]` between [10 ~ 100]", e.getMessage());
                }
            }
        }

        {
            {
                JsonObject param = JsonObject.required("objParam", "对象参数", //
                        JsonArray.required("items", "商品列表", //
                                JsonObject.required(//
                                        JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                        JsonString.required("name", "商品名称").setMax(50), //
                                        JsonArray.required("ids", "id列表", //
                                                JsonNumber.make().setMin(10).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[{}]}]}");
                        Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.ids` parameter error", e.getMessage());
                    }
                }
            }
        }

        {
            {
                JsonObject param = JsonObject.required("objParam", "对象参数", //
                        JsonArray.required("items", "商品列表", //
                                JsonObject.required(//
                                        JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                        JsonString.required("name", "商品名称").setMax(50), //
                                        JsonArray.required("ids", "id列表", //
                                                JsonNumber.make().setMin(10).setMax(100) //
                                        ), //
                                        JsonArray.required("array", "x", //
                                                JsonObject.required( //
                                                        JsonNumber.optional("test", "")//
                                                )//
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[100],\"array\":[{\"test\":\"x\"}]}]}");
                        Validator.fromSchema(ArgumentVerifyHandler.getInstance(), param).validate(request::getParameter);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.array.test` parameter error", e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    public void test_init_param_error() {
        try {
            JsonObject.required("objParam", "对象参数", //
                JsonArray.required("items", "商品列表", //
                    JsonObject.required(//
                        JsonArray.required("array", "x", //
                            JsonObject.required("x", "x", //
                                JsonNumber.optional("test", "")//
                                )//
                            )//
                        )//
                    ) //
                );
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals("parameter error", e.getMessage());
        }
    }

    @Test
    public void test_seriable() {
        try {
            String a1 = DataResult.make("T", new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})}).toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            System.out.println(a1);
            System.out.println(b1);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            JsonSchema A1 = JsonObject.required("A1", "参数描述", //
                    JsonObject.required("result", "参数描述", //
                            JsonArray.required("T", "参数描述", //
                                    JsonObject.required(//
                                            JsonObject.required("status", "参数描述", //
                                                    JsonNumber.required("statusCode", "参数描述")//
                                            ), //
                                            JsonArray.required("result", "参数描述", //
                                                    JsonObject.required(//
                                                            JsonString.required("result", "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            JsonSchema B1 = JsonObject.required("B1", "X", JsonObject.required("result", "X"));
            {// 经过一次序列化在反序列化处理
                A1 = GsonEncoder.INSTANCE.decode(GsonEncoder.INSTANCE.encode(A1), JsonBasicSchema.class);
                B1 = GsonEncoder.INSTANCE.decode(GsonEncoder.INSTANCE.encode(B1), JsonBasicSchema.class);
            }
            Map<String, Object> map = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), A1, B1).validate(request::getParameter).extract(request::getParameter);
            Assert.fail("没有出现预期错误");
            System.out.println(map);
        } catch (Exception e) {
            Assert.assertEquals("`A1.result.T.result.result` parameter error", e.getMessage());
        }
    }

    @Test
    public void test_json() {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("obj", "");
            JsonSchema jsonSchema = JsonObject.required("obj", "参数描述");
            Validator.fromSchema(ArgumentVerifyHandler.getInstance(), jsonSchema).validate(request::getParameter);
            Assert.fail("没有出现预期错误");
        } catch (ValidationException e) {
            Assert.assertEquals("`obj` parameter error", e.getMessage());
        }
    }
}
