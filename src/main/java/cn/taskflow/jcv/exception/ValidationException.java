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
package cn.taskflow.jcv.exception;

/**
 * ValidationException 是一个自定义异常，继承自 IllegalArgumentException。
 * 它用于指示发生了验证错误，提供错误信息和错误发生的路径。
 * 
 * 这个异常在输入验证至关重要的场景中特别有用，路径信息有助于精确定位数据结构或配置中验证失败的确切位置。
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class ValidationException extends IllegalArgumentException {
    private String path; // 验证错误发生的路径

    /**
     * 构造一个新的 ValidationException，具有指定的详细信息和路径。
     *
     * @param msg  详细信息，供 getMessage() 方法稍后检索。
     * @param path 验证错误发生的路径，供 getPath() 方法稍后检索。
     */
    public ValidationException(String msg, String path) {
        super(msg);
        this.path = path;
    }

    /**
     * 返回验证错误发生的路径。
     *
     * @return 路径作为字符串返回。
     */
    public String getPath() {
        return this.path;
    }
}
