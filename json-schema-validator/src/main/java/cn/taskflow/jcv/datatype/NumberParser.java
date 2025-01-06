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
package cn.taskflow.jcv.datatype;

import cn.taskflow.jcv.core.Primitive;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.JsvUtils;

/**
 * NumberParser类负责将数字的字符串表示解析为Double或Long类型，并提供针对指定约束的验证。
 */
public class NumberParser {

    // 表示解析的数字是否为Double类型
    public boolean isDouble;
    // 表示解析的数字是否为Long类型
    public boolean isLong;
    // 保存解析的数字值
    public Number  value;

    /**
     * 检查解析的数字值是否为null。
     * 
     * @return 如果值为null则返回true，否则返回false。
     */
    public boolean isNull() {
        return this.value == null;
    }

    /**
     * 将给定的字符串解析为NumberParser对象。根据小数点的存在确定数字是Double还是Long。
     * 
     * @param value   要解析的数字的字符串表示。
     * @param require 一个布尔标志，指示该值是否为必需。
     * @return 包含解析数字的NumberParser对象。
     */
    public static NumberParser parse(String value, boolean require) {
        NumberParser numberParser = new NumberParser();
        if (value != null) {
            if (value.indexOf(".") != -1) {
                numberParser.value = Double.parseDouble(value);
                numberParser.isDouble = true;
            } else {
                numberParser.value = Long.parseLong(value);
                numberParser.isLong = true;
            }
        }
        return numberParser;
    }

    /**
     * 根据给定Primitive对象中定义的约束验证解析的数字。如果值不符合所需的约束，则抛出ValidationException。
     * 
     * @param primitive 包含验证约束的Primitive对象。
     */
    public void check(Primitive primitive) {
        if (primitive.isRequired()) {
            if (value == null) {
                throw JsvUtils.throwMissingParamException(primitive.getName());
            }
        } else {
            if (value == null) {
                return;
            }
        }
        if (isDouble) {
            if (primitive.getMin() != null) {
                if (primitive.getMin().doubleValue() > value.doubleValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().doubleValue() < value.doubleValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
        } else {
            if (primitive.getMin() != null) {
                if (primitive.getMin().longValue() > value.longValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().longValue() < value.longValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
        }
    }

}
