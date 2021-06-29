package nl.jellejurre.seedchecker;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class ReflectionUtils {
    public static Unsafe unsafe = ((Unsafe) getValueFromStaticField(Unsafe.class, "theUnsafe"));

    //Gets a field from the class provided
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException  {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    //Gets a value from a field from an object provided
    public static Object getValueFromField(Object instance, String fieldName) {
        try {
            Field field = getField(instance.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Gets a value from a static field from a class provided
    public static Object getValueFromStaticField(Class clazz, String fieldName) {
        try {
            Field field = getField(clazz, fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Sets a value of a static field of a class provided
    public static void setValueOfStaticField(Class clazz, String fieldName, Object value) {
        try {
            Field field = getField(clazz, fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    //Sets a value of a field of an object provided
    public static void setValueOfField(Object instance, String fieldName, Object value) {
        try {
            Field field = getField(instance.getClass(), fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
