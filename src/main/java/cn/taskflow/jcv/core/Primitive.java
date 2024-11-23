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
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.JsvUtils;
import cn.taskflow.jcv.validation.CustomValidationRule;
import cn.taskflow.jcv.validation.ValueRangeValidation;

/**
 * 表示一个基本参数，这是参数的最小单位。
 * 该类扩展了JsonBasicSchema，并提供了处理具有可选范围验证的基本数据类型的附加功能。
 *
 * @作者 KEVIN LUAN
 */
public class Primitive extends JsonBasicSchema {

    /**
     * 使用指定的属性构造一个新的Primitive实例。
     *
     * @param name        基本参数的名称
     * @param require     基本参数是否必需
     * @param dataType    基本参数的数据类型
     * @param description 基本参数的描述
     * @param min         验证的最小值
     * @param max         验证的最大值
     * @throws IllegalArgumentException 如果dataType不是基本类型
     */
    public Primitive(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description);
        if (!DataType.isPrimitive(dataType)) {
            throw new IllegalArgumentException("Unsupported dataType: " + dataType);
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Primitive asPrimitive() {
        return this;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    /**
     * 获取验证的最小值。
     *
     * @return 最小值
     */
    public Number getMin() {
        return min;
    }

    /**
     * 获取验证的最大值。
     *
     * @return 最大值
     */
    public Number getMax() {
        return max;
    }

    /**
     * 设置验证的最小值并执行检查。
     *
     * @param min 要设置的最小值
     * @return 当前的Primitive实例
     */
    public Primitive setMin(Number min) {
        this.min = min;
        this.check();
        return this;
    }

    /**
     * 验证min和max值以确保它们在有效范围内。
     * 如果范围无效，则抛出ValidationException。
     */
    private void check() {
        if (min != null && max != null) {
            if (String.valueOf(min).indexOf(".") != -1 || String.valueOf(max).indexOf(".") != -1) {
                if (min.doubleValue() > max.doubleValue()) {
                    throw new ValidationException("Validation parameters error. `" + max + "` must gt `" + min + "`",
                        getPath());
                }
            } else {
                if (min.longValue() > max.longValue()) {
                    throw new ValidationException("Validation parameters error. `" + max + "` must gt `" + min + "`",
                        getPath());
                }
            }
        }
    }

    /**
     * 设置验证的最小值和最大值。
     *
     * @param min 要设置的最小值
     * @param max 要设置的最大值
     * @return 当前的Primitive实例
     */
    public Primitive between(Number min, Number max) {
        this.setMin(min);
        this.setMax(max);
        return this;
    }

    /**
     * 设置验证的最大值并执行检查。
     *
     * @param max 要设置的最大值
     * @return 当前的Primitive实例
     */
    public Primitive setMax(Number max) {
        this.max = max;
        this.check();
        return this;
    }

    /**
     * 获取当前路径的提示信息。
     *
     * @return 提示信息
     */
    public String getTipMsg() {
        return this.getTipMsg(getPath());
    }

    /**
     * 根据数据类型和范围获取指定路径的提示信息。
     *
     * @param path 要获取提示信息的路径
     * @return 格式化的提示信息
     */
    public String getTipMsg(String path) {
        if (getDataType().isNumber()) {
            if (this.min != null && this.max != null) {
                return JsvUtils.formatBetween(path, min, max);
            } else if (this.min != null) {
                return JsvUtils.formatBetweenGtOrEq(path, min);
            } else if (this.max != null) {
                return JsvUtils.formatBetweenLtOrEq(path, max);
            } else {
                return JsvUtils.mustBeNumber(path);
            }
        } else if (getDataType().isString()) {
            if (this.min != null && this.max != null) {
                return JsvUtils.formatBetweenLength(path, min, max);
            } else if (this.min != null) {
                return JsvUtils.formatBetweenLengthGtOrEq(path, min);
            } else if (this.max != null) {
                return JsvUtils.formatBetweenLengthLtOrEq(path, max);
            }
        }
        return JsvUtils.formatParamError(path);
    }

    /**
     * 检查是否存在范围验证。
     *
     * @return 如果min或max不为null，则返回true，否则返回false
     */
    public boolean existBetweenCheck() {
        return max != null || min != null;
    }

    /**
     * 获取此基本参数的示例值。
     *
     * @return 示例值作为字符串
     */
    public String getExampleValue() {
        return exampleValue;
    }

    /**
     * 为此基本参数设置示例值，可用于模拟数据。
     *
     * @param exampleValue 要设置的示例值
     * @return 当前的Primitive实例
     */
    public Primitive setExampleValue(Object exampleValue) {
        super.exampleValue = String.valueOf(exampleValue);
        return this;
    }

    /**
     * 限定数据范围
     *
     * @param values
     * @return
     */
    public Primitive withinValues(Object... values) {
        for (CustomValidationRule validationRule : validationRules) {
            if (validationRule.getClass() == ValueRangeValidation.class) {
                ((ValueRangeValidation) validationRules).addWithinValues(values);
                return this;
            }
        }
        validationRules.add(ValueRangeValidation.fromWithinValues(values));
        return this;
    }

    /**
     * 排除数据范围
     *
     * @param values
     * @return
     */
    public Primitive excludeValues(Object... values) {
        for (CustomValidationRule validationRule : validationRules) {
            if (validationRule.getClass() == ValueRangeValidation.class) {
                ((ValueRangeValidation) validationRules).addExcludeValues(values);
                return this;
            }
        }
        validationRules.add(ValueRangeValidation.fromExcludeValues(values));
        return this;
    }
}
