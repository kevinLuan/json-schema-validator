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

import cn.taskflow.jcv.test.SchemaTest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-02-26
 */
public class IOUtils {
    public static String readFile(String name) throws IOException {
        String path = SchemaTest.class.getResource("").getPath();
        int index = path.indexOf("/target/test-classes/");
        String baseDir = path.substring(0, index);
        File filePath = Paths.get(baseDir).resolve("src").resolve("test").resolve("resources").resolve(name)
            .toAbsolutePath().toFile();
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(filePath)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }

    public static String readJson(Class<?> testType, String suffixName) throws IOException {
        String json = readFile(testType.getSimpleName() + "_" + suffixName + ".json");
        return JsonParser.parseString(json).toString();
    }
}
