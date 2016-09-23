package com.codebreeze.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.codebreeze.testing.PintoThrowables.propagate;
import static com.codebreeze.testing.Randoms.randomSetFrom;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EqualAndHashcodeTest<T> extends AbstractTester
{
    private final Set<String> excludeFieldNames = new HashSet<String>(){
        {
            add("$jacocoData");
            add("this$0");
        }
    };

    private final Set<String> includeFieldNames = new HashSet<>();
    private final Class<T> clazz;
    //a cutSupplier that returns Equal but NOT the same values
    private final Supplier<T> cutSupplier;

    /**
     * cutSupplier needs to return equal but not-same objects of the same class
     *
     * @param cutSupplier              of equal but not same instance of the CUT
     * @param cutClass                 CUT class
     * @param includeFieldNames        whitelist of fields to consider
     * @param nonStandardTypeSuppliers these need to be able to produce different instance and unequal instances of the target type
     */
    private EqualAndHashcodeTest(final Supplier<T> cutSupplier,
                                 final Class<T> cutClass,
                                 final Set<String> includeFieldNames,
                                 final Set<String> excludeFieldNames,
                                 final Map<Class<?>, Supplier<?>> nonStandardTypeSuppliers)
    {
        Objects.requireNonNull(cutClass, "CUT class cannot be null");
        Objects.requireNonNull(cutSupplier, "CUT supplier cannot be null");
        Objects.requireNonNull(includeFieldNames, "includeFieldNames");
        Objects.requireNonNull(excludeFieldNames, "excludeFieldNames");
        Objects.requireNonNull(nonStandardTypeSuppliers, "nonStandardTypeFactories");
        if(cutSupplier.get() == cutSupplier.get())
        {
            throw new IllegalArgumentException("a cutSupplier.create() call must not be same instance as any other cutSupplier.create() call");
        }
        if(!Objects.equals(cutSupplier.get(), cutSupplier.get()))
        {
           throw new IllegalArgumentException("please provide implementation of cutSupplier that produces equal objects but never the same");
        }

        this.cutSupplier = cutSupplier;

        this.clazz = cutClass;
        if (!PintoCollections.isEmpty(includeFieldNames))
        {
            this.includeFieldNames.addAll(includeFieldNames);
        }
        if (!PintoCollections.isEmpty(excludeFieldNames))
        {
            this.excludeFieldNames.addAll(excludeFieldNames);
        }
        addSuppliers(nonStandardTypeSuppliers);
    }

    private void testSubclassesAreUnequal()
    {
        final T t1 = cutSupplier.get();
        final T t3 = mock(clazz);
        assertTrue("second object is not on the hierarchy", t1
                .getClass()
                .isAssignableFrom(t3.getClass()));
        PintoCheck.State.notSame(t1.getClass(), t3.getClass(), "classes are the same!");
        assertFalse("subclasses are not equal", t1.equals(t3));
        assertFalse("subclasses should be transitively unequal", t3.equals(t1));
    }

    private void testNullIsNotEqual()
    {
        final T t1 = cutSupplier.get();
        final T t3 = null;
        assertFalse("nulls need to return false", t1.equals(t3));
    }

    private void testSameIsEqual()
    {
        final T t1 = cutSupplier.get();
        assertTrue("same object reference need to return true", t1.equals(t1));
    }

    private void testTargetFieldsInfluenceEquality() throws IllegalAccessException
    {
        T t1;
        T t2;

        final List<Field> targetFields = getTargetFields();
        for (Field targetField : targetFields)
        {
            //Reset instances
            t1 = cutSupplier.get();
            t2 = cutSupplier.get();

            targetField.setAccessible(true);

            setDifferentValuesForFieldOnObjects(t1, t2, targetField);

            //cannot be equal if fields are different
            assertFalse("fields have different values, should not be equal [" + t1.toString() + "][" + t2 + "]", t1.equals(t2));

            // transitive
            assertFalse("unequal instances need to be transitively unequal", t2.equals(t1));

            //equalize the fields
            targetField.set(t2, targetField.get(t1));

            //make sure they are equal
            assertTrue("they should be equal since they were set to be equal", targetField
                    .get(t1)
                    .equals(targetField.get(t2)));

            // they need to be equal
            assertTrue(String.format("setting the fields equal should make the objects equal field[%s], obj1[%s], obj2[%s]", targetField.getName(), t1, t2), t1.equals(t2));

            //transitive
            assertTrue("transitivity needs to be maintained", t2.equals(t1));

            //hashCode needs to be equal
            assertEquals("since they are equal, hashCodes must be equal", t1.hashCode(), t2.hashCode());
        }
    }

    private void setDifferentValuesForFieldOnObjects(final T t1, final T t2, final Field field) throws IllegalAccessException
    {
        if (field
                .getType()
                .isEnum())
        {
            final Iterator<?> enumItems = randomSetFrom(2, asList(field.getType().getEnumConstants())).iterator();
            field.set(t1, enumItems.next());
            field.set(t2, enumItems.next());
        }
        else
        {
            final Supplier<?> factoryForField = getFactoryForClass(field.getType());
            field.set(t1, factoryForField.get());
            field.set(t2, factoryForField.get());
        }
    }

    private List<Field> getTargetFields() throws SecurityException
    {
        final List<Field> targetFields = new ArrayList<>();
        final Field[] fields = Stream
                .of(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        if (!includeFieldNames.isEmpty())
        {
            for (final Field field : fields)
            {
                if (includeFieldNames.contains(field.getName()))
                {
                    targetFields.add(field);
                }
            }
        }
        else //if (!excludeFieldNames.isEmpty())
        {
            for (final Field field : fields)
            {
                if (!excludeFieldNames.contains(field.getName()))
                {
                    targetFields.add(field);
                }
            }
        }
        return targetFields;
    }

    private void testTargetFieldsInfluenceHashCode() throws IllegalAccessException
    {
        final List<Field> targetFields = getTargetFields();
        final Field[] fields = targetFields.toArray(new Field[targetFields.size()]);
        for (final Field field1 : fields)
        {
            final T t1 = cutSupplier.get();
            field1.setAccessible(true);
            final Supplier<?> factoryForField = getFactoryForClass(field1.getType());
            field1.set(t1, factoryForField.get());
            final Object firstValue = field1.get(t1);
            final int hashcodeBefore = t1.hashCode();
            field1.setAccessible(true);
            field1.set(t1, factoryForField.get());
            final Object secondValue = field1.get(t1);
            final int hashCodeAfter = t1.hashCode();
            assertNotSame(String.format("different member values should result in diff hascodes for field [%s][%s][%s]", field1, firstValue, secondValue), hashcodeBefore, hashCodeAfter);
        }
    }

    public void runAllTests()
    {
        propagate(() ->
                  {
                      testSubclassesAreUnequal();
                      testNullIsNotEqual();
                      testSameIsEqual();
                      testTargetFieldsInfluenceEquality();
                      testTargetFieldsInfluenceHashCode();
                      return null;
                  });
    }

    public void verify()
    {
        runAllTests();
    }

    public static <T> Builder<T> forClass(final Class<T> clazz)
    {
        return new Builder<T>().forClass(clazz, () -> PintoThrowables.propagate(() ->
                  {
                      final Constructor<?> constructor = Stream.of(clazz.getDeclaredConstructors())
                                                               .filter(c -> c.getParameterCount() == 0)
                                                               .findFirst()
                                                               .orElseThrow(() -> new IllegalArgumentException(
                                                                       "cannot instantiate class without a no-args constructor, you can provide your own supplier if you want"));
                      constructor.setAccessible(true);
                      return (T) constructor.newInstance();
                  }));
    }

    public static <T> Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier)
    {
        return new Builder<T>().forClass(clazz, cutSupplier);
    }

    public static class Builder<T>
    {
        private Set<String> includeFieldNames = new HashSet<>();
        private Set<String> excludeFieldNames = new HashSet<>();
        private Class<T> clazz;
        private Map<Class<?>, Supplier<?>> nonStandardTypeSuppliers = new HashMap<>();
        //a cutSupplier that returns Equal but NOT the same values
        private Supplier<T> cutSupplier;

        private Builder()
        {
        }


        public Builder<T> forClass(final Class<T> clazz, final Supplier<T> cutSupplier)
        {
            this.clazz = clazz;
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> withSupplier(final Supplier<T> cutSupplier)
        {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> includeFields(final String... fieldNames)
        {
            includeFieldNames.addAll(asList(fieldNames));
            return this;
        }

        public Builder<T> excludeFields(final String... fieldNames)
        {
            excludeFieldNames.addAll(asList(fieldNames));
            return this;
        }

        public <B> Builder<T> withComplexTypeSupplier(final Class<B> bClass, final Supplier<B> complexTypeSupplier)
        {
            nonStandardTypeSuppliers.put(bClass, complexTypeSupplier);
            return this;
        }

        public void verify()
        {
            validateIncludeExcludeListsAreUsedExclusively();
            new EqualAndHashcodeTest<>(cutSupplier, clazz, includeFieldNames, excludeFieldNames, nonStandardTypeSuppliers).verify();
        }

        private void validateIncludeExcludeListsAreUsedExclusively()
        {
            if (!includeFieldNames.isEmpty() && !excludeFieldNames.isEmpty())
            {
                final String message1 = "cannot have both include and exclude fields non-empty.";
                final String message2 = " Use either white or black listing (one of include or exclude list can be used, but not both).";
                final String message3 = String.format(", include list[%S], exclude list[%s].", includeFieldNames, excludeFieldNames);
                throw new IllegalStateException(message1 + message2 + message3);
            }
        }

    }
}
