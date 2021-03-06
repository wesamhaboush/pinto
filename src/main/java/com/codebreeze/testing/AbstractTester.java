package com.codebreeze.testing;


import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.codebreeze.testing.PintoArrays.toPrimitive;
import static com.codebreeze.testing.Randoms.*;
import static org.mockito.Mockito.mock;

class AbstractTester {
    private final Map<ClassType, Function<Class<?>, Supplier<?>>> getFactoryForClass = new HashMap<ClassType, Function<Class<?>, Supplier<?>>>(){
        {
            put(ClassType.STANDARD, SUPPLIERS_FOR_STANDARD_TYPES::get);
            put(ClassType.ENUM, AbstractTester::factoryForEnum);
            put(ClassType.NON_FINAL, AbstractTester::factoryFor);
            put(ClassType.OTHER, type -> {
                Supplier<?> result = suppliersForNonStandardTypes.get(type);
                Objects.requireNonNull(result, "otherFactories does not contain a factory for type " + type);
                return result;
            });
        }
    };
    private static final Supplier<Integer> INTEGER_FACTORY = new Supplier<Integer>() {
        private int previous = 0;
        @Override
        public Integer get()
        {
            return previous = Stream.generate(() -> Randoms.nextInt(0, Integer.MAX_VALUE))
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous + 1);
        }
    };
    private static final Supplier<Integer[]> INTEGER_ARRAY_FACTORY = () -> randomArray(Integer[].class, INTEGER_FACTORY, randomInt(1, 100));
    private static final Supplier<int[]> INT_ARRAY_FACTORY = () -> toPrimitive(INTEGER_ARRAY_FACTORY.get());
    private static final Supplier<Short> SHORT_FACTORY = new Supplier<Short>() {
        private short previous = 0;
        @Override
        public Short get() {
            return previous = Stream.generate(Randoms::randomShort)
                                       .filter(doesNotEqual(previous))
                                       .limit(1)
                                       .findFirst()
                                       .orElse(previous++);
        }
    };
    private static final Supplier<Short[]> SHORT_ARRAY_FACTORY = () -> randomArray(Short[].class, SHORT_FACTORY, randomInt(1, 100));
    private static final Supplier<short[]> PRIMITIVE_SHORT_ARRAY_FACTORY = () -> toPrimitive(SHORT_ARRAY_FACTORY.get());
    private static final Supplier<Long> LONG_FACTORY = new Supplier<Long>() {
        private long previous = 0L;
        @Override
        public Long get() {
            return previous = Stream.generate(Randoms::randomLong)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous++);
        }
    };
    private static final Supplier<Long[]> LONG_ARRAY_FACTORY = () -> randomArray(Long[].class, LONG_FACTORY, randomInt(1, 100));
    private static final Supplier<long[]> PRIMITIVE_LONG_ARRAY_FACTORY = () -> toPrimitive(LONG_ARRAY_FACTORY.get());
    private static final Supplier<Double> DOUBLE_FACTORY = new Supplier<Double>() {
        private double previous = 0;
        @Override
        public Double get() {
            return previous = Stream.generate(Randoms::randomDouble)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous++);
        }
    };
    private static final Supplier<Double[]> DOUBLE_ARRAY_FACTORY = () -> randomArray(Double[].class, DOUBLE_FACTORY, randomInt(1, 100));
    private static final Supplier<double[]> PRIMITIVE_DOUBLE_ARRAY_FACTORY = () -> toPrimitive(DOUBLE_ARRAY_FACTORY.get());
    private static final Supplier<Float> FLOAT_FACTORY = new Supplier<Float>() {
        private float previous = 0f;
        @Override
        public Float get() {
            return previous = Stream.generate(Randoms::randomFloat)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous++);
        }
    };
    private static final Supplier<Float[]> FLOAT_ARRAY_FACTORY = () -> randomArray(Float[].class, FLOAT_FACTORY, randomInt(1, 100));
    private static final Supplier<float[]> PRIMITIVE_FLOAT_ARRAY_FACTORY = () -> toPrimitive(FLOAT_ARRAY_FACTORY.get());
    private static final Supplier<Boolean> BOOLEAN_FACTORY = new Supplier<Boolean>() {
        private boolean previous = false;
        @Override
        public Boolean get() {
            return previous = Stream.generate(Randoms::randomBoolean)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(!previous);
        }
    };
    private static final Supplier<Boolean[]> BOOLEAN_ARRAY_FACTORY = () -> randomArray(Boolean[].class, BOOLEAN_FACTORY, randomInt(1, 100));
    private static final Supplier<boolean[]> PRIMITIVE_BOOLEAN_ARRAY_FACTORY = () -> toPrimitive(BOOLEAN_ARRAY_FACTORY.get());
    private static final Supplier<Character> CHARACTER_FACTORY = new Supplier<Character>() {
        private char previous = '\0';
        @Override
        public Character get() {
            return previous = Stream.generate(Randoms::randomChar)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous++);
        }
    };
    private static final Supplier<Character[]> CHARACTER_ARRAY_FACTORY = () -> randomArray(Character[].class, CHARACTER_FACTORY, randomInt(1, 100));
    private static final Supplier<char[]> PRIMITIVE_CHAR_ARRAY_FACTORY = () -> toPrimitive(CHARACTER_ARRAY_FACTORY.get());
    private static final Supplier<Byte> BYTE_FACTORY = new Supplier<Byte>() {

        private byte previous = 0;

        @Override
        public Byte get() {
            return previous = Stream.generate(Randoms::randomByte)
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous++);
        }
    };
    private static final Supplier<Byte[]> BYTE_ARRAY_FACTORY = () -> randomArray(Byte[].class, BYTE_FACTORY, randomInt(1, 100));
    private static final Supplier<byte[]> PRIMITIVE_BYTE_ARRAY_FACTORY = () -> toPrimitive(BYTE_ARRAY_FACTORY.get());
    private static final Supplier<String> STRING_FACTORY = new Supplier<String>() {

        private String previous = "";

        @Override
        public String get() {
            return previous = Stream.generate(() -> randomAlphanumeric(randomInt(1, 100)))
                                    .filter(doesNotEqual(previous))
                                    .limit(1)
                                    .findFirst()
                                    .orElse(previous + randomAlphanumeric(randomInt(1, 100)));
        }
    };
    private static final Supplier<String[]> STRING_ARRAY_FACTORY = () -> randomArray(String[].class, STRING_FACTORY, randomInt(1, 100));
    private final Map<Class<?>, Supplier<?>> suppliersForNonStandardTypes = new HashMap<>();


    enum ClassType {
        ENUM, STANDARD, NON_FINAL, OTHER
    }

    private final static Map<Class<?>, Supplier<?>> SUPPLIERS_FOR_STANDARD_TYPES = createStandardFactories();

    Supplier<?> getFactoryForClass(final Class<?> type) {
        return getFactoryForClass.get(getClassType(type)).apply(type);
    }

    private ClassType getClassType(final Class<?> type) {
        if (SUPPLIERS_FOR_STANDARD_TYPES.containsKey(type)) {
            return ClassType.STANDARD;
        }
        if (type.isEnum()) {
            if(type.getEnumConstants().length == 0)
            {
                throw new IllegalArgumentException("enumConstants count is greater than 0 must be true. "
                                                   + "cannot use this field for equality or hashcoding, since "
                                                   + "the enum has no values, only null can be assigned to it. "
                                                   + "Exclude it please");
            }
            return ClassType.ENUM;
        }
        if (!Modifier.isFinal(type.getModifiers())) {
            return ClassType.NON_FINAL;
        }
        return ClassType.OTHER;
    }

    private static Map<Class<?>, Supplier<?>> createStandardFactories() {
        final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

        //Integer/int
        factories.put(int.class, INTEGER_FACTORY);
        factories.put(Integer.class, factories.get(int.class));
        factories.put(int[].class, INT_ARRAY_FACTORY);
        factories.put(Integer[].class, INTEGER_ARRAY_FACTORY);

        factories.put(short.class, SHORT_FACTORY);
        factories.put(Short.class, factories.get(short.class));
        factories.put(short[].class, PRIMITIVE_SHORT_ARRAY_FACTORY);
        factories.put(Short[].class, SHORT_ARRAY_FACTORY);

        factories.put(long.class, LONG_FACTORY);
        factories.put(Long.class, factories.get(long.class));
        factories.put(long[].class, PRIMITIVE_LONG_ARRAY_FACTORY);
        factories.put(Long[].class, LONG_ARRAY_FACTORY);

        factories.put(double.class, DOUBLE_FACTORY);
        factories.put(Double.class, factories.get(double.class));
        factories.put(double[].class, PRIMITIVE_DOUBLE_ARRAY_FACTORY);
        factories.put(Double[].class, DOUBLE_ARRAY_FACTORY);

        factories.put(float.class, FLOAT_FACTORY);
        factories.put(Float.class, factories.get(float.class));
        factories.put(float[].class, PRIMITIVE_FLOAT_ARRAY_FACTORY);
        factories.put(Float[].class, FLOAT_ARRAY_FACTORY);

        factories.put(boolean.class, BOOLEAN_FACTORY);
        factories.put(Boolean.class, factories.get(boolean.class));
        factories.put(boolean[].class, PRIMITIVE_BOOLEAN_ARRAY_FACTORY);
        factories.put(Boolean[].class, BOOLEAN_ARRAY_FACTORY);

        factories.put(char.class, CHARACTER_FACTORY);
        factories.put(Character.class, factories.get(char.class));
        factories.put(char[].class, PRIMITIVE_CHAR_ARRAY_FACTORY);
        factories.put(Character[].class, CHARACTER_ARRAY_FACTORY);

        factories.put(byte.class, BYTE_FACTORY);
        factories.put(Byte.class, factories.get(byte.class));
        factories.put(byte[].class, PRIMITIVE_BYTE_ARRAY_FACTORY);
        factories.put(Byte[].class, BYTE_ARRAY_FACTORY);

        factories.put(String.class, STRING_FACTORY);
        factories.put(String[].class, STRING_ARRAY_FACTORY);

        return factories;
    }

    private static Supplier<?> factoryFor(final Class<?> sClass) {
        return () -> mock(sClass);
    }

    private static Supplier<?> factoryForEnum(final Class<?> sClass) {
        return new Supplier<Object>() {

            private final Object[] ss = sClass.getEnumConstants();
            private final CyclicIndex index = new CyclicIndex(ss.length);

            @Override
            public Object get() {
                return ss[index.next()];
            }
        };
    }

    void addSuppliers(Map<Class<?>, Supplier<?>> extraFactories) {
        this.suppliersForNonStandardTypes.putAll(extraFactories);
    }

    private static <T> Predicate<T> doesNotEqual(final T number)
    {
        return i -> PintoObjects.isNotEqual(i, number);
    }

    private static Predicate<Boolean> doesNotEqual(final boolean flag)
    {
        return i -> PintoObjects.isNotEqual(i, flag);
    }

    private static Predicate<Character> doesNotEqual(final char c)
    {
        return i -> PintoObjects.isNotEqual(i, c);
    }
}
