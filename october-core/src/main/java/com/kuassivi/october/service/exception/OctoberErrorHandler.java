package com.kuassivi.october.service.exception;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Response;

public interface OctoberErrorHandler {

    IOException handleError(@NonNull Response r);
}
