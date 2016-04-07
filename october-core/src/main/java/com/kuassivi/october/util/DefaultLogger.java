package com.kuassivi.october.util;


import android.text.TextUtils;
import android.util.Log;

/**
 * Custom logger that avoids to show Log messages in Production environment!
 */
public abstract class DefaultLogger implements ILogger {

    private static final String TRACKING_TAG = "TRACKING";
    private static final String TRACKING_METHOD = "track";
    private static final String JAVA_EXTENSION_FILE = ".java";
    private static final String TRACKING_METHOD_PATTERN = "^\\w+\\(\\)\\s*\\-\\>\\s*(.*)";

    protected String projectPackage;

    @Override
    public void v(String tag, String msg) {
        v(tag, msg, null);
    }

    @Override
    public void v(String tag, String msg, Throwable e) {
        processLog(Log.VERBOSE, tag, msg, e);
    }

    @Override
    public void d(String tag, String msg) {
        d(tag, msg, null);
    }

    @Override
    public void d(String tag, String msg, Throwable e) {
        processLog(Log.DEBUG, tag, msg, e);
    }

    @Override
    public void i(String tag, String msg) {
        i(tag, msg, null);
    }

    @Override
    public void i(String tag, String msg, Throwable e) {
        processLog(Log.INFO, tag, msg, e);
    }

    @Override
    public void w(String tag, String msg) {
        w(tag, msg, null);
    }

    @Override
    public void w(String tag, String msg, Throwable e) {
        processLog(Log.WARN, tag, msg, e);
    }

    @Override
    public void e(String tag, String msg) {
        e(tag, msg, null);
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        processLog(Log.ERROR, tag, msg, e);
    }

    @Override
    public void track() {
        track(null);
    }

    @Override
    public void track(String msg) {
        track(TRACKING_TAG, msg);
    }

    @Override
    public void track(String tag, String msg) {
        Log.i(tag, getStackInfo(msg));
    }

    @Override
    public void setProjectPackage(String projectPackage) {
        this.projectPackage = projectPackage;
    }

    protected String getStackInfo(String msg) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        String trace = "", method = "", lastMethod = "", lastClassName = "";
        boolean isTraceMethod = false;
        for (StackTraceElement e : stacktrace) {
            if (e.getClassName().equals(DefaultLogger.class.getName())
                    && TextUtils.equals(e.getMethodName(), TRACKING_METHOD)) {
                isTraceMethod = true;
            } else if (isTraceMethod) {
                if (e.getClassName().contains(projectPackage)) {
                    if (e.getMethodName().equals(method)
                            || e.getMethodName().equals(lastMethod)
                            || lastMethod.equals(method)) {
                        trace = trace.replaceFirst(TRACKING_METHOD_PATTERN, "$1");
                    }
                    String currentClassName = e.getFileName().replace(JAVA_EXTENSION_FILE, "");
                    if (currentClassName.equals(lastClassName)) {
                        continue;
                    }
                    lastClassName = currentClassName;
                    trace = (!e.getMethodName().equals(method) ? e.getMethodName()
                            + "() -> " : "")
                            + "[" + lastClassName + "] -> "
                            + trace;
                    if (lastMethod.isEmpty()) {
                        lastMethod = e.getMethodName();
                    }
                    method = e.getMethodName();
                } else {
                    break;
                }
            }
        }
        trace = trace.replaceFirst(TRACKING_METHOD_PATTERN, "$1") + lastMethod + "()";

        return trace + (msg != null ? ": " + msg : "");
    }

    public abstract void processLog(int priority, String tag, String msg, Throwable e);
}
