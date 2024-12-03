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

import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-12-02
 */
public class TypeResolverTests {
    static class Container<T> {
        private T              value;
        private List<T>        items;
        private Map<String, T> mapping;
    }

    // 具体实现类
    static class StringContainer extends Container<String> {
    }

    @Test
    public void test() {
        System.out.println(MockDataGenerator.getJsonMock(StringContainer.class, MockOptions.defaultOptions()));
    }

    public static void main(String[] args) {
        System.out.println(MockDataGenerator.getJsonMock(MyAIRequestCmd.class, MockOptions.defaultOptions()));
        System.out.println(MockDataGenerator.getJsonMock(new TypeReference<AIRequestCmd<HelloCmd>>() {
        }, MockOptions.defaultOptions()));
    }

    public static class AIRequestCmd<T> {
        /**
         * 事件名称
         */
        private String actionName;
        /**
         * 事件Body
         */
        private T      body;
    }

    public static class MyAIRequestCmd extends AIRequestCmd<HelloCmd> {

    }

    public static class HelloCmd {

        private Boolean              status;
        private String[]             types;
        private List<String>         names;
        private List<Long>           ids;
        private Map<String, Boolean> map;
    }
}
