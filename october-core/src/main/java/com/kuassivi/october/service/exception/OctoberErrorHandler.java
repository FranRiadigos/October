package com.kuassivi.october.service.exception;

import com.kuassivi.october.service.adapter.OctoberRetrofitFactory;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Response;

/**
 * Used in conjunction with {@link OctoberRetrofitFactory}.
 */
public interface OctoberErrorHandler {

    IOException handleError(@NonNull Response r);
}
