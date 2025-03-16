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

/**
 * @author kevin.luan
 * @since 2025-03-16
 */
public class PatternConstants {

    // 手机号正则表达式：中国大陆手机号，以1开头，后面跟随10位数字
    public static final String PHONE                  = "^1[0-9]{10}$";

    // 电子邮件地址正则表达式：简单验证格式
    public static final String EMAIL                  = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // 字母限定（仅包含字母），长度从1到32
    public static final String LETTERS_1_TO_32        = "^[a-zA-Z]{1,32}$";

    // 字母限定（仅包含字母），长度从1到64
    public static final String LETTERS_1_TO_64        = "^[a-zA-Z]{1,64}$";

    // 字母数字组合，长度从1到32
    public static final String LETTERS_DIGITS_1_TO_32 = "^[a-zA-Z0-9]{1,32}$";

    // 字母数字组合，长度从1到64
    public static final String LETTERS_DIGITS_1_TO_64 = "^[a-zA-Z0-9]{1,64}$";
}