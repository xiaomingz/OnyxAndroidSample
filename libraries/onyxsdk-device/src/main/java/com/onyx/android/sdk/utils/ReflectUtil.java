/**
 * 
 */
package com.onyx.android.sdk.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Joy
 *
 */
public class ReflectUtil
{
    private static final String TAG = "ReflectUtil";
    
    private static Object sDummyObject = new Object();

    public static boolean getConstructorSafely(AtomicReference<Constructor<?>> result, Class<?> cls, Class<?>... parameterTypes)
    {
        try {
            if (cls == null) {
                return false;
            }
            result.set(cls.getConstructor(parameterTypes));
            return true;
        }
        catch (SecurityException e) {
            Log.w(TAG, e);
        }
        catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
        return false;
    }
    
    public static Constructor<?> getConstructorSafely(Class<?> cls, Class<?>... parameterTypes)
    {
        AtomicReference<Constructor<?>> result = new AtomicReference<Constructor<?>>();
        if (getConstructorSafely(result, cls, parameterTypes)) {
            return result.get();
        }
        return null;
    }

    public static Class<?> classForName(final String name) {
        Class<?> cls = null;
        try {
            cls = Class.forName(name);
        } catch (Exception exception) {
            Log.e(TAG, "", exception);
        }
        return cls;
    }

    public static boolean getMethodSafely(AtomicReference<Method> result, Class<?> cls, String name, Class<?>... parameterTypes)
    {
        try {
            if (cls == null) {
                return false;
            }
            result.set(cls.getMethod(name, parameterTypes));
            return true;
        }
        catch (SecurityException e) {
            if (Debug.getDebug()) {
                Log.w(TAG, e);
            }
        }
        catch (NoSuchMethodException e) {
            if (Debug.getDebug()) {
                Log.w(TAG, e.toString());
            }
        }
        return false;
    }
    
    public static Method getMethodSafely(Class<?> cls, String name,  Class<?>... parameterTypes)
    {
        AtomicReference<Method> result = new AtomicReference<Method>();
        if (getMethodSafely(result, cls, name, parameterTypes)) {
            return result.get();
        }
        return null;
    }
    
    public static boolean getStaticIntFieldSafely(AtomicReference<Integer> result, Class<?> cls, String name)
    {
        try {
            if (cls == null) {
                return false;
            }
            int n = cls.getField(name).getInt(null);
            result.set(Integer.valueOf(n));
            return true;
        }
        catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        }
        catch (SecurityException e) {
            Log.w(TAG, e);
        }
        catch (IllegalAccessException e) {
            Log.w(TAG, e);
        }
        catch (NoSuchFieldException e) {
            Log.w(TAG, e);
        }
        return false;
    }
    
    public static int getStaticIntFieldSafely(Class<?> cls, String name)
    {
        AtomicReference<Integer> result = new AtomicReference<Integer>();
        if (getStaticIntFieldSafely(result, cls, name)) {
            return result.get().intValue();
        }
        
        return 0;
    }
    
    public static boolean getStaticFieldSafely(AtomicReference<Object> result, Class<?> cls, String name)
    {
        try {
            if (cls == null) {
                return false;
            }
            result.set(cls.getField(name).get(null));
            return true;
        }
        catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        }
        catch (SecurityException e) {
            Log.w(TAG, e);
        }
        catch (IllegalAccessException e) {
            Log.w(TAG, e);
        }
        catch (NoSuchFieldException e) {
            Log.w(TAG, e);
        }
        return false;
    }
    
    public static Object getStaticFieldSafely(Class<?> cls, String name)
    {
        AtomicReference<Object> result = new AtomicReference<Object>();
        if (getStaticFieldSafely(result, cls, name)) {
            return result.get();
        }
        
        return null;
    }
    
    public static boolean constructObjectSafely(AtomicReference<Object> result, Constructor<?> constructor, Object... args)
    {
        if (constructor == null) {
            return false;
        }
        
        try {
            result.set(constructor.newInstance(args));
            return true;
        }
        catch (Throwable tr) {
            Log.w(TAG, "", tr);
        }
        
        return false;
    }

    public static Object newInstance(Constructor<?> constructor, Object... args)
    {
        AtomicReference<Object> result = new AtomicReference<Object>();
        if (constructObjectSafely(result, constructor, args)) {
            return result.get();
        }
        
        return null;
    }
    
    /**
     * If this method is static, the receiver argument is ignored.
     * 
     * @param result
     * @param method
     * @param receiver
     * @param args
     * @return
     */
    public static boolean invokeMethodSafely(AtomicReference<Object> result, Method method, Object receiver, Object... args)
    {
        if (method == null) {
            return false;
        }
        
        try {
            result.set(method.invoke(receiver, args));
            return true;
        }
        catch (Throwable tr) {
            Debug.w(tr);
        }
        return false;
    }
    
    /**
     * If this method is static, the receiver argument is ignored.
     * 
     * @param method
     * @param receiver
     * @param args
     * @return
     */
    public static Object invokeMethodSafely(Method method, Object receiver, Object... args)
    {
        AtomicReference<Object> result = new AtomicReference<Object>();
        if (invokeMethodSafely(result, method, receiver, args)) {
            if (result.get() != null) {
                return result.get();
            }
            return sDummyObject;
        }
        
        return null;
    }

    public static Method getDeclaredMethodSafely(Class<?> cls, String name,  Class<?>... parameterTypes) {
        AtomicReference<Method> result = new AtomicReference<Method>();
        if (getDeclaredMethod(result, cls, name, parameterTypes)) {
            return result.get();
        }
        return null;
    }

    public static boolean getDeclareIntFieldSafely(AtomicReference<Integer> result, Class<?> cls, Object obj, String name) {
        try {
            if (cls == null) {
                return false;
            }
            Field field = cls.getDeclaredField(name);
            if (field == null) {
                return false;
            }
            field.setAccessible(true);
            int n = field.getInt(obj);
            result.set(n);
            return true;
        }
        catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        }
        catch (SecurityException e) {
            Log.w(TAG, e);
        }
        catch (IllegalAccessException e) {
            Log.w(TAG, e);
        }
        catch (NoSuchFieldException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static boolean getDeclareStringFieldSafely(AtomicReference<String> result, Class<?> cls, Object obj, String name) {
        try {
            if (cls == null) {
                return false;
            }
            Field field = cls.getDeclaredField(name);
            if (field == null) {
                return false;
            }
            field.setAccessible(true);
            String string = (String) field.get(obj);
            result.set(string);
            return true;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        } catch (SecurityException e) {
            Log.w(TAG, e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (NoSuchFieldException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static int getDeclareIntFieldSafely(String clsName, Object obj, String name) {
        return getDeclareIntFieldSafely(classForName(clsName), obj, name);
    }

    public static int getDeclareIntFieldSafely(Class<?> cls, Object obj, String name) {
        AtomicReference<Integer> result = new AtomicReference<Integer>();
        if (getDeclareIntFieldSafely(result, cls, obj, name)) {
            return result.get();
        }

        return 0;
    }

    public static String getDeclareStringFieldSafely(Class<?> cls, Object obj, String name) {
        AtomicReference<String> result = new AtomicReference<>();
        if (getDeclareStringFieldSafely(result, cls, obj, name)) {
            return result.get();
        }

        return null;
    }

    public static boolean getStaticInnerClassDeclareIntFieldSafely(AtomicReference<Integer> result, Class<?> cls, String innerClsName, String fieldName) {
        try {
            if (cls == null) {
                return false;
            }
            Class[] innerClazz = cls.getDeclaredClasses();
            for (Class innerCls : innerClazz) {
                int mod = innerCls.getModifiers();
                String modifier = Modifier.toString(mod);
                if (modifier.contains("static") && innerCls.getSimpleName().contains(innerClsName)) {
                    Object obj = innerCls.newInstance();
                    Field field = innerCls.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    int n = field.getInt(obj);
                    result.set(n);
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        } catch (SecurityException e) {
            Log.w(TAG, e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (NoSuchFieldException e) {
            Log.w(TAG, e);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getStaticInnerClassDeclareIntFieldSafely(String clsName, String innerClsName, String fieldName) {
        return getStaticInnerClassDeclareIntFieldSafely(classForName(clsName), innerClsName, fieldName);
    }

    public static int getStaticInnerClassDeclareIntFieldSafely(Class<?> cls, String innerClsName, String fieldName) {
        AtomicReference<Integer> result = new AtomicReference<Integer>();
        if (getStaticInnerClassDeclareIntFieldSafely(result, cls, innerClsName, fieldName)) {
            return result.get();
        }
        return 0;
    }

    public static boolean setDeclareIntFieldSafely(String clsName, Object obj, String fieldName, int targetValue) {
        return setDeclareIntFieldSafely(classForName(clsName), obj, fieldName, targetValue);
    }

    public static boolean setDeclareIntFieldSafely(Class<?> cls, Object obj, String fieldName, int targetValue) {
        Field field;
        try {
            field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(obj, targetValue);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getDeclaredMethod(AtomicReference<Method> result, Class<?> cls, String name,  Class<?>... parameterTypes) {
        try {
            if (cls == null) {
                return false;
            }
            Method method = cls.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            result.set(method);
            return true;
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    public static Constructor<?> getDeclaredConstructorSafely(Class<?> cls, Class<?>... parameterTypes)
    {
        AtomicReference<Constructor<?>> result = new AtomicReference<Constructor<?>>();
        if (getDeclaredConstructorSafely(result, cls, parameterTypes)) {
            return result.get();
        }
        return null;
    }


    public static boolean getDeclaredConstructorSafely(AtomicReference<Constructor<?>> result, Class<?> cls, Class<?>... parameterTypes)
    {
        try {
            if (cls == null) {
                return false;
            }
            result.set(cls.getDeclaredConstructor(parameterTypes));
            return true;
        }
        catch (SecurityException e) {
            Log.w(TAG, e);
        }
        catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
        return false;
    }
}
