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
import cn.taskflow.jcv.core.JsonArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.taskflow.jcv.encode.NodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class TestResponse {
    @After
    public void after() {
        System.out.println();
    }

    private static JsonObject getResultParam() {
        return JsonObject.required(//
            JsonObject.required("status", "返回", //
                JsonNumber.required("status_code", ""), //
                JsonString.required("status_reasion", "")//
                ), //
            buildResult()//
            );
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
                JsonNumber.make().setMax(100) //
                )//
            );
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
    public void test_ok() {
        JsonSchema jsonSchema = getResultParam();
        JsonNode dataResult = NodeFactory.parser(getResponseData());
        Map<String, Object> response = Validator.of(DataVerifyHandler.getInstance(), jsonSchema).validate(dataResult)
            .extract(dataResult);
        System.out.println("提取数据：" + NodeFactory.stringify(response));
        String expected = "{'result':{'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'},'status':{'status_code':100,'status_reasion':'参数错误'}}"
            .replace("'", "\"");
        Assert.assertEquals(expected, NodeFactory.stringify(response));
    }

    @Test
    public void test() {
        {
            JsonSchema jsonSchema = getResultParam();
            JsonNode dataResult = NodeFactory.parser(getResponseData());
            System.out.println("返回原始数据：" + dataResult.toString());
            Map<String, Object> response = Validator.of(DataVerifyHandler.getInstance(), jsonSchema)
                .validate(dataResult).extract(dataResult);
            System.out.println("提取需要的数据：" + NodeFactory.stringify(response));
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JsonSchema jsonSchema = getResultParam();
            JsonNode dataResult = NodeFactory.parser(getResponseData());
            Validator.of(DataVerifyHandler.getInstance(), jsonSchema).validate(dataResult).extract(dataResult);
        }
        System.out.println("use time:" + (System.currentTimeMillis() - start));
    }
}
