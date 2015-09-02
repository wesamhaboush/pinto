package com.codebreeze.testing;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EqualsHashcodeTester<T> extends AbstractTester {

    public final static Function<Class<?>, Set<String>> CLASS_TO_FIELD_NAME_SET_MAPPER = new Function<Class<?>, Set<String>>() {
        @Override
        public Set<String> apply(final Class<?> aClass) {
            final Set<String> fieldNames = new HashSet<String>();
            for (final Field field : aClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fieldNames.add(field.getName());
                }
            }
            return fieldNames;
        }
    };
    private final static Predicate<Field> NON_STATIC_FIELDS_FILTER = new Predicate<Field>() {
        @Override
        public boolean apply(final Field field) {
            final int modifiers = field.getModifiers();
            return !Modifier.isStatic(modifiers);
        }
    };

    private final Set<String> targetFieldNames = new HashSet<String>();


    private final Class<T> clazz;
    //a factory that returns Equal but NOT the same values
    private final Supplier<T> factory;

    /**
     * factory needs to return equal but not-same objects of the same class
     *
     * @param factory
     * @param clazz
     * @param targetFieldNames
     * @param nonStandardTypesFactories
     */
    public EqualsHashcodeTester(
            final Supplier<T> factory,
            final Class<T> clazz,
            final Set<String> targetFieldNames,
            final Map<Class<?>, Supplier<?>> nonStandardTypesFactories) {

        Validate.notNull(clazz, "clazz");
        Validate.notNull(factory, "factory");
        Validate.notNull(targetFieldNames, "targetFieldNames");
        Validate.notNull(nonStandardTypesFactories, "nonStandardTypeFactories");
        Validate.isTrue(factory.get() != factory.get(), "a factory.create() call must not be same as any other factory.create() call");
        Validate.isTrue(Objects.equals(factory.get(), factory.get()), "a factory.create() call", "another factory.create() call", "please provide implementation of factory that produces equal objects but never the same");

        this.factory = factory;

        this.clazz = clazz;
        if (!CollectionUtils.isEmpty(targetFieldNames)) {
            this.targetFieldNames.addAll(targetFieldNames);
        }
        addFactories(nonStandardTypesFactories);
    }

    public EqualsHashcodeTester(final Supplier<T> factory, final Class<T> clazz, final Map<Class<?>, Supplier<?>> nonStandardTypFactories) {
        this(factory, clazz, Collections.<String>emptySet(), nonStandardTypFactories);
    }

    public EqualsHashcodeTester(final Supplier<T> factory, final Class<T> clazz, final Set<String> targetFieldNames) {
        this(factory, clazz, targetFieldNames, Collections.<Class<?>, Supplier<?>>emptyMap());
    }

    public EqualsHashcodeTester(final Supplier<T> factory, final Class<T> clazz) {
        this(factory, clazz, Collections.<String>emptySet(), Collections.<Class<?>, Supplier<?>>emptyMap());
    }

    private void testSubclassesAreUnequal() {
        final T t1 = factory.get();
        final T t3 = mock(clazz);
        assertTrue("second object is not on the hierarchy", t1.getClass().isAssignableFrom(t3.getClass()));
        assertTrue("classes are the same!", t1.getClass() != t3.getClass());
        assertFalse("subclasses are not equal", t1.equals(t3));
        assertFalse("subclasses should be transitively unequal", t3.equals(t1));
    }

    private void testNullIsNotEqual() {
        final T t1 = factory.get();
        final T t3 = null;
        assertFalse("nulls need to return false", t1.equals(t3));
    }

    private void testSameIsEqual() {
        final T t1 = factory.get();
        final T t3 = t1;
        assertTrue("same object reference need to return true", t1.equals(t3));
    }

    private void testTargetFieldsInfluenceEquality() throws IllegalAccessException {
        T t1;
        T t2;

        final List<Field> targetFields = getTargetFields();
        for (int i = 0; i < targetFields.size(); i++) {
            //Reset instances
            t1 = factory.get();
            t2 = factory.get();

            final Field field = targetFields.get(i);
            field.setAccessible(true);

            setDifferentValuesForFieldOnObjects(t1, t2, field);

            //cannot be equal if fields are different
            assertFalse("fields have different values, should not be equal [" + t1.toString() + "][" + t2 + "]", t1.equals(t2));

            // transitive
            assertFalse("unequal instances need to be transitively unequal", t2.equals(t1));

            //equalize the fields
            field.set(t2, field.get(t1));

            //make sure they are equal
            assertTrue("they should be equal since they were set to be equal", field.get(t1).equals(field.get(t2)));

            // they need to be equal
            assertTrue(String.format("setting the fields equal should make the objects equal field[%s], obj1[%s], obj2[%s]", field.getName(), t1, t2), t1.equals(t2));

            //transitive
            assertTrue("transitivity needs to be maintained", t2.equals(t1));

            //hashCode needs to be equal
            assertEquals("since they are equal, hashCodes must be equal", t1.hashCode(), t2.hashCode());
        }
    }

    private void setDifferentValuesForFieldOnObjects(final T t1, final T t2, final Field field) throws IllegalAccessException {
        if (field.getType().isEnum()) {
            final int[] indices = twoDifferentIntegersBelow(field.getType().getEnumConstants().length);
            field.set(t1, field.getType().getEnumConstants()[indices[0]]);
            field.set(t2, field.getType().getEnumConstants()[indices[1]]);
        } else {
            final Supplier<?> factoryForField = getFactoryForClass(field.getType());
            field.set(t1, factoryForField.get());
            field.set(t2, factoryForField.get());
        }
    }

    private List<Field> getTargetFields() throws SecurityException {
        final List<Field> targetFields = new ArrayList<Field>();
        final Field[] fields = clazz.getDeclaredFields();
        if (!targetFieldNames.isEmpty()) {
            for (final Field field : fields) {
                if (targetFieldNames.contains(field.getName())) {
                    targetFields.add(field);
                }
            }
        } else {
            targetFields.addAll(Collections2.filter(Arrays.asList(fields), NON_STATIC_FIELDS_FILTER));
        }
        return targetFields;
    }

    private void testTargetFieldsInfluenceHashCode() throws IllegalAccessException {
        final Field[] fields = getTargetFields().toArray(new Field[]{});
        for (int i = 0; i < fields.length; i++) {
            final T t1 = factory.get();
            final Field field = fields[i];
            field.setAccessible(true);
            final Supplier<?> factoryForField = getFactoryForClass(field.getType());
            field.set(t1, factoryForField.get());
            final Object firstValue = field.get(t1);
            final int hashcodeBefore = t1.hashCode();
            field.setAccessible(true);
            field.set(t1, factoryForField.get());
            final Object secondValue = field.get(t1);
            final int hashCodeAfter = t1.hashCode();
            assertNotSame(String.format("different member values should result in diff hascodes for field [%s][%s][%s]", field, firstValue, secondValue), hashcodeBefore, hashCodeAfter);
        }
    }

    public void runAllTests() {
        try {
            testSubclassesAreUnequal();
            testNullIsNotEqual();
            testSameIsEqual();
            testTargetFieldsInfluenceEquality();
            testTargetFieldsInfluenceHashCode();
        } catch (final IllegalAccessException ex) {
            throw new EqualAndHashCodeTestException(ex);
        }
    }


    private static int[] twoDifferentIntegersBelow(final int max) {
        Validate.isTrue(max > 1, "max > One is not true, cannot provide two different positive integers with maximum value of 1 or less");

        final int[] result = new int[2];
        result[0] = randomInt(0, max);

        do {
            result[1] = randomInt(0, max);
        } while (result[0] == result[1]);

        return result;
    }


    @SuppressWarnings("serial")
    private static class EqualAndHashCodeTestException extends RuntimeException {

        public EqualAndHashCodeTestException(final Throwable throwable) {
            super(throwable);
        }
    }
}
