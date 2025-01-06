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

import cn.taskflow.jcv.utils.JsvUtils;

/**
 * ArgumentVerifyHandler 是 VerifyHandler 接口的实现。
 * 它提供了生成验证失败和缺少必需字段的错误消息的方法。
 * 该类使用 JsvUtils 的实用方法来格式化这些错误消息。
 * 它遵循单例模式，以确保在整个应用程序中只使用一个实例。
 * 
 * 用法：
 * - 使用 getInstance() 获取 ArgumentVerifyHandler 的单例实例。
 * - 调用 getTipError() 获取格式化的验证失败错误消息。
 * - 调用 getTipMissing() 获取格式化的缺少必需字段的错误消息。
 * 
 * 示例：
 * <pre>
 *     VerifyHandler handler = ArgumentVerifyHandler.getInstance();
 *     String error = handler.getTipError("$.name");
 *     String missing = handler.getTipMissing("$.age");
 * </pre>
 * 
 * @see VerifyHandler
 * @see JsvUtils
 * 
 * @since 2024-02-04
 */
public class ArgumentVerifyHandler implements VerifyHandler {
    /**
     * 获取 ArgumentVerifyHandler 单例实例的工厂方法。
     * 
     * @return VerifyHandler 的单例实例。
     */
    public static VerifyHandler getInstance() {
        return new ArgumentVerifyHandler();
    }

    /**
     * 格式化指定 JSON 路径的验证失败错误消息。
     * 
     * @param path 验证失败的 JSON 路径。
     * @return 格式化的验证失败错误消息。
     */
    @Override
    public String getTipError(String path) {
        return JsvUtils.formatParamError(path);
    }

    /**
     * 格式化指定 JSON 路径的缺少必需字段的错误消息。
     * 
     * @param path 缺少必需字段的 JSON 路径。
     * @return 格式化的缺少字段错误消息。
     */
    @Override
    public String getTipMissing(String path) {
        return JsvUtils.formatParamMissing(path);
    }
}
