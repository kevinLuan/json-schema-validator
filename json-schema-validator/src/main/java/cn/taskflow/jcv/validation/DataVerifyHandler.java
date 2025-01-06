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
 * VerifyHandler接口的默认实现，提供验证错误处理。
 * 该处理程序使用JsvUtils中的实用方法格式化验证失败和缺少必需字段的错误消息。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class DataVerifyHandler implements VerifyHandler {
    /**
     * 获取DataVerifyHandler单例实例的工厂方法
     * @return VerifyHandler的新实例
     */
    public static VerifyHandler getInstance() {
        return new DataVerifyHandler();
    }

    /**
     * 格式化指定JSON路径的验证失败错误消息
     * @param path 验证失败的JSON路径
     * @return 验证失败的格式化错误消息
     */
    @Override
    public String getTipError(String path) {
        return JsvUtils.formatParamError(path);
    }

    /**
     * 格式化指定JSON路径的缺少必需字段的错误消息
     * @param path 缺少必需字段的JSON路径
     * @return 缺少字段的格式化错误消息
     */
    @Override
    public String getTipMissing(String path) {
        return JsvUtils.formatParamMissing(path);
    }
}
