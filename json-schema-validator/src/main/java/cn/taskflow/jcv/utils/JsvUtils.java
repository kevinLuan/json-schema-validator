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

import cn.taskflow.jcv.exception.ValidationException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-05
 */
public class JsvUtils {
    public static String formatStringArgs(String name) {
        return name == null ? null : "\"" + name + "\"";
    }

    public static ValidationException throwParamException(String name) {
        throw new ValidationException(formatParamError(name), name);
    }

    public static ValidationException throwMissingParamException(String name) {
        throw new ValidationException(formatParamMissing(name), name);
    }

    public static String formatParamError(String path) {
        if (StringUtils.isBlank(path)) {
            return "Parameter error";
        }
        return "`" + path + "` parameter error";
    }

    public static String formatParamMissing(String path) {
        if (StringUtils.isBlank(path)) {
            return "Missing parameter";
        }
        return "Missing `" + path + "` parameter";
    }

    public static String formatBetween(String path, Number min, Number max) {
        return "`" + path + "` between [" + min + " ~ " + max + "]";
    }

    public static String formatBetweenGtOrEq(String path, Number min) {
        return "`" + path + "` greater than or equal to " + min;
    }

    public static String formatBetweenLtOrEq(String path, Number max) {
        return "`" + path + "` less than or equal to " + max;
    }

    public static String mustBeNumber(String path) {
        return "`" + path + "` It has to be a number";
    }

    public static String formatBetweenLength(String path, Number min, Number max) {
        return "`" + path + "` between character size [ " + min + "~" + max + " ]";
    }

    public static String formatBetweenLengthGtOrEq(String path, Number min) {
        return "`" + path + "` greater than or equal to character size " + min;
    }

    public static String formatBetweenLengthLtOrEq(String path, Number max) {
        return "`" + path + "` less than or equal to character size " + max;
    }

    public static ClassCastException newClassCastException(Class<?> src, Class<?> dest) {
        throw new ClassCastException(src.getName() + " cannot be cast to " + dest.getName());
    }

    public static String f(String format, Object... args) {
        return String.format(format, args);
    }
}
