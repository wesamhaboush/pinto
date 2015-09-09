package com.codebreeze.testing;

import com.google.common.base.Throwables;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class RandomizedInstance<T> extends AbstractTester{
    private final Set<String> excludeFieldNames = new HashSet<>();
    private final Set<String> includeFieldNames = new HashSet<>();
    private final Class<T> clazz;
    //a cutSupplier that returns Equal but NOT the same values
    private final Supplier<T> cutSupplier;

    /**
     * cutSupplier needs to return equal but not-same objects of the same class
     *
     * @param cutSupplier of equal but not same instance of the CUT
     * @param cutClass CUT class
     * @param includeFieldNames whitelist of fields to consider
     * @param nonStandardTypeSuppliers these need to be able to produce different instance and unequal instances of the target type
     */
    private RandomizedInstance(
            final Supplier<T> cutSupplier,
            final Class<T> cutClass,
            final Set<String> includeFieldNames,
            final Set<String> excludeFieldNames,
            final Map<Class<?>, Supplier<?>> nonStandardTypeSuppliers) {

        Validate.notNull(cutClass, "cutClass");
        Validate.notNull(cutSupplier, "cutSupplier");
        Validate.notNull(includeFieldNames, "includeFieldNames");
        Validate.notNull(excludeFieldNames, "excludeFieldNames");
        Validate.notNull(nonStandardTypeSuppliers, "nonStandardTypeFactories");
        Validate.isTrue(cutSupplier.get() != cutSupplier.get(), "a cutSupplier.create() call must not be same instance as any other cutSupplier.create() call");

        this.cutSupplier = cutSupplier;

        this.clazz = cutClass;
        if (!CollectionUtils.isEmpty(includeFieldNames)) {
            this.includeFieldNames.addAll(includeFieldNames);
        }
        if (!CollectionUtils.isEmpty(excludeFieldNames)) {
            this.excludeFieldNames.addAll(excludeFieldNames);
        }
        addSuppliers(nonStandardTypeSuppliers);
    }

    private T getInstance() throws IllegalAccessException {
        final T instance = cutSupplier.get();
        final List<Field> targetFields = getTargetFields();
        for (final Field targetField : targetFields) {
            targetField.setAccessible(true);
            setValueForFieldOnInstance(instance, targetField);
        }
        return instance;
    }

    private void setValueForFieldOnInstance(final T instance, final Field field) throws IllegalAccessException {
        if (field.getType().isEnum()) {
            final Object[] enumConstants = field.getType().getEnumConstants();
            field.set(instance, enumConstants[nextInt(0, enumConstants.length)]);
        } else {
            final Supplier<?> factoryForField = getFactoryForClass(field.getType());
            field.set(instance, factoryForField.get());
        }
    }

    private List<Field> getTargetFields() throws SecurityException {
        final List<Field> targetFields = new ArrayList<>();
        final Field[] fields = Stream.of(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        if (!includeFieldNames.isEmpty()) {
            for (final Field field : fields) {
                if (includeFieldNames.contains(field.getName())) {
                    targetFields.add(field);
                }
            }
        } else if(!excludeFieldNames.isEmpty()){
            for (final Field field : fields) {
                if (!excludeFieldNames.contains(field.getName())) {
                    targetFields.add(field);
                }
            }
        } else {
            targetFields.addAll(Arrays.asList(fields));
        }
        return targetFields;
    }

    public T create() {
        try {
            return getInstance();
        } catch (final IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public T get() {
        return create();
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
                throw Throwables.propagate(e);
            }
        });
    }

    public static <T> Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier){
        return new Builder<T>().forClass(clazz, cutSupplier);
    }

    public static class Builder<T>{
        private Set<String> includeFieldNames = new HashSet<>();
        private Set<String> excludeFieldNames = new HashSet<>();
        private Class<T> clazz;
        private Map<Class<?>, Supplier<?>> nonStandardTypeSuppliers = new HashMap<>();
        //a cutSupplier that returns Equal but NOT the same values
        private Supplier<T> cutSupplier;

        private Builder() {
        }


        public Builder<T> forClass(final Class<T> clazz){
            this.clazz = clazz;
            return this;
        }

        public Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier){
            this.clazz = clazz;
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> withSupplier(final Supplier<T> cutSupplier){
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> includeFields(final String ...fieldNames){
            includeFieldNames.addAll(Arrays.asList(fieldNames));
            return this;
        }

        public Builder<T> excludeFields(final String ...fieldNames){
            excludeFieldNames.addAll(Arrays.asList(fieldNames));
            return this;
        }

        public <B> Builder<T> withComplexTypeSupplier(final Class<B> bClass, final Supplier<B> complexTypeSupplier){
            nonStandardTypeSuppliers.put(bClass, complexTypeSupplier);
            return this;
        }

        public T get(){
            validateIncludeExcludeListsAreUsedExclusively();
            return new RandomizedInstance<T>(cutSupplier, clazz, includeFieldNames, excludeFieldNames, nonStandardTypeSuppliers).get();
        }

        private void validateIncludeExcludeListsAreUsedExclusively() {
            if(!includeFieldNames.isEmpty() && !excludeFieldNames.isEmpty()){
                final String message1 = "cannot have both include and exclude fields non-empty.";
                final String message2 = " Use either white or black listing (one of include or exclude list can be used, but not both).";
                final String message3 = String.format(", include list[%S], exclude list[%s].",
                        includeFieldNames, excludeFieldNames);
                throw new RuntimeException(message1 + message2 + message3);
            }
        }

    }
}
