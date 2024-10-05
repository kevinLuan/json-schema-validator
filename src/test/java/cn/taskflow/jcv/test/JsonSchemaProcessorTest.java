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
import cn.taskflow.jcv.utils.JsonSchemaProcessor;
import org.junit.Test;

public class JsonSchemaProcessorTest {
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

    @Test
    public void test() {
        new JsonSchemaProcessor(buildResult()).run((jsonSchema) -> {
            System.out.println("节点名称:" + jsonSchema.getName() + "\t是否必须:" + jsonSchema.isRequired() + "-\t路径：" + jsonSchema.getPath());
        });
    }
}
