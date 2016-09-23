package com.codebreeze.testing;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetterAndSetterTest<T> extends AbstractTester {

    private final Class<T> clazz;
    private final Supplier<T> cutSupplier;
    private final Set<String> excludeFields = new HashSet<String>()
    {
        {
            add("$jacocoData");
        }
    };
    private final Set<String> includeFields = new HashSet<>();
    private final boolean strict;


    private GetterAndSetterTest(final Class<T> clazz,
                                final Supplier<T> cutSupplier,
                                final Set<String> includeFields,
                                final Set<String> excludeFields,
                                final Map<Class<?>, Supplier<?>> nonStandardSuppliers,
                                final boolean strict) {
        this.clazz = clazz;
        this.cutSupplier = cutSupplier;
        this.includeFields.addAll(includeFields);
        this.excludeFields.addAll(excludeFields);
        addSuppliers(nonStandardSuppliers);
        this.strict = strict;
    }

    public void run() throws InvocationTargetException, IllegalAccessException {
        final List<Field> fields = getFields();
        testTestsForTheseFields(fields);
    }

    private List<Field> getFields() {
        final Field[] nonStaticFields = Stream.of(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(size -> new Field[size]);
        if (!includeFields.isEmpty()) {
            return Stream.of(nonStaticFields)
                    .filter(field -> includeFields.contains(field.getName()))
                    .collect(toList());
        }
        if (!excludeFields.isEmpty()) {
            return Stream.of(nonStaticFields)
                    .filter(field -> !excludeFields.contains(field.getName()))
                    .collect(toList());
        }
        return Arrays.asList(clazz.getDeclaredFields());
    }

    private void testTestsForTheseFields(final List<Field> fields) throws InvocationTargetException, IllegalAccessException {
        final T instance = cutSupplier.get();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!excludeFields.contains(field.getName())) {
                if (hasGetterAndSetter(field)) {
                    testGetterAndSetter(field, instance);
                } else if(strict){
                    final String message = String.format("all included fields must have both setters and getters and field [%s] did not",
                            field.getName());
                    fail(message);
                } else {
                    if (hasGetter(field)) {
                        testGetter(field, instance);
                    }
                    if (hasSetter(field)) {
                        testSetter(field, instance);
                    }
                }
            }
        }
    }

    public void verify() {
        try {
            run();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw PintoThrowables.propagate(e);
        }
    }

    private static Method getSetter(final Field field) {
        final Class<?> declaringClass = field.getDeclaringClass();
        try {
            return declaringClass.getMethod("set" + nameWithCapital(field),
                    field.getType());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("cannot find setter for field [%s]", field.getName()), e);
        }
    }

    private static Method getGetter(final Field field) {
        final Class<?> declaringClass = field.getDeclaringClass();
        try {
            return declaringClass.getMethod("get" + nameWithCapital(field));
        } catch (NoSuchMethodException e) {
            try {
                return declaringClass.getMethod("is" + nameWithCapital(field));
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException(String.format("cannot find getter for field [%s]", field.getName()), e);
            }
        }
    }

    private static String nameWithCapital(final Field field) {
        final String result = field.getName();
        return result.replaceFirst("" + result.charAt(0),
                "" + Character.toUpperCase(result.charAt(0)));
    }

    private static boolean hasGetter(final Field field) {
        try {
            return getGetter(field) != null;
        } catch (RuntimeException rte) {
            if (rte.getCause() != null &&
                    rte.getCause().getClass() == NoSuchMethodException.class) {
                return false;
            } else {
                throw rte;
            }
        }
    }

    private static boolean hasSetter(final Field field) {
        try {
            return getSetter(field) != null;
        } catch (RuntimeException rte) {
            if (rte.getCause().getClass() == NoSuchMethodException.class) {
                return false;
            } else {
                throw rte;
            }
        }
    }

    private static boolean hasGetterAndSetter(final Field field) {
        return hasGetter(field) && hasSetter(field);
    }

    private void testGetterAndSetter(final Field field, final Object instance) throws InvocationTargetException, IllegalAccessException {
        final Object value = getValueForField(field.getType());
        final Method getter = getGetter(field);
        final Method setter = getSetter(field);

        setter.invoke(instance, value);
        assertEquals(String.format("Failed getter and setter test of field [%s] on class [%s]",
                        field.getName(), field.getDeclaringClass().getName()),
                value, getter.invoke(instance));
    }

    private void testSetter(final Field field, final Object instance) throws IllegalAccessException, InvocationTargetException {
        final Object value = getValueForField(field.getType());
        final Method setter = getSetter(field);

        setter.invoke(instance, value);
        field.setAccessible(true);
        assertEquals(String.format("Failed setter test of field [%s]  on class [%s]",
                        field.getDeclaringClass().getName(), field.getName()),
                value, field.get(instance));
    }

    private void testGetter(final Field field, final Object instance) throws IllegalAccessException, InvocationTargetException {
        final Object value = getValueForField(field.getType());
        final Method getter = getGetter(field);
        field.setAccessible(true);
        field.set(instance, value);
        assertEquals(String.format("Failed getter test of field [%s] on classs [%s]",
                        field.getName(), field.getDeclaringClass().getName()),
                value, getter.invoke(instance));
    }

    private Object getValueForField(final Class<?> clazz) {

        final Object fieldValue = getFactoryForClass(clazz).get();

        if (fieldValue != null) {
            return fieldValue;
        } else {
            throw new RuntimeException(
                    String.format("cannot find instance of [%s] in " +
                                    "available standard factory map [%s] or non-standard factory map [%s]",
                            clazz, getFactoriesForStandardTypes(), getSuppliersForNonStandardTypes()));
        }
    }


    public static <T> Builder<T> forClass(final Class<T> clazz){
        return new Builder<T>().forClass(clazz, () -> {
            try {
                final Constructor<?> constructor = Stream.of(clazz.getDeclaredConstructors())
                        .filter(c -> c.getParameterCount() == 0)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("cannot instantiate class without a no-args constructor, you can provide your own supplier if you want"));
                    constructor.setAccessible(true);
                    return (T)constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw PintoThrowables.propagate(e);
            }
        });
    }

    public static <T> Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier){
        return new Builder<T>().forClass(clazz, cutSupplier);
    }

    public static class Builder<T> {
        private Set<String> includeFieldNames = new HashSet<>();
        private Set<String> excludeFieldNames = new HashSet<>();
        private Class<T> clazz;
        private Map<Class<?>, Supplier<?>> nonStandardTypeSuppliers = new HashMap<>();
        //a cutSupplier that returns Equal but NOT the same values
        private Supplier<T> cutSupplier;
        private boolean strict = false;

        private Builder() {
        }

        public Builder<T> forClass(final Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier) {
            this.clazz = clazz;
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> withSupplier(final Supplier<T> cutSupplier) {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> includeFields(final String... fieldNames) {
            includeFieldNames.addAll(Arrays.asList(fieldNames));
            return this;
        }

        public Builder<T> excludeFields(final String... fieldNames) {
            excludeFieldNames.addAll(Arrays.asList(fieldNames));
            return this;
        }

        public <B> Builder<T> withComplexTypeSupplier(final Class<B> bClass, final Supplier<B> complexTypeSupplier) {
            nonStandardTypeSuppliers.put(bClass, complexTypeSupplier);
            return this;
        }

        public Builder<T> withComplexTypeSupplier(final boolean strict){
            this.strict = strict;
            return this;
        }

        public void verify() {
            validateIncludeExcludeListsAreUsedExclusively();
            new GetterAndSetterTest<T>(clazz, cutSupplier, includeFieldNames, excludeFieldNames, nonStandardTypeSuppliers, strict).verify();
        }

        private void validateIncludeExcludeListsAreUsedExclusively() {
            if (!includeFieldNames.isEmpty() && !excludeFieldNames.isEmpty()) {
                final String message1 = "cannot have both include and exclude fields non-empty.";
                final String message2 = " Use either white or black listing (one of include or exclude list can be used, but not both).";
                final String message3 = String.format(", include list[%S], exclude list[%s].",
                        includeFieldNames, excludeFieldNames);
                throw new RuntimeException(message1 + message2 + message3);
            }
        }

    }
}
