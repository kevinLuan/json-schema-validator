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

import cn.taskflow.jcv.core.JsonBoolean;
import cn.taskflow.jcv.core.JsonObject;
import com.github.javaparser.StaticJavaParser;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.utils.CodeGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class CodeGeneratorTest {
    @Test
    public void test() {
        String json = "{" + "    'b':true," + "    'a':false," + "    'bools':[" + "        true," + "        false"
                      + "    ]" + "}";
        String javaCode = CodeGenerator.generateCode(json);
        String expected = String.valueOf(
            "JsonObject.optional(" + "   JsonBoolean.optional('b', null).setExampleValue(true),"
                    + "   JsonBoolean.optional('a', null).setExampleValue(false),"
                    + "   JsonArray.optional('bools', null, JsonBoolean.make().setExampleValue(true))" + ");").replace(
            '\'', '"');
        System.out.println("格式化代码：" + StaticJavaParser.parseStatement(javaCode));
        System.out.println("原始代码:" + StaticJavaParser.parseStatement(expected).toString());
        Assert.assertEquals(StaticJavaParser.parseStatement(expected).toString(),
            StaticJavaParser.parseStatement(javaCode).toString());

        JsonSchema jsonSchema = JsonObject.optional(JsonBoolean.optional("b", null).setExampleValue("true"),
            JsonBoolean.optional("a", null).setExampleValue("false"),
            JsonArray.optional("bools", null, JsonBoolean.make().setExampleValue("true")));
        System.out.println(CodeGenerator.generateSampleData(jsonSchema));
        System.out.println(CodeGenerator.serialization(jsonSchema));
    }
}
