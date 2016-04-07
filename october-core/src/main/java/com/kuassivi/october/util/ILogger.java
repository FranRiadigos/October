package com.kuassivi.october.util;

public interface ILogger {
    void v(String tag, String msg);

    void v(String tag, String msg, Throwable e);

    void d(String tag, String msg);

    void d(String tag, String msg, Throwable e);

    void i(String tag, String msg);

    void i(String tag, String msg, Throwable e);

    void w(String tag, String msg);

    void w(String tag, String msg, Throwable e);

    void e(String tag, String msg);

    void e(String tag, String msg, Throwable e);

    void track();

    void track(String msg);

    void track(String tag, String msg);

    void logException(Throwable thr);

    void setProjectPackage(String projectPackage);
}
