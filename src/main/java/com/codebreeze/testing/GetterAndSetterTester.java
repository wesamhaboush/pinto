package com.codebreeze.testing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class GetterAndSetterTester<T> extends AbstractTester{

  private final Supplier<T> factory;
  private final List<String> ignoredFields = new LinkedList<String>();


  public GetterAndSetterTester(final Supplier<T> factory) {
    this(factory, Collections.<String>emptyList(), Collections.<Class<?>, Supplier<?>>emptyMap());
  }

  public GetterAndSetterTester(final Supplier<T> factory, final List<String> ignoredFields) {
    this(factory, ignoredFields, Collections.<Class<?>, Supplier<?>>emptyMap());
  }

  public GetterAndSetterTester(final Supplier<T> factory, final List<String> ignoredFields, final Map<Class<?>, Supplier<?>> nonStandardFactories) {
    this.factory = factory;
    this.ignoredFields.addAll(ignoredFields);
    addFactories(nonStandardFactories);
  }

  public void run() throws InvocationTargetException, IllegalAccessException {
    final T instance = factory.get();
    final List<Field> fields = Arrays.asList(instance.getClass()
            .getDeclaredFields());

    for (Field field : fields) {
      if (!ignoredFields.contains(field.getName())) {
        if (hasGetterAndSetter(field)) {
          testGetterAndSetter(field, instance);
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

  private Method getSetter(final Field field) {
    final Class<?> declaringClass = field.getDeclaringClass();
    try {
      return declaringClass.getMethod("set" + nameWithCapital(field),
              field.getType());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(String.format("cannot find setter for field [%s]", field.getName()), e);
    }
  }

  private Method getGetter(final Field field) {
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

  private String nameWithCapital(final Field field) {
    final String result = field.getName();
    return result.replaceFirst("" + result.charAt(0),
            "" + Character.toUpperCase(result.charAt(0)));
  }

  private boolean hasGetter(final Field field) {
    try {
      return getGetter(field) != null;
    } catch (RuntimeException rte) {
      if (rte.getCause().getClass() == NoSuchMethodException.class) {
        return false;
      } else {
        throw rte;
      }
    }
  }

  private boolean hasSetter(final Field field) {
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

  private boolean hasGetterAndSetter(final Field field) {
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
                      clazz, getFactoriesForStandardTypes(), getFactoriesForNonStandardTypes()));
    }
  }
}
