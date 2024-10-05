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

public class NumberParser {

    public boolean isDouble;
    public boolean isLong;
    public Number  value;

    public boolean isNull() {
        return this.value == null;
    }

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
