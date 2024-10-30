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
/*
 * 根据一个或多个贡献者许可协议，授权给Apache软件基金会（ASF）。请参阅此作品的NOTICE文件以获取有关版权所有者的其他信息。
 * ASF根据Apache许可证2.0版（“许可证”）授权您使用此文件；除非符合许可证，否则您不得使用此文件。
 * 您可以在以下位置获取许可证副本：
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则根据许可证分发的软件按“原样”分发，
 * 不提供任何明示或暗示的担保或条件。
 * 请参阅许可证以了解管理权限和限制的特定语言。
 */
package cn.taskflow.jcv.core;

/**
 * @作者 SHOUSHEN.LUAN
 * @自 2024-02-03
 */
public class JsonBoolean extends Primitive {
    public JsonBoolean(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name 参数名称
     * @return JsonBoolean对象
     */
    public static JsonBoolean required(String name) {
        return new JsonBoolean(name, true, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean required(String name, String description) {
        return new JsonBoolean(name, true, DataType.Boolean, description, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @return JsonBoolean对象
     */
    public static JsonBoolean ofNonNull() {
        return new JsonBoolean("", true, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean ofNullable() {
        return new JsonBoolean("", true, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean optional(String name) {
        return new JsonBoolean(name, false, DataType.Boolean, null, null, null);
    }

    public static JsonBoolean optional(String name, String description) {
        return new JsonBoolean(name, false, DataType.Boolean, description, null, null);
    }

}
