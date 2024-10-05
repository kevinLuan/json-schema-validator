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

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class Primitive extends JsonBasicSchema {

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

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public Primitive setMin(Number min) {
        this.min = min;
        this.check();
        return this;
    }

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

    public Primitive between(Number min, Number max) {
        this.setMin(min);
        this.setMax(max);
        return this;
    }

    public Primitive setMax(Number max) {
        this.max = max;
        this.check();
        return this;
    }

    public String getTipMsg() {
        return this.getTipMsg(getPath());
    }

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
     * 存在范围验证
     *
     * @return
     */
    public boolean existBetweenCheck() {
        return max != null || min != null;
    }

    /**
     * 获取示例原始值
     *
     * @return
     */
    public String getExampleValue() {
        return exampleValue;
    }

    /**
     * 设置示例值(可以用户mock数据)
     *
     * @param exampleValue
     * @return
     */
    public Primitive setExampleValue(Object exampleValue) {
        super.exampleValue = String.valueOf(exampleValue);
        return this;
    }
}
