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
import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-05
 */
public class JsonValidatorTests {
    final JsonValidator jsonValidator = new JsonValidator() {
                                          @Override
                                          public boolean validate(JsonSchema schema, JsonNode node)
                                                                                                   throws ValidationException {
                                              System.out.println("path: `" + schema.getPath() + "`  -> node: `" + node
                                                                 + "`");
                                              if (schema.getPath().equals("items.id")) {
                                                  if (node.asInt() % 2 != 0) {
                                                      throw new IllegalArgumentException("参数:`" + schema.getPath()
                                                                                         + "` value:" + node
                                                                                         + " 不能是2的倍数");
                                                  }
                                              }
                                              return true;
                                          }
                                      };

    @Test
    public void test() {
        String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":\"2\"}],\"age\":100.11}";
        JsonSchema jsonSchema = JsonObject.optional(
            "a",
            JsonString.required("name").withValidator(jsonValidator),
            JsonArray.required("ids").withValidator(jsonValidator),
            JsonArray.required(
                "items",
                JsonObject.required(JsonString.required("name").withValidator(jsonValidator),
                    JsonNumber.required("id").withValidator(jsonValidator)).withValidator(jsonValidator))
                .withValidator(jsonValidator), JsonNumber.required("age").withValidator(jsonValidator)).withValidator(
            jsonValidator);
        Validator.of(jsonSchema).validate(json);
    }
}
