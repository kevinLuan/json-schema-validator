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
package cn.taskflow.jcv.codegen;

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.encode.GsonEncoder;
import cn.taskflow.jcv.spring.JsonSchemaValidate;
import cn.taskflow.jcv.validation.Validator;
import cn.taskflow.jcv.validation.ValueRangeValidation;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-29
 */
public class CodeGenerationTests {
    @Test
    public void test() {
        String json = "[" + //
                      "  {" + //
                      "    'id': 1024263244258258627," + //
                      "    'menuId': 1024263244258258626," + //
                      "    'name': 'Paninis'" + //
                      "  }," + //
                      "  {" + //
                      "    'id': 1024263244258258629," + //
                      "    'menuId': 1024263244258258626," + //
                      "    'name': 'Flagels'" + //
                      "  }" + //
                      "]";
        String schemaCode = CodeGenerationUtils.generateSchemaCode(json.replace('\'', '"'));
        JsonArray jsonArray = JsonArray.optional(JsonObject.optional(JsonNumber.optional("id"),
            JsonNumber.optional("menuId"), JsonString.optional("name")));
        Assert.assertEquals(schemaCode, CodeGenerationUtils.generateSchemaCode(jsonArray));

    }

    @Test
    public void testValidate() {
        JsonArray schema = JsonArray.required(//
            JsonObject.required(//
                JsonNumber.optional("id"),//
                JsonNumber.optional("menuId"),//
                JsonString.optional("name")//
                ));
        Validator.fromSchema(schema).validate("[{}]");
    }
}
