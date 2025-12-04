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

import cn.taskflow.jcv.encode.GsonEncoder;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * Mock数据生成器
 * 负责根据解析的类型生成相应的模拟数据
 */
public class MockDataGenerator {
    private static final Logger log = LoggerFactory.getLogger(MockDataGenerator.class);
    private static final Faker faker = new Faker();
    private static final ThreadLocal<MockOptions> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 自定义Mock值生成器接口
     */
    public interface MockValueGenerator {
        Object generate(Class<?> type, Type genericType, Set<Class<?>> visitedClasses);
    }

    /**
     * 自定义实例生成器接口
     */
    public interface InstanceGenerator {
        Object generate(Class<?> type, Set<Class<?>> visitedClasses);
    }

    private static MockValueGenerator customGenerator = (type, genericType, visitedClasses) -> null;
    private static InstanceGenerator instanceGenerator = (type, visitedClasses) -> null;

    /**
     * 设置自定义的模拟值生成器
     */
    public static void setCustomMockValueGenerator(MockValueGenerator generator) {
        customGenerator = generator;
    }

    /**
     * 设置自定义的实例生成器
     */
    public static void setInstanceGenerator(InstanceGenerator generator) {
        instanceGenerator = generator;
    }

    /**
     * 生成指定类的JSON格式模拟数据
     */
    @SneakyThrows
    public static String getJsonMock(Class<?> clazz, MockOptions options) {
        try {
            THREAD_LOCAL.set(options);
            Set<Class<?>> visitedClasses = new HashSet<>();
            Object instance = generateMockInstance(clazz, visitedClasses);
            return GsonEncoder.INSTANCE.encode(instance);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 生成指定TypeReference的JSON格式模拟数据
     */
    public static <T> String getJsonMock(TypeReference<T> typeReference, MockOptions options) {
        try {
            THREAD_LOCAL.set(options);
            Set<Class<?>> visitedClasses = new HashSet<>();

            // 获取TypeReference中的实际类型
            Type type = typeReference.getType();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                // 创建类型映射
                Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < typeParameters.length; i++) {
                    typeMap.put(typeParameters[i], actualTypeArguments[i]);
                }

                // 生成实例并处理泛型
                Object instance = createInstance(rawType, visitedClasses);
                processFields(instance, rawType, typeMap, visitedClasses);

                return GsonEncoder.INSTANCE.encode(instance);
            }

            return "{}";
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 生成模拟实例
     */
    @SneakyThrows
    private static Object generateMockInstance(Class<?> clazz, Set<Class<?>> visitedClasses) {
        if (shouldSkipInstance(clazz, visitedClasses)) {
            return null;
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return getRandomEnumInstance((Class<? extends Enum>) clazz);
        }

        visitedClasses.add(clazz);
        try {
            Object instance = createInstance(clazz, visitedClasses);

            // 获取完整的类层次结构
            List<Class<?>> classHierarchy = new ArrayList<>();
            Class<?> currentClass = clazz;
            while (currentClass != null && !currentClass.getName().startsWith("java.")) {
                classHierarchy.add(0, currentClass);
                currentClass = currentClass.getSuperclass();
            }

            // 处理每个类的字段
            Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
            buildTypeMap(clazz, typeMap);

            for (Class<?> cls : classHierarchy) {
                for (Field field : cls.getDeclaredFields()) {
                    if (shouldSkipField(field)) {
                        continue;
                    }
                    field.setAccessible(true);

                    Type genericType = field.getGenericType();
                    if (genericType instanceof TypeVariable) {
                        Type actualType = typeMap.get(genericType);
                        if (actualType == null) {
                            actualType = TypeResolver.resolveTypeVariable((TypeVariable<?>) genericType);
                        }
                        Class<?> actualClass = TypeResolver.resolveActualType(actualType);
                        Object value = generateMockValue(actualClass, actualType, visitedClasses);
                        field.set(instance, value);
                    } else {
                        Object value = generateMockValue(field.getType(), genericType, visitedClasses);
                        if (value != null) {
                            field.set(instance, value);
                        }
                    }
                }
            }

            return instance;
        } finally {
            visitedClasses.remove(clazz);
        }
    }

    /**
     * 构建类型映射
     */
    private static void buildTypeMap(Class<?> clazz, Map<TypeVariable<?>, Type> typeMap) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            for (int i = 0; i < typeParameters.length; i++) {
                typeMap.put(typeParameters[i], actualTypeArguments[i]);
            }

            // 递归处理父类
            buildTypeMap(rawType, typeMap);
        }
    }

    /**
     * 生成模拟值
     */
    private static Object generateMockValue(Class<?> type, Type genericType, Set<Class<?>> visitedClasses) {
        // 处理自定义生成器
        Object customValue = customGenerator.generate(type, genericType, visitedClasses);
        if (customValue != null) {
            return customValue;
        }

        // 处理数组类型
        if (type.isArray()) {
            return generateMockArray(type.getComponentType(), visitedClasses);
        }

        // 处理集合类型
        if (Collection.class.isAssignableFrom(type)) {
            return generateMockCollection((Class<? extends Collection>) type, genericType, visitedClasses);
        }

        // 处理Map类型
        if (Map.class.isAssignableFrom(type)) {
            return generateMockMap((Class<? extends Map>) type, genericType, visitedClasses);
        }

        // 处理基本类型
        if (type.isPrimitive() || type == String.class ||
                Number.class.isAssignableFrom(type) ||
                Boolean.class == type) {
            return generateBasicTypeValue(type);
        }

        // 处理复杂类型
        if (!type.isPrimitive() && !type.getName().startsWith("java.")) {
            return generateMockInstance(type, visitedClasses);
        }

        return null;
    }

    /**
     * 生成基本类型的模拟值
     */
    private static Object generateBasicTypeValue(Class<?> type) {
        if (type == String.class) {
            return faker.lorem().word();
        } else if (type == Boolean.class || type == boolean.class) {
            return faker.bool().bool();
        } else if (type == Integer.class || type == int.class) {
            return faker.number().numberBetween(0, 100);
        } else if (type == Long.class || type == long.class) {
            return faker.number().randomNumber();
        } else if (type == Double.class || type == double.class) {
            return faker.number().randomDouble(2, 0, 100);
        } else if (type == Float.class || type == float.class) {
            return (float) faker.number().randomDouble(2, 0, 100);
        } else if (type == Short.class || type == short.class) {
            return (short) faker.number().numberBetween(0, 100);
        } else if (type == Byte.class || type == byte.class) {
            return (byte) faker.number().numberBetween(0, 100);
        } else if (type == Character.class || type == char.class) {
            return faker.lorem().character();
        } else if (type == BigDecimal.class) {
            return BigDecimal.valueOf(faker.number().randomDouble(2, 0, 100));
        } else if (type == BigInteger.class) {
            return BigInteger.valueOf(faker.number().randomNumber());
        } else if (Date.class.isAssignableFrom(type)) {
            return faker.date().birthday();
        } else if (type == Timestamp.class) {
            return new Timestamp(faker.date().birthday().getTime());
        }
        return null;
    }

    private static <E extends Enum<E>> E getRandomEnumInstance(Class<E> enumClass) {
        if (Enum.class.isAssignableFrom(enumClass)) {
            E[] enumConstants = enumClass.getEnumConstants();
            int randomIndex = new Random().nextInt(enumConstants.length);
            return enumConstants[randomIndex];
        }
        throw new IllegalArgumentException("Provided class is not an enum type");
    }

    /**
     * 获取类及其所有父类的字段
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && !clazz.equals(Object.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Object createInstance(Class<?> clazz, Set<Class<?>> visitedClasses) {
        try {
            Constructor[] constructors = clazz.getDeclaredConstructors();
            Object instance;
            if (Arrays.stream(constructors).anyMatch(c -> c.getParameterCount() == 0 && Modifier.isPublic(c.getModifiers()))) {
                instance = clazz.getDeclaredConstructor().newInstance();
            } else {
                Optional<Constructor> optional = Arrays.stream(constructors).filter((c) -> Modifier.isPublic(c.getModifiers())).findAny();
                Constructor constructor;
                if (optional.isPresent()) {
                    constructor = optional.get();
                } else {
                    constructor = constructors[0];
                    try {
                        constructor.setAccessible(true);
                    } catch (Exception e) {
                        instance = instanceGenerator.generate(clazz, visitedClasses);
                        if (instance == null) {
                            throw new UndeclaredThrowableException(e, "createInstance(" + clazz.getName() + ") ERROR");
                        }
                        return instance;
                    }
                }
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Type[] types = constructor.getGenericParameterTypes();
                Object[] args = new Object[constructor.getParameterCount()];
                for (int i = 0; i < paramTypes.length; i++) {
                    if (types.length > 0) {
                        args[i] = generateMockValue(paramTypes[i], types[i], visitedClasses);
                    } else {
                        args[i] = generateMockValue(paramTypes[i], null, visitedClasses);
                    }
                }
                instance = constructor.newInstance(args);
            }
            return instance;
        } catch (Exception e) {
            Object instance = instanceGenerator.generate(clazz, visitedClasses);
            if (instance == null) {
                throw new UndeclaredThrowableException(e, "createInstance(" + clazz.getName() + ") ERROR");
            }
            return instance;
        }
    }

    /**
     * 判断是否应该跳过实例生成
     */
    private static boolean shouldSkipInstance(Class<?> clazz, Set<Class<?>> visitedClasses) {
        return visitedClasses.contains(clazz) || clazz.isInterface();
    }

    /**
     * 判断是否应该跳过字段处理
     */
    private static boolean shouldSkipField(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
    }

    /**
     * 生成数组类的模拟数据。
     *
     * @param componentType 数组的组件类型。
     * @return 数组的模拟数据。
     */
    private static Object generateMockArray(Class<?> componentType, Set<Class<?>> visitedClasses) {
        int arrayLength = getOptions().getArraySize(); // 例长度
        Object array = Array.newInstance(componentType, arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            Object element = generateMockValue(componentType, componentType, visitedClasses);
            Array.set(array, i, element);
        }
        return array;
    }

    /**
     * 生成集合类型的模拟数据。
     *
     * @param collectionType 集合的类型。
     * @param genericType    集合的泛型类型。
     * @return 集合的模拟数据。
     */
    private static Collection<?> generateMockCollection(Class<? extends Collection> collectionType, Type genericType, Set<Class<?>> visitedClasses) {
        Collection<Object> collection;
        // 创建具体的集合实例
        if (List.class.isAssignableFrom(collectionType)) {
            collection = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            collection = new HashSet<>();
        } else {
            try {
                collection = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                collection = new ArrayList<>();
            }
        }

        // 获取元素类型
        Class<?> elementType = Object.class;
        Type actualElementType = Object.class;
        
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0) {
                actualElementType = typeArgs[0];
                if (actualElementType instanceof Class) {
                    elementType = (Class<?>) actualElementType;
                } else if (actualElementType instanceof ParameterizedType) {
                    elementType = (Class<?>) ((ParameterizedType) actualElementType).getRawType();
                }
            }
        }

        // 生成元素
        int size = getOptions().getArraySize();
        for (int i = 0; i < size; i++) {
            Object element = generateMockValue(elementType, actualElementType, visitedClasses);
            if (element != null) {
                collection.add(element);
            }
        }

        return collection;
    }

    /**
     * 生成映射类型的模拟数据。
     *
     * @param mapType     映射的类型。
     * @param genericType 映射的泛型类型。
     * @return 映射的模拟数据。
     */
    private static Map<?, ?> generateMockMap(Class<? extends Map> mapType, Type genericType, Set<Class<?>> visitedClasses) {
        Map<Object, Object> map = new HashMap<>();

        // 获取键值类型
        Class<?> keyType = String.class;
        Class<?> valueType = Object.class;
        Type actualValueType = Object.class;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 1) {
                // 获取值类型
                Type valueTypeArg = typeArgs[1];
                actualValueType = valueTypeArg;
                if (valueTypeArg instanceof Class) {
                    valueType = (Class<?>) valueTypeArg;
                } else if (valueTypeArg instanceof ParameterizedType) {
                    valueType = (Class<?>) ((ParameterizedType) valueTypeArg).getRawType();
                }
            }
        }

        // 生成键值对
        int size = getOptions().getMapSize();
        for (int i = 0; i < size; i++) {
            String key = getOptions().getMapKeyPrefix() + i;
            Object value = generateMockValue(valueType, actualValueType, visitedClasses);
            if (value != null) {
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * 获取当前线程的MockOptions配置
     */
    private static MockOptions getOptions() {
        return THREAD_LOCAL.get() != null ? THREAD_LOCAL.get() : MockOptions.defaultOptions();
    }

    /**
     * 处理集合类型字段
     */
    @SneakyThrows
    private static void processCollectionField(Object instance, Field field,
                                               Class<?> collectionType,
                                               Type elementType,
                                               Set<Class<?>> visitedClasses) {
        Collection<Object> collection;
        if (List.class.isAssignableFrom(collectionType)) {
            collection = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            collection = new HashSet<>();
        } else {
            try {
                collection = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                collection = new ArrayList<>();
            }
        }

        // 解析元素类型
        Class<?> actualElementType = TypeResolver.resolveActualType(elementType);

        // 生成集合元素
        for (int i = 0; i < getOptions().getArraySize(); i++) {
            Object element = generateMockValue(actualElementType, elementType, visitedClasses);
            if (element != null) {
                collection.add(element);
            }
        }

        field.set(instance, collection);
    }

    /**
     * 处理Map类型字段
     */
    @SneakyThrows
    private static void processMapField(Object instance, Field field,
                                        Class<?> mapType,
                                        Type valueType,
                                        Set<Class<?>> visitedClasses) {
        Map<String, Object> map = new HashMap<>();

        // 解析值类型
        Class<?> actualValueType = TypeResolver.resolveActualType(valueType);

        // 生成Map元素
        for (int i = 0; i < getOptions().getMapSize(); i++) {
            String key = getOptions().getMapKeyPrefix() + i;
            Object value = generateMockValue(actualValueType, valueType, visitedClasses);
            if (value != null) {
                map.put(key, value);
            }
        }

        field.set(instance, map);
    }

    /**
     * 处理字段，包括泛型字段
     */
    @SneakyThrows
    private static void processFields(Object instance, Class<?> clazz,
                                      Map<TypeVariable<?>, Type> typeMap,
                                      Set<Class<?>> visitedClasses) {
        // 获取完整的类层次结构
        List<Class<?>> classHierarchy = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && !currentClass.getName().startsWith("java.")) {
            classHierarchy.add(0, currentClass);
            currentClass = currentClass.getSuperclass();
        }

        // 处理每个类的字段
        for (Class<?> cls : classHierarchy) {
            for (Field field : cls.getDeclaredFields()) {
                if (shouldSkipField(field)) {
                    continue;
                }
                try {
                    generateFieldMockData(instance, typeMap, visitedClasses, field);
                } catch (Exception e) {
                    log.error("field:{} 生成 mock 数据出错",field.getName(),e);
                }
            }
        }
    }

    private static void generateFieldMockData(Object instance, Map<TypeVariable<?>, Type> typeMap, Set<Class<?>> visitedClasses, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Type genericType = field.getGenericType();
        if (genericType instanceof TypeVariable) {
            // 处理泛型字段
            Type actualType = typeMap.get(genericType);
            if (actualType != null) {
                Object value;
                if (actualType instanceof Class) {
                    value = generateMockValue((Class<?>) actualType, actualType, visitedClasses);
                } else if (actualType instanceof ParameterizedType) {
                    Class<?> rawType = (Class<?>) ((ParameterizedType) actualType).getRawType();
                    value = generateMockValue(rawType, actualType, visitedClasses);
                } else {
                    value = generateMockValue(Object.class, actualType, visitedClasses);
                }
                field.set(instance, value);
            }
        } else if (genericType instanceof ParameterizedType) {
            // 处理参数化类型字段
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            // 创建新的类型映射
            Map<TypeVariable<?>, Type> fieldTypeMap = new HashMap<>(typeMap);
            TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                if (actualTypeArguments[i] instanceof TypeVariable) {
                    Type actualType = typeMap.get(actualTypeArguments[i]);
                    if (actualType != null) {
                        fieldTypeMap.put(typeParameters[i], actualType);
                    }
                } else {
                    fieldTypeMap.put(typeParameters[i], actualTypeArguments[i]);
                }
            }

            Object value = generateMockValue(rawType, parameterizedType, visitedClasses);
            if (value != null) {
                processFields(value, rawType, fieldTypeMap, visitedClasses);
                field.set(instance, value);
            }
        } else {
            // 处理普通字段
            Object value = generateMockValue(field.getType(), genericType, visitedClasses);
            if (value != null) {
                field.set(instance, value);
            }
        }
    }
}
