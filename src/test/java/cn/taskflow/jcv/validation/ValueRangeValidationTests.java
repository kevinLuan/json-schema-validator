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
package cn.taskflow.jcv.validation;

import cn.taskflow.jcv.core.JsonString;
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-23
 */
public class ValueRangeValidationTests {
    @Test
    public void test() {
        List<Object> values = new ArrayList<>();
        values.addAll(Lists.newArrayList("a", "b", "c"));
        values.addAll(Lists.newArrayList(true, false));
        values.addAll(Lists.newArrayList(0, 1, 2, 3, 4, 5));
        values.addAll(Lists.newArrayList(10.12f, 11.123f, 12.123f));
        values.addAll(Lists.newArrayList(2.123d, 3.1415926d));

        List<Object> excludeValues = new ArrayList<>();
        excludeValues.addAll(Lists.newArrayList("AA", "BB"));
        excludeValues.addAll(Lists.newArrayList(6, 7, 8));
        excludeValues.addAll(Lists.newArrayList(1101.12f, 1111.123f, 1112.123f));
        excludeValues.addAll(Lists.newArrayList(211.123d, 13.1415926d));
        ValueRangeValidation rangeValidation = ValueRangeValidation.fromWithinValues("hello");
        rangeValidation.addWithinValues(values.toArray());
        rangeValidation.addExcludeValues(excludeValues.toArray());

        for (Object value : values) {
            JsonNode jsonNode = NodeFactory.convert(value);
            rangeValidation.validate(JsonString.required("name"), jsonNode);
        }
        //排除值：false
        rangeValidation.addExcludeValues(false);
        try {
            rangeValidation.validate(JsonString.required("x"), NodeFactory.convert(false));
            Assert.fail("未出现逾期结果");
        } catch (ValidationException e) {
            Assert.assertEquals("The parameter field:'x' is out of the legal range", e.getMessage());
        }
    }
}
