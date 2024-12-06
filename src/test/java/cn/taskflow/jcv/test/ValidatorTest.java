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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.validation.ArgumentVerifyHandler;
import cn.taskflow.jcv.validation.DataVerifyHandler;
import cn.taskflow.jcv.validation.Validator;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

public class ValidatorTest {
    private static JsonObject getResultParam() {
        return JsonObject.required(
            JsonObject.required("status", "返回", JsonNumber.required("status_code", ""),
                JsonString.required("status_reasion", "")), buildResult());
    }

    private static JsonSchema buildResult() {
        return JsonObject.optional("result", "返回数据", //
            JsonString.required("name", "姓名").setMax(5), //
            JsonNumber.required("age", "年龄").setMin(0).setMax(120), //
            JsonArray.required("items", "商品列表", //
                JsonObject.required(//
                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                    JsonString.required("name", "商品名称").setMax(50)//
                    )//
                ), //
            JsonArray.required("ids", "id列表", //
                JsonNumber.ofNonNull().setMax(100) //
                ), //
            JsonObject.optional("extendMap", "扩展字段"), //
            JsonArray.optional("array_any", "任意数组节点"), //
            JsonArray.optional("array_any_simple", "任意数组节点"));
    }

    private static Object getResult() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("C_", new Date());
        map.put("age", "100.11");
        List<Object> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "2");
        item.put("name", "手机");
        item.put("D_", new Date());
        items.add(item);
        map.put("items", items);
        List<Object> ids = new ArrayList<>();
        ids.add("100");
        map.put("ids", ids);
        map.put("EE__", new HashMap<>());

        Map<String, Object> extendMap = new HashMap<>();
        extendMap.put("a", 10);
        extendMap.put("obj", new HashMap<String, Object>());
        map.put("extendMap", extendMap);
        map.put("array_any", new Object[] { extendMap });
        map.put("array_any_simple", new int[] { 1, 2, 3, 4, 5 });
        return map;
    }

    private static String getResponseData() {
        Map<String, Object> dataResult = new HashMap<>();
        Map<String, Object> status = new HashMap<>();
        status.put("status_code", 100);
        status.put("A_", new Object());// 协议规范中没有的字段（会自动排除掉）
        status.put("B_", true);//// 协议规范中没有的字段（会自动排除掉）
        status.put("status_reasion", "参数错误");
        dataResult.put("status", status);
        dataResult.put("result", getResult());
        dataResult.put("other", new HashMap<>());
        return NodeFactory.stringify(dataResult);
    }

    @Test
    public void test_response() {
        System.out.println(getResultParam());
        String json = getResponseData();
        System.out.println(json);
        JsonNode jsonNode = NodeFactory.parser(json);
        Map<String, Object> map = Validator.fromSchema(DataVerifyHandler.getInstance(), getResultParam())
            .validate(jsonNode).extract(jsonNode);
        System.out.println(NodeFactory.stringify(map));
        String expected = "{'result':{'array_any':[{'a':10,'obj':{}}],'array_any_simple':[1,2,3,4,5],'extendMap':{'a':10,'obj':{}},'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'},'status':{'status_code':100,'status_reasion':'参数错误'}}";
        expected = expected.replace("'", "\"");
        Assert.assertEquals(expected, NodeFactory.stringify(map));
    }

    @Getter
    static class MyObj {
        private String actionName = "hello";
        private Number helloKitty = 123;
    }

    @Test
    public void testCamelValidate() {
        MyObj myObj = new MyObj();
        JsonNode jsonNode = NodeFactory.getJsonNodeConverter(true).convert(myObj);
        Assert.assertEquals(myObj.getActionName(), jsonNode.get("actionName").asText());
        Assert.assertEquals(myObj.getHelloKitty(), jsonNode.get("helloKitty").asInt());
        Map<String, Object> map = Validator
            .fromSchema(JsonObject.required(JsonString.required("actionName"), JsonNumber.required("helloKitty")))
            .validate(myObj, true).extract(myObj, true);
        Assert.assertTrue(map.containsKey("actionName"));
        Assert.assertTrue(map.containsKey("helloKitty"));
    }

    @Test
    public void testSnakeCaseValidate() {
        MyObj myObj = new MyObj();
        JsonNode jsonNode = NodeFactory.getJsonNodeConverter(false).convert(myObj);
        Assert.assertEquals(myObj.getActionName(), jsonNode.get("action_name").asText());
        Assert.assertEquals(myObj.getHelloKitty(), jsonNode.get("hello_kitty").asInt());
        Map<String, Object> map = Validator
            .fromSchema(JsonObject.required(JsonString.required("action_name"), JsonNumber.required("hello_kitty")))
            .validate(myObj, false).extract(myObj, false);
        Assert.assertTrue(map.containsKey("action_name"));
        Assert.assertTrue(map.containsKey("hello_kitty"));
    }

    private JsonSchema buildParam() {
        return JsonObject.required("objParam",
            "对象参数", //
            JsonString.required("name", "姓名").setMax(5), //
            JsonNumber.required("age", "年龄").setMin(0).setMax(120), //
            JsonArray.required("items", "商品列表", //
                JsonObject.required(//
                    JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                    JsonString.required("name", "商品名称").setMax(50)//
                    )//
                ), //
            JsonArray.required("ids", "id列表", //
                JsonNumber.ofNonNull().setMax(100) //
                ), //
            JsonObject.optional("extendMap", "扩展Map(任意子节点)"), JsonArray.optional("array_any", "任意数组类型"),
            JsonArray.optional("array_any_simple", "任意数组类型"));
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
        {
            Map<String, Object> extendMap = new HashMap<>();
            extendMap.put("a", 10);
            extendMap.put("obj", new HashMap<String, Object>());
            map.put("extendMap", extendMap);
            map.put("array_any", new Object[] { extendMap });
            map.put("array_any_simple", new int[] { 1, 2, 3, 4, 5 });
        }
        String json = NodeFactory.stringify(map);
        request.addParameter("objParam", json);
        return request;
    }

    @Test
    public void testRequestValidate() {
        HttpServletRequest request = buildHttpRequest();
        Map<String, Object> map = Validator.fromSchema(ArgumentVerifyHandler.getInstance(), buildParam()).validate(request::getParameter).extract(request::getParameter);
        System.out.println("提取数据：" + NodeFactory.stringify(map));
        String expected = "{'objParam':{'array_any':[{'a':10,'obj':{}}],'array_any_simple':[1,2,3,4,5],'extendMap':{'a':10,'obj':{}},'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'}}";
        String actual = NodeFactory.stringify(map);
        Assert.assertEquals(expected.replace("'", "\""), actual);
    }
}
