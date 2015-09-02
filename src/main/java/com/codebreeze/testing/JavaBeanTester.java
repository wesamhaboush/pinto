package com.codebreeze.testing;

import org.apache.commons.lang3.ArrayUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * source:
 * http://macbeantechnology.co.uk/blog/automate-testing-javabeans/
 */
public class JavaBeanTester {

    public static <T> void test(final Class<T> clazz,
                                final boolean testObjectOverrides,
                                final String... skipThese) throws IntrospectionException {
        final PropertyDescriptor[] props =
                Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        nextProp:
        for (PropertyDescriptor prop : props) {
            //Check the list of properties that we don't want to test
            if (!ArrayUtils.contains(skipThese, prop.getName())) {
                final Method getter = prop.getReadMethod();
                final Method setter = prop.getWriteMethod();
                if (getter != null && setter != null) {
                    final Class<?> returnType = getter.getReturnType();
                    final Class<?>[] params = setter.getParameterTypes();
                    if (params.length == 1 && params[0] == returnType) {
                        try {
                            final Object value = buildValue(returnType);
                            final T bean = clazz.newInstance();
                            if (testObjectOverrides) {
                                testObjectOverrides(bean);
                            }
                            setter.invoke(bean, value);
                            final Object expectedValue = value;
                            final Object actualValue = getter.invoke(bean);
                            assertEquals(String.format("Failed while testing property %s",
                                    prop.getName()), expectedValue, actualValue);
                        } catch (Exception ex) {
                            fail(String.format("Error testing the property %s: %s",
                                    prop.getName(), ex.toString()));
                        }
                    }
                }
            }
        }
    }

    public static <T> void test(final Class<T> clazz) throws IntrospectionException {
        test(clazz, false, new String[0]);
    }

    public static <T> void test(final Class<T> clazz, final String... skipThese)
            throws IntrospectionException {
        test(clazz, false, skipThese);
    }

    @SuppressWarnings("unchecked")
    private static <T> void testObjectOverrides(final T bean) throws IllegalAccessException,
                                                                     InstantiationException {
        final T otherBean = (T) bean.getClass().newInstance();
        assertEquals("Failed equals()", bean, bean);
        assertEquals("Failed equals()", otherBean, bean);
        assertEquals("Failed hashCode()", bean.hashCode(), bean.hashCode());
        assertEquals("Failed hashCode()", otherBean.hashCode(), otherBean.hashCode());
        assertEquals("Failed full hashCode()", bean.hashCode(), otherBean.hashCode());
        assertEquals("Failed toString()", otherBean.toString(), bean.toString());
    }

    private static Object buildMockValue(Class<?> clazz) {
        if (!Modifier.isFinal(clazz.getModifiers())) {
            //Call your mocking framework here
            return mock(clazz);
        } else {
            return null;
        }
    }

    private static Object buildValue(Class<?> clazz)
            throws InstantiationException, IllegalAccessException,
                   IllegalArgumentException, SecurityException,
                   InvocationTargetException {
        //Try mocking framework first
        final Object mockedObject = buildMockValue(clazz);
        if (mockedObject != null) {
            return mockedObject;
        }
        final Constructor<?>[] ctrs = clazz.getConstructors();
        for (Constructor<?> ctr : ctrs) {
            if (ctr.getParameterTypes().length == 0) {
                return ctr.newInstance();
            }
        }
        if (clazz == String.class) return "testvalue";
        else if (clazz.isArray()) return Array.newInstance(clazz.getComponentType(), 1);
        else if (clazz == boolean.class || clazz == Boolean.class) return true;
        else if (clazz == int.class || clazz == Integer.class) return 1;
        else if (clazz == long.class || clazz == Long.class) return 1L;
        else if (clazz == double.class || clazz == Double.class) return 1.0D;
        else if (clazz == float.class || clazz == Float.class) return 1.0F;
        else if (clazz == char.class || clazz == Character.class) return 'Y';
        else if (clazz.isEnum()) return clazz.getEnumConstants()[0];
        else {
            fail("Unable to build an instance of class " + clazz.getName());
            return null; //for the compiler
        }
    }
}


