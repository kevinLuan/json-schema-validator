package cn.taskflow.jcv.codegen;

import cn.taskflow.jcv.encode.GsonEncoder;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * 根据Java类定义生成Mock对象
 */
public class MockDataGenerator {
    private static final Faker faker = new Faker();

    public interface MockValueGenerator {
        Object generate(Class<?> type, Type genericType, Set<Class<?>> visitedClasses);
    }

    /**
     * Interface for custom mock value generators.
     */
    private static MockValueGenerator customGenerator = (type, genericType, visitedClasses) -> null;

    /**
     * 设置自定义的模拟值生成器。
     *
     * @param generator 自定义生成器。
     */
    public static void setCustomMockValueGenerator(MockValueGenerator generator) {
        customGenerator = generator;
    }

    /**
     * 生成指定类的JSON格式模拟数据。
     *
     * @param clazz 要生成模拟数据的类。
     * @return JSON格式的字符串。
     */
    @SneakyThrows
    public static String getJsonMock(Class<?> clazz) {
        Set<Class<?>> visitedClasses = new HashSet<>();
        Object instance = generateMockInstance(clazz, visitedClasses);
        return GsonEncoder.INSTANCE.encode(instance);
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
        visitedClasses.add(clazz);
        try {
            Object instance = createInstance(clazz, visitedClasses);
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = generateMockValue(field.getType(), field.getGenericType(), visitedClasses);
                field.set(instance, value);
            }
            return instance;
        } finally {
            visitedClasses.remove(clazz);
        }
    }

    private static Object createInstance(Class<?> clazz, Set<Class<?>> visitedClasses) {
        try {
            Constructor[] constructors = clazz.getDeclaredConstructors();
            Object instance;
            if (Arrays.stream(constructors).anyMatch(c -> c.getParameterCount() == 0)) {
                instance = clazz.getDeclaredConstructor().newInstance();
            } else {
                Class<?>[] paramTypes = constructors[0].getParameterTypes();
                Type[] types = constructors[0].getGenericParameterTypes();
                Object[] args = new Object[constructors[0].getParameterCount()];
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = generateMockValue(paramTypes[i], types[i], visitedClasses);
                }
                instance = constructors[0].newInstance(args);
            }
            return instance;
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e, "createInstance(" + clazz.getName() + ") ERROR");
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
        int arrayLength = 3; // 示例长度
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
                for (int i = 0; i < 3; i++) { // 生成几个元素
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
                for (int i = 0; i < 3; i++) { // 生成几个条目
                    Object key = generateMockValue(keyType, keyType, visitedClasses);
                    Object value = generateMockValue(valueType, valueType, visitedClasses);
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
