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
import cn.taskflow.jcv.utils.GenerateOptional;
import cn.taskflow.jcv.utils.IOUtils;
import com.github.javaparser.StaticJavaParser;
import cn.taskflow.jcv.utils.CodeGenerationUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class CodeGenerationUtilsTest {
    @Test
    public void test() throws IOException {
        String json = IOUtils.readFile("GeneratorCodeTest_data.json");
        System.out.println(CodeGenerationUtils.generateSchemaCode(json));
        String generateJavaCode = StaticJavaParser.parseStatement(CodeGenerationUtils.generateSchemaCode(json))
            .toString();
        StaticJavaParser.parseStatement(generateJavaCode);
        JsonSchema jsonSchema = JsonObject.optional(JsonBoolean.optional("b", null).setExampleValue("true"),
            JsonBoolean.optional("a", null).setExampleValue("false"),
            JsonArray.optional("bools", null, JsonBoolean.ofNonNull().setExampleValue("true")));
        CodeGenerationUtils.generateSampleData(jsonSchema);
        System.out.println(CodeGenerationUtils.serialization(jsonSchema));
    }

    @Test
    public void testSimple() {
        String json = "{\"name\":\"x\",\"description\":\"x\",\"paused\":false,\"runCatchupScheduleInstances\":false,\"startTime\":1729935819148,\"endTime\":0,\"timeZone\":\"Asia/Shanghai\",\"triggerType\":\"SCHEDULE\",\"timerTaskTrigger\":{\"dayOfWeeks\":[],\"dayOfMonths\":[],\"skipWeekends\":false,\"skipHolidays\":false},\"cronTrigger\":{},\"startWorkflowRequest\":{\"version\":1,\"input\":{},\"taskToDomain\":{},\"priority\":0,\"idempotencyStrategy\":\"FAIL\"},\"overwrite\":false}";
        System.out.println(CodeGenerationUtils.generateSchemaCode(json, new GenerateOptional()));
    }
}
