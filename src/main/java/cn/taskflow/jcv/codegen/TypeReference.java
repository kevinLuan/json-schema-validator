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
package cn.taskflow.jcv.codegen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用于捕获和传递完整泛型类型信息的抽象类。
 * 该类利用Java的参数化类型能力在运行时保留泛型类型信息。
 * 它对于需要了解泛型类型的操作非常有用，例如JSON序列化和反序列化。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-12-02
 * @param <T> 由此TypeReference捕获的类型参数
 */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {
    /**
     * 由此TypeReference捕获的类型。
     */
    private final Type type;

    /**
     * 构造一个新的TypeReference并捕获超类的类型参数。
     * 此构造函数从必须参数化的超类中提取实际的类型参数。
     * 如果超类未参数化，此构造函数将抛出IllegalArgumentException。
     */
    protected TypeReference() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (!(superClass instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                "Internal error: The TypeReference is constructed without actual type information");
        }
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    /**
     * 返回捕获的类型。
     * @return 由此TypeReference捕获的实际类型
     */
    public Type getType() {
        return this.type;
    }

    /**
     * 将此TypeReference与另一个进行比较以确定顺序。
     * 目前，此方法不执行任何特定的比较逻辑，始终返回0。
     * 
     * @param o 要与此实例比较的另一个TypeReference
     * @return 表示此对象相对于指定对象的顺序的整数
     */
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}
