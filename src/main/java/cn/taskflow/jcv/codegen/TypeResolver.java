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

import java.lang.reflect.*;

/**
 * 类型解析器
 * 负责解析泛型类型、参数化类型等
 */
public class TypeResolver {

    /**
     * 解析实际类型
     * 支持嵌套泛型类型如 TypeReference<AIRequestCmd<SalesChannelStatusUpdateCmd>>
     */
    public static Class<?> resolveActualType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof TypeVariable) {
            return resolveTypeVariable((TypeVariable<?>) type);
        }
        return Object.class;
    }

    /**
     * 解析TypeVariable的实际类型
     */
    public static Class<?> resolveTypeVariable(TypeVariable<?> typeVariable) {
        GenericDeclaration declaration = typeVariable.getGenericDeclaration();
        if (declaration instanceof Class) {
            Class<?> declaringClass = (Class<?>) declaration;
            Type genericSuperclass = declaringClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                TypeVariable<?>[] typeParameters = declaringClass.getTypeParameters();
                for (int i = 0; i < typeParameters.length; i++) {
                    if (typeParameters[i].getName().equals(typeVariable.getName())) {
                        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[i];
                        if (actualTypeArgument instanceof Class) {
                            return (Class<?>) actualTypeArgument;
                        }
                    }
                }
            }
        }
        return Object.class;
    }

    /**
     * 获取泛型的实际类型
     */
    public static Class<?> getActualType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof TypeVariable) {
            Type resolvedType = resolveTypeVariable((TypeVariable<?>) type);
            return getActualType(resolvedType);
        }
        return Object.class;
    }
}