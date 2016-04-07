package com.kuassivi.october.service.adapter.interceptor;

import com.kuassivi.october.service.exception.OctoberErrorHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class OctoberErrorInterceptor implements Interceptor {

    OctoberErrorHandler errorHandler;

    public OctoberErrorInterceptor(OctoberErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        IOException exception = errorHandler.handleError(response);
        if (exception != null)
            throw exception;

        return response;
    }
}
