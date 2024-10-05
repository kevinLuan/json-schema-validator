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

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-26
 */
public class GenerateOptional {
    /*生成示例值*/
    private boolean generateExample;
    /*生成描述*/
    private boolean generateDesc;
    private boolean require;

    public GenerateOptional(boolean generateExample, boolean generateDesc, boolean require) {
        this.generateExample = generateExample;
        this.generateDesc = generateDesc;
        this.require = require;
    }

    public GenerateOptional() {
        require = true;
    }

    public boolean isGenerateExample() {
        return generateExample;
    }

    public boolean isGenerateDesc() {
        return generateDesc;
    }

    public boolean isRequire() {
        return require;
    }
}
