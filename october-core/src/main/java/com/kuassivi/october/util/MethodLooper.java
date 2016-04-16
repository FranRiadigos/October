package com.kuassivi.october.util;

import android.util.Log;

/**
 * Utility class to prevent method loops.
 */
public class MethodLooper {

    /**
     * Prevent and warn when overloading the same method multiple times.
     *
     * @param methodName String of the method name to prevent infinite loop
     */
    public static void warning(Object that, String methodName) {
        Class<?> clazz = that.getClass();
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        boolean overloaded = false;
        int i = 0, o = 0;
        boolean isPreviousMethod = false;
        String currentMethod = "preventLooper";
        String preMethod = null;
        String conflictMethod = null;
        int conflictLineNumber = 0;
        for (StackTraceElement e : stacktrace) {
            i++;
            if (preMethod != null && conflictMethod == null
                && e.getClassName().equals(clazz.getName())) {
                conflictMethod = e.getMethodName();
                conflictLineNumber = e.getLineNumber();
            }
            if (isPreviousMethod) {
                preMethod = e.getMethodName();
            }
            if (e.getMethodName().equals(currentMethod)
                && e.getClassName().equals(clazz.getName())) {
                isPreviousMethod = true;
            }
            if (e.getMethodName().equals(methodName)) {
                if (o > 0) {
                    overloaded = true;
                    break;
                }
                o++;
            }
        }
        String previousMethod = stacktrace[i - 2].getMethodName();
        if (overloaded) {
            Log.w(clazz.getSimpleName(), "You are calling repeatedly" +
                                         " super." + previousMethod + "() from " +
                                         clazz.getSimpleName() + " -> "
                                         + methodName + "() method."
                                         + " The conflict is inside " +
                                         clazz.getSimpleName() + " -> " + conflictMethod
                                         + "() on line " + conflictLineNumber);
        }
    }
}
