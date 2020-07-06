/*==================================================================================================
 RoboLib - An Expansion and Improvement Library for WPILibJ
 Copyright (C) 2015  Glossawy

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 =================================================================================================*/


package org.usfirst.frc.team1554.lib.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class ReflectionHelper {

    // Retrieve Object at Field.
    private static Object retrieveField(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException {
        Object item;
        final boolean accessible = field.isAccessible();

        if (!accessible) {
            field.setAccessible(!accessible);
        }

        item = field.get(obj);

        if (!accessible) {
            field.setAccessible(accessible);
        }

        return item;
    }

    /**
     * Retrieve Value from Static Field (Belonging to No Particular
     * Object)
     *
     * @param klass           Containing Class
     * @param fieldName       Name of Filed
     * @param conversionClass Class to Convert Result To (prevents Type Mismatch)
     * @return Value of Static Field of type 'conversionClass'
     */
    public static <T> T getStaticField(Class<?> klass, String fieldName, Class<T> conversionClass) {
        conversionClass = (Class<T>) ensureClassNotPrimitive(conversionClass);

        try {
            return conversionClass.cast(retrieveField(klass.getDeclaredField(fieldName), null));
        } catch (final Exception e) {
            if (klass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failure to reflectively obtain Static Field '" + fieldName + "'!", e);
        }

        return getStaticField(klass.getSuperclass(), fieldName, conversionClass);
    }

    /**
     * Retrieve Value from Instance Field (Belonging to the Given
     * Object)
     *
     * @param instClass       Instance Type
     * @param instance        Containing Instance
     * @param fieldName       Name of Field in Instance Type
     * @param conversionClass Class to Convert Result To (prevents Type Mismatch)
     * @return Value of Field in Instance
     */
    public static <T> T getInstanceField(Class<?> instClass, Object instance, String fieldName, Class<T> conversionClass) {
        try {
            conversionClass = (Class<T>) ensureClassNotPrimitive(conversionClass);
            return conversionClass.cast(retrieveField(instClass.getDeclaredField(fieldName), instance));
        } catch (final Exception e) {
            if (instClass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failure to reflectively obtain Instance Field '" + fieldName + "' from Object '" + instance + "'!", e);
        }

        return getInstanceField(instClass.getSuperclass(), instance, fieldName, conversionClass);
    }

    /**
     * Create New One Dimensional Array of Some Type
     *
     * @param klass Class Type of Array Elements
     * @param size  Size of Array (value of array.length)
     * @return Array of Element Type 'klass'
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<? extends T> klass, int size) {
        return (T[]) Array.newInstance(klass, size);
    }

    /**
     * Create new Two Dimensional Array of Some Type
     *
     * @param klass Class Type of Array Elements
     * @param size1 Number of sub arrays
     * @param size2 Length of sub arrays
     * @return Two-Dimensional Array of the provided type that is size1 x size2
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] newArray2D(Class<? extends T> klass, int size1, int size2) {
        return (T[][]) Array.newInstance(klass, size1, size2);
    }

    // Set Field to Some Value and return the Old Value
    private static <T> Object setField(Field field, T newValue, Object instance) throws IllegalArgumentException, IllegalAccessException {
        Object old = null;
        final boolean accessible = field.isAccessible();

        if (!accessible) {
            field.setAccessible(!accessible);
        }

        old = retrieveField(field, instance);
        field.set(instance, newValue);

        field.setAccessible(accessible);

        return old;
    }

    /**
     * Set Value of Some Static Field (Not Belonging to Any Particular
     * Object)
     *
     * @param klass     Containing Class
     * @param fieldName Name of Field
     * @param newValue  Value to Set Field to
     * @return Previous Field Value
     */
    public static <T> Object setStaticField(Class<?> klass, String fieldName, T newValue) {
        try {
            return setField(klass.getDeclaredField(fieldName), newValue, null);
        } catch (final Exception e) {
            if (klass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failure to Reflectively Set Static Field '" + fieldName + "' to " + newValue + "!", e);
        }

        return setStaticField(klass.getSuperclass(), fieldName, newValue);
    }

    /**
     * Set Value of Some Instance Field (Belonging to A Particular
     * Object)
     *
     * @param instance  Containing Instance
     * @param fieldName Name of Field
     * @param newValue  Value to set Field to
     * @return Previous Field Value
     */
    public static <T> Object setInstanceField(Class<?> instClass, Object instance, String fieldName, T newValue) {
        try {
            return setField(instClass.getDeclaredField(fieldName), newValue, instance);
        } catch (final Exception e) {
            if (instClass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failure to Reflectively Set Instance Field '" + fieldName + "' in " + instance + " to " + newValue + "!", e);
        }

        return setInstanceField(instClass.getSuperclass(), instance, fieldName, newValue);
    }

    private static Object invokeMethod(Method meth, Object inst, Object[] args) throws IllegalAccessException, InvocationTargetException {

        boolean accessible = meth.isAccessible();

        if (!accessible)
            meth.setAccessible(!accessible);

        Object res = meth.invoke(inst, args);

        meth.setAccessible(accessible);
        return res;
    }

    public static <T> T invokeStaticMethod(Class<?> klass, CallParameters params, Class<T> conversionClass) {
        conversionClass = (Class<T>) ensureClassNotPrimitive(conversionClass);
        try {
            T res = null;
            if (conversionClass != null)
                res = conversionClass.cast(invokeMethod(klass.getDeclaredMethod(params.name, params.callTypes), null, params.callArguments));

            return res;
        } catch (Exception e) {
            if (klass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failed to invoke static method '" + params.name + "' with parameters: " + params + "!", e);
        }

        return invokeStaticMethod(klass.getSuperclass(), params, conversionClass);
    }

    public static void invokeStaticMethod(Class<?> klass, CallParameters params) {
        invokeStaticMethod(klass, params, null);
    }

    public static <T> T invokeInstanceMethod(Class<?> klass, Object inst, CallParameters params, Class<T> conversionClass) {
        try {
            conversionClass = (Class<T>) ensureClassNotPrimitive(conversionClass);

            T res = null;
            if (conversionClass != null)
                res = conversionClass.cast(invokeMethod(klass.getDeclaredMethod(params.name, params.callTypes), inst, params.callArguments));

            return res;
        } catch (Exception e) {
            if (klass.getSuperclass() == null)
                throw new RuntimeReflectionException("Failed to invoke instance method '" + params.name + "' on instance " + inst + " with parameters: " + params, e);
        }

        return invokeInstanceMethod(klass.getSuperclass(), inst, params, conversionClass);
    }

    public static void invokeInstanceMethod(Class<?> klass, Object inst, CallParameters params) {
        invokeInstanceMethod(klass, inst, params, null);
    }

    private static <T> T invokeConstructor(Constructor<? extends T> cons, Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T res;
        boolean access = cons.isAccessible();

        if (!access)
            cons.setAccessible(!access);

        res = cons.newInstance(args);

        cons.setAccessible(access);
        return res;
    }

    public static <T> T newInstance(Class<? extends T> klass) {
        try {
            return invokeConstructor(klass.getDeclaredConstructor());
        } catch (Exception e) {
            throw new RuntimeReflectionException("Failed to Reflectively Create instance of " + klass.getName(), e);
        }
    }

    public static <T> T newInstance(Class<? extends T> klass, CallParameters params) {
        try {
            return invokeConstructor(klass.getDeclaredConstructor(params.callTypes), params.callArguments);
        } catch (Exception e) {
            throw new RuntimeReflectionException("Failed to Reflectively Create Instance of " + klass.getName() + " with parameters: " + params, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newUnknownInstance(Class<?> klass, CallParameters params) {
        try {
            return (T) klass.getDeclaredConstructor(params.callTypes).newInstance(params.callArguments);
        } catch (Exception e) {
            throw new RuntimeReflectionException("Failed to Reflectively Create Instance of " + klass.getName() + " with parameters: " + params, e);
        }
    }

    /**
     * Useful abstraction of the details required to make a call to a
     * Constructor or Method. <br />
     * Supports Parameters, Parameter Type Guessing and Null
     * Parameters.
     */
    public static class CallParameters {

        /**
         * Name Descriptor to Call (Typically Constructor or Method
         * Name)
         */
        public final String name;
        private Object[] callArguments = new Object[0];
        private Class<?>[] callTypes = new Class<?>[0];
        private int size = 0;

        private CallParameters(String methodName) {
            this.name = methodName;
        }

        /**
         * Creates a Method Call where all required parameters are of the right type, but null.
         *
         * @param name       Method Name as per Signature
         * @param paramTypes Full List of Parameter Types, or none.
         * @return Empty Method Call
         */
        public static CallParameters createEmptyMethodCall(String name, Class<?>... paramTypes) {
            CallParameters params = createMethodCall(name);

            for (Class<?> klass : paramTypes)
                params.paramNull(klass);

            return params;
        }

        /**
         * Creates an Empty Constructor Call, a Constructor call where every parameter is null but of the right type.
         *
         * @param paramTypes Full list of Parameters Types, or none.
         * @return Empty Constructor Call
         */
        public static CallParameters createEmptyConstructorCall(Class<?>... paramTypes) {
            CallParameters params = createConstructorCall();

            for (Class<?> klass : paramTypes)
                params.paramNull(klass);

            return params;
        }

        /**
         * Initializes Method Call Parameters for the given Method
         *
         * @param methodName Method Name as per Method Signature
         * @return No Parameter Method Call (to add to)
         */
        public static CallParameters createMethodCall(String methodName) {
            return new CallParameters(methodName);
        }

        /**
         * Initializes Constructor Call Parameters
         *
         * @return No Parameter Constructor Call (to add to)
         */
        public static CallParameters createConstructorCall() {
            return new CallParameters("Constructor");
        }

        /**
         * Appends a Parameter to the current CallParameters. The Type argType should be appropriate for the method's
         * parameter types and in the right order.
         *
         * @param arg     Argument
         * @param argType Argument Type
         * @return This CallParameters object for chaining
         */
        public CallParameters param(Object arg, Class<?> argType) {
            append(arg, argType);

            return this;
        }

        /**
         * Guesses the Parameter Type based on the value provided by calling {@link Object#getClass()}/
         *
         * @param arg Argument Value
         * @return This CallParameters object for chaining.
         */
        public CallParameters paramGuess(Object arg) {
            append(arg, arg.getClass());

            return this;
        }

        /**
         * Appends a Parameter to this CallParameters where the Value is Null but of type argType.
         *
         * @param argType Argument Type
         * @return This CallParameters object for chaining.
         */
        public CallParameters paramNull(Class<?> argType) {
            append(null, argType);

            return this;
        }

        private void append(Object obj, Class<?> klass) {
            int oldSize = size;
            if (size + 1 > callArguments.length)
                resize(size + 1);

            callArguments[oldSize] = obj;
            callTypes[oldSize] = klass;
        }

        private void resize(int newSize) {
            if (newSize <= size)
                return;

            Object[] newArgs = new Object[newSize];
            Class<?>[] newTypes = new Class<?>[newSize];
            System.arraycopy(callArguments, 0, newArgs, 0, callArguments.length);
            System.arraycopy(callTypes, 0, newTypes, 0, callTypes.length);

            callArguments = newArgs;
            callTypes = newTypes;
            size = newSize;
        }

        /**
         * Prints the basic method signature. <br />
         * <br />
         * <tt>{methodName(arg1: argType1, arg2: argType2, ...)}</tt>
         *
         * @return
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean element = false;

            sb.append('{').append(name).append('(');
            for (int i = 0; i < callArguments.length; i++) {
                element = true;
                sb.append(String.valueOf(callArguments[i]))
                        .append(": ").append(callTypes[i].getSimpleName())
                        .append(", ");
            }

            if (element)
                sb.replace(sb.length() - 2, sb.length(), "").trimToSize();
            return sb.append(")}").toString();
        }
    }

    /**
     * Returns either the class given if it is not a Primitive Class or the Object Class of this Primitive
     * Class. e.g. Integer.class for int.class and Double.class for double.class. <br />
     * <br />
     * The Primitive Class is that value returned by {@link Class#getPrimitiveClass(String)}.
     *
     * @param primitiveClass
     * @return
     */
    public static Class<?> mapPrimitiveClass(Class<?> primitiveClass) {
        return ensureClassNotPrimitive(primitiveClass);
    }

    /**
     * Returns either this class if it is not a Primitive Class or the Object Class of this Primitive
     * Class. e.g. Integer.class for int.class and Double.class for double.class. <br />
     * <br />
     * The Primitive Class is that value returned by {@link Class#getPrimitiveClass(String)}.
     *
     * @param klass
     * @return
     */
    private static Class<?> ensureClassNotPrimitive(Class<?> klass) {
        if (klass == byte.class)
            return Byte.class;
        else if (klass == short.class)
            return Short.class;
        else if (klass == int.class)
            return Integer.class;
        else if (klass == long.class)
            return Long.class;
        else if (klass == float.class)
            return Float.class;
        else if (klass == double.class)
            return Double.class;
        else
            return klass;
    }

    private static class RuntimeReflectionException extends RuntimeException {
        public RuntimeReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
