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
package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.extension.SchemaOptions;
import cn.taskflow.jcv.validation.Validator;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-05-04
 */
public class MyTest {
    private Map<String, Object> getRequest() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", 60);
        map.put("item", new HashMap<>());
        ((Map<String, Object>) map.get("item")).put("id", 1000);
        ((Map<String, Object>) map.get("item")).put("title", "item name");
        ((Map<String, Object>) map.get("item")).put("orderIds", Lists.newArrayList(1, 2, 3, 4, 5));
        return map;
    }

    @Test
    public void test() {
        String json = "{\"item\":{\"id\":1000,\"orderIds\":[1,2,3,4,5],\"title\":\"item name\"},\"name\":\"张三丰\",\"age\":60}";
        System.out.println("JSON: " + json);
        JsonObject jsonSchema = JsonObject.required(JsonObject.required("item", JsonNumber.required("id", null)
            .setExampleValue(1000), JsonArray.required("orderIds", null, JsonNumber.ofNonNull().setExampleValue(1)),
            JsonString.required("title", null).setExampleValue("item name")), JsonString.required("name", null)
            .setExampleValue("张三丰"), JsonNumber.required("age", null).setExampleValue(60));

        Map<String, Object> request = getRequest();
        //创建验证器
        Validator validator = Validator.fromSchema(jsonSchema);
        request.put("age", "十八岁");
        try {
            validator.validate(request);
            Assert.fail("未出现预期异常");
        } catch (ValidationException e) {
            Assert.assertEquals("`age` parameter error", e.getMessage());
        }
        //增加忽略字段
        request.put("ignore_field", "忽略数据");
        //根据根据定义参数提取数据
        Map<String, Object> response = validator.extract(request);
        System.out.println("提取数据：" + NodeFactory.stringify(response));

    }

    @Test
    public void testGenerateCode() {
        String json = "{\"item\":{\"id\":1000,\"orderIds\":[1,2,3,4,5],\"title\":\"item name\"},\"name\":\"张三丰\",\"age\":60}";
        System.out.println("--根据任意JSON数据化生成java代码：定义属性为必须类型--");
        System.out.println(CodeGenerationUtils.generateSchemaCode(json, SchemaOptions.REQUIRED));
        System.out.println("--根据任意JSON数据化生成java代码：定义为缺省值--");
        System.out.println(CodeGenerationUtils.generateSchemaCode(json, SchemaOptions.OPTIONAL));
        JsonSchema jsonSchema = CodeGenerationUtils.generateParamFromJson(json);
        System.out.println("-------------根据参数 schema 生成示例数据------------");
        System.out.println(CodeGenerationUtils.generateSampleData(jsonSchema));
        System.out.println("-------------验证schema参数序列化和反序列------------------");
        String paramDefine = CodeGenerationUtils.serialization(jsonSchema);
        CodeGenerationUtils.deserialization(paramDefine);
    }
}
