package eu.realalpha.infinityfilter;


import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

    private static final Map<Class<?>, Map<String, Field>> CACHED_FIELDS_BY_NAME = new HashMap<>();
    private static final Map<Class<?>, Map<Class<?>, Field>> CACHED_FIELDS_BY_CLASS = new HashMap<>();
    private static Unsafe UNSAFE;

    public static void setFinalField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setFinalField(object, ReflectionUtils.getPrivateField(object.getClass(), fieldName), value);
    }

    public static void setFinalField(Object object, Field field, Object value) throws IllegalAccessException {
        UNSAFE.putObject(object, UNSAFE.objectFieldOffset(field), value);
    }

    public static void setField(Object object, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        ReflectionUtils.setField(object, ReflectionUtils.getPrivateField(object.getClass(), fieldName), value);
    }

    public static void setField(Object object, Field field, Object value) throws IllegalAccessException {
        field.set(object, value);
    }

    public static Object getObjectInPrivateField(Object object, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = ReflectionUtils.getPrivateField(object.getClass(), fieldName);
        return field.get(object);
    }

    public static Field getPrivateField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = ReflectionUtils.getDeclaredField(clazz, fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Field searchFieldByClass(Class<?> clazz, Class<?> searchFor) {
        Field cachedField = CACHED_FIELDS_BY_CLASS.computeIfAbsent(clazz, aClass -> new HashMap<>()).get(searchFor);
        if (cachedField != null) {
            return cachedField;
        }
        Class<?> currentClass = clazz;
        do {
            Field[] arrayOfField = currentClass.getDeclaredFields();
            int i = arrayOfField.length;
            for (int b = 0; b < i; b = (byte)(b + 1)) {
                Field field = arrayOfField[b];
                if (!searchFor.isAssignableFrom(field.getType())) continue;
                CACHED_FIELDS_BY_CLASS.computeIfAbsent(clazz, aClass -> new HashMap<>()).put(searchFor, field);
                return field;
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        throw new IllegalArgumentException("no " + searchFor.getName() + " field for class " + clazz.getName() + " found");
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field;
        Field cachedField = CACHED_FIELDS_BY_NAME.computeIfAbsent(clazz, aClass -> new HashMap<>()).get(fieldName);
        if (cachedField != null) {
            return cachedField;
        }
        try {
            field = clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return ReflectionUtils.getDeclaredField(superclass, fieldName);
            }
            throw e;
        }
        CACHED_FIELDS_BY_NAME.computeIfAbsent(clazz, aClass -> new HashMap<>()).put(fieldName, field);
        return field;
    }


    public static Object invokeDeclaredMethod(Object o, String methode, Object... objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = o.getClass();
        Method method = (objects.length != 0 ? aClass.getDeclaredMethod(methode, getParametersType(objects)) : aClass.getDeclaredMethod(methode));
        method.setAccessible(true);
        return method.invoke(o, objects);
    }

    public static Object invokeDeclaredField(Object o, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(o);
    }

    public static void setDeclaredField(Object o, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(o, value);
    }

    private static Class<?>[] getParametersType(Object... parameters){
        Class<?>[] classes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
        }
        return classes;
    }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
