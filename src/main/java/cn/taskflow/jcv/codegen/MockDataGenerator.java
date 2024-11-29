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
 * 根据Java类定义生成Mock对象
 */
public class MockDataGenerator {
    static Logger log = LoggerFactory.getLogger(MockDataGenerator.class);
    private static final Faker faker = new Faker();
    private static final ThreadLocal<MockOptions> THREAD_LOCAL = new ThreadLocal<>();

    public interface MockValueGenerator {
        Object generate(Class<?> type, Type genericType, Set<Class<?>> visitedClasses);
    }

    public interface InstanceGenerator {
        Object generate(Class<?> type, Set<Class<?>> visitedClasses);
    }

    /**
     * Interface for custom mock value generators.
     */
    private static MockValueGenerator customGenerator = (type, genericType, visitedClasses) -> null;
    private static InstanceGenerator instanceGenerator = (type, visitedClasses) -> null;

    /**
     * 设置自定义的模拟值生成器。
     *
     * @param generator 自定义生成器。
     */
    public static void setCustomMockValueGenerator(MockValueGenerator generator) {
        customGenerator = generator;
    }

    public static void setInstanceGenerator(InstanceGenerator generator) {
        instanceGenerator = generator;
    }

    /**
     * 生成指定类的JSON格式模拟数据。
     *
     * @param clazz 要生成模拟数据的类。
     * @return JSON格式的字符串。
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

    private static <E extends Enum<E>> E getRandomEnumInstance(Class<E> enumClass) {
        if (Enum.class.isAssignableFrom(enumClass)) {
            E[] enumConstants = enumClass.getEnumConstants();
            int randomIndex = new Random().nextInt(enumConstants.length);
            return enumConstants[randomIndex];
        }
        throw new IllegalArgumentException("Provided class is not an enum type");
    }

    /**
     * 生成指定类的模拟实例。
     *
     * @param clazz 要生成实例的类。
     * @return 类的实例对象。
     */
    @SneakyThrows
    private static Object generateMockInstance(Class<?> clazz, Set<Class<?>> visitedClasses) {
        if (visitedClasses.contains(clazz)) {
            return null; // 避免循环引用
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return getRandomEnumInstance((Class<? extends Enum>) clazz);
        } else if (clazz.isInterface()) {
            log.info("自动跳过接口实例化:{}", clazz.getName());
            return null;
        }
        visitedClasses.add(clazz);
        try {
            Object instance = createInstance(clazz, visitedClasses);
            if (!clazz.getName().startsWith("java.")) {
                for (Field field : getAllFields(clazz)) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                        continue;//skip
                    }
                    field.setAccessible(true);
                    Object value = generateMockValue(field.getType(), field.getGenericType(), visitedClasses);
                    field.set(instance, value);
                }
            }
            return instance;
        } finally {
            visitedClasses.remove(clazz);
        }
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
     * 根据字段类型生成模拟值。
     *
     * @param type        字段的类型。
     * @param genericType 字段的泛型类型。
     * @return 模拟值。
     */
    private static Object generateMockValue(Class<?> type, Type genericType, Set<Class<?>> visitedClasses) {
        if (type.isArray()) {
            return generateMockArray(type.getComponentType(), visitedClasses);
        } else if (type == String.class) {
            return faker.lorem().word();
        } else if (type == int.class || type == Integer.class) {
            return faker.number().numberBetween(0, 100);
        } else if (type == long.class || type == Long.class) {
            return faker.number().randomNumber();
        } else if (type == double.class || type == Double.class) {
            return faker.number().randomDouble(2, 0, 100);
        } else if (type == boolean.class || type == Boolean.class) {
            return faker.bool().bool();
        } else if (type == float.class || type == Float.class) {
            return (float) faker.number().randomDouble(2, 0, 100);
        } else if (type == short.class || type == Short.class) {
            return (short) faker.number().numberBetween(0, 100);
        } else if (type == byte.class || type == Byte.class) {
            return (byte) faker.number().numberBetween(0, 100);
        } else if (type == char.class || type == Character.class) {
            return faker.lorem().character();
        } else if (Collection.class.isAssignableFrom(type)) {
            return generateMockCollection((Class<? extends Collection>) type, genericType, visitedClasses);
        } else if (Map.class.isAssignableFrom(type)) {
            return generateMockMap((Class<? extends Map>) type, genericType, visitedClasses);
        } else if (type == Timestamp.class) {
            return new Timestamp(faker.date().birthday().getTime());
        } else if (Date.class.isAssignableFrom(type)) {
            return faker.date().birthday();
        } else if (BigInteger.class.isAssignableFrom(type)) {
            return BigInteger.valueOf(faker.number().randomNumber());
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return BigDecimal.valueOf(faker.number().randomDouble(2, 0, 100));
        } else if (!type.isPrimitive()) {
            return generateMockInstance(type, visitedClasses);
        }
        return customGenerator.generate(type, genericType, visitedClasses);
    }

    /**
     * 生成数组类型的模拟数据。
     *
     * @param componentType 数组的组件类型。
     * @return 数组的模拟数据。
     */
    private static Object generateMockArray(Class<?> componentType, Set<Class<?>> visitedClasses) {
        int arrayLength = getOptions().getArraySize(); // 示例长度
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
        if (List.class.isAssignableFrom(collectionType)) {
            collection = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            collection = new HashSet<>();
        } else {
            return Collections.emptyList(); // 默认返回空列表
        }

        if (genericType instanceof ParameterizedType) {
            Type elementType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (elementType instanceof Class) {
                for (int i = 0; i < getOptions().getArraySize(); i++) { // 生成几个元素
                    Object element = generateMockValue((Class<?>) elementType, elementType, visitedClasses);
                    if (element != null) { // 确保非空元素
                        collection.add(element);
                    }
                }
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
        Map<Object, Object> map;
        if (Map.class.isAssignableFrom(mapType)) {
            map = new HashMap<>();
        } else {
            return Collections.emptyMap(); // 默认返回空映射
        }
        if (genericType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArguments.length == 2 && typeArguments[0] instanceof Class && typeArguments[1] instanceof Class) {
                Class<?> keyType = (Class<?>) typeArguments[0];
                Class<?> valueType = (Class<?>) typeArguments[1];
                for (int i = 0; i < getOptions().getMapSize(); i++) {
                    Object key = generateMockValue(keyType, keyType, visitedClasses);
                    Object value = generateMockValue(valueType, valueType, visitedClasses);
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private static MockOptions getOptions() {
        return THREAD_LOCAL.get() == null ? THREAD_LOCAL.get() : MockOptions.defaultOptions();
    }
}
