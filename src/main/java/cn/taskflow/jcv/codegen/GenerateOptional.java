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

/**
 * The GenerateOptional class is used to specify options for generating code from JSON schemas.
 * It allows configuration of whether to generate example values, descriptions, and whether fields are required.
 * This class provides constructors to initialize these options and getter methods to access their values.
 * 
 * GenerateOptional类用于指定从JSON模式生成代码的选项。
 * 它允许配置是否生成示例值、描述以及字段是否为必需。
 * 该类提供构造函数来初始化这些选项，并提供getter方法来访问它们的值。
 * 
 * Usage:
 * - Create an instance of GenerateOptional with specific options for code generation.
 * - Use the getter methods to check the configured options.
 * 
 * 用法：
 * - 使用特定的代码生成选项创建GenerateOptional的实例。
 * - 使用getter方法检查配置的选项。
 * 
 * Example:
 * <pre>
 *     GenerateOptional options = new GenerateOptional(true, false, true);
 *     boolean generateExample = options.isGenerateExample();
 * </pre>
 * 
 * 示例：
 * <pre>
 *     GenerateOptional options = new GenerateOptional(true, false, true);
 *     boolean generateExample = options.isGenerateExample();
 * </pre>
 * 
 * @see CodeGenerationUtils
 * @since 2024-09-26
 * 
 * Author: SHOUSHEN.LUAN
 * 作者: SHOUSHEN.LUAN
 */
public class GenerateOptional {
    /** Flag to indicate whether to generate example values */
    /** 标志指示是否生成示例值 */
    private boolean generateExample;
    /** Flag to indicate whether to generate descriptions */
    /** 标志指示是否生成描述 */
    private boolean generateDesc;
    /** Flag to indicate whether fields are required */
    /** 标志指示字段是否为必需 */
    private boolean require;

    /**
     * Constructs a GenerateOptional instance with specified options.
     *
     * 构造一个具有指定选项的GenerateOptional实例。
     *
     * @param generateExample whether to generate example values
     * @param generateDesc whether to generate descriptions
     * @param require whether fields are required
     *
     * @param generateExample 是否生成示例值
     * @param generateDesc 是否生成描述
     * @param require 字段是否为必需
     */
    public GenerateOptional(boolean generateExample, boolean generateDesc, boolean require) {
        this.generateExample = generateExample;
        this.generateDesc = generateDesc;
        this.require = require;
    }

    /**
     * Constructs a GenerateOptional instance with default options.
     * By default, fields are set to be required.
     *
     * 构造一个具有默认选项的GenerateOptional实例。
     * 默认情况下，字段被设置为必需。
     */
    public GenerateOptional() {
        require = true;
    }

    /**
     * Checks if example values should be generated.
     *
     * 检查是否应生成示例值。
     *
     * @return true if example values should be generated, false otherwise
     * @return 如果应生成示例值，则返回true，否则返回false
     */
    public boolean isGenerateExample() {
        return generateExample;
    }

    /**
     * Checks if descriptions should be generated.
     *
     * 检查是否应生成描述。
     *
     * @return true if descriptions should be generated, false otherwise
     * @return 如果应生成描述，则返回true，否则返回false
     */
    public boolean isGenerateDesc() {
        return generateDesc;
    }

    /**
     * Checks if fields are required.
     *
     * 检查字段是否为必需。
     *
     * @return true if fields are required, false otherwise
     * @return 如果字段为必需，则返回true，否则返回false
     */
    public boolean isRequire() {
        return require;
    }
}
