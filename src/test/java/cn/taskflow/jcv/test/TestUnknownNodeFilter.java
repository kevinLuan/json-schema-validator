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
import cn.taskflow.jcv.extension.DefaultUnknownNodeFilter;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 测试未知字段过滤器处理
 *
 * @author KEVIN LUAN
 */
public class TestUnknownNodeFilter {
    private JsonSchema buildResult() {
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

    @Test
    public void test_Filter() {
        JsonSchema jsonSchema = buildResult();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("result",
                "{\"name\":true,\"error\":true,\"remark\":\"remark\",\"extendProps\":[\"不符合邀请类型的属性\"],\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"remark\":true,\"ERROR\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}");
        Map<String, Object> map = Validator.of(ArgumentVerifyHandler.getInstance(), jsonSchema).setUnknownNodeFilter(DefaultUnknownNodeFilter.INSTANCE)
                .validate(request::getParameter).extract(request::getParameter);
        System.out.println(map);
        String expected = "{result={\"name\":true,\"remark\":\"remark\",\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}}";
        Assert.assertEquals(expected, map.toString());
        JsonNode jsonNode = NodeFactory.parser(request.getParameter("result"));
        map = Validator.of(DataVerifyHandler.getInstance(), jsonSchema).setUnknownNodeFilter(DefaultUnknownNodeFilter.INSTANCE).validate(jsonNode)
                .extract(jsonNode);
        System.out.println(map);
        expected = "{name=true, ids=[1], remark=\"remark\", items=[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}], age=1}";
        Assert.assertEquals(expected, map.toString());

    }
}
