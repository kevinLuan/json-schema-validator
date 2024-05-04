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
import cn.taskflow.jcv.utils.IOUtils;
import com.github.javaparser.StaticJavaParser;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.utils.GeneratorCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class GeneratorCodeTest {
    @Test
    public void test() throws IOException {
        String json = IOUtils.readFile("CodeGeneratorTest_data.json");
        String generateJavaCode = StaticJavaParser.parseStatement(GeneratorCode.generateJavaCode(json)).toString();
        String exampleJavaCode = StaticJavaParser.parseStatement(IOUtils.readFile("CodeGeneratorTest_data.java"))
            .toString();
        Assert.assertEquals(exampleJavaCode, generateJavaCode);
        JsonSchema jsonSchema = JsonObject.optional(JsonBoolean.optional("b", null).setExampleValue("true"),
            JsonBoolean.optional("a", null).setExampleValue("false"),
            JsonArray.optional("bools", null, JsonBoolean.make().setExampleValue("true")));
        Assert.assertEquals(IOUtils.readJson(this.getClass(), "sample"), GeneratorCode.generateSampleData(jsonSchema));
        Assert.assertEquals(IOUtils.readJson(getClass(), "schema"), GeneratorCode.serialization(jsonSchema));
    }
}
