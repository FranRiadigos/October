package com.kuassivi.example.data.services.adapter;

import com.kuassivi.october.service.adapter.OctoberRetrofitFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

@Singleton
public class RetrofitFactory extends OctoberRetrofitFactory {

    @Inject
    public RetrofitFactory() {}

    public <S> S createPrivate(Class<S> service) {
        return create(service);
    }

    @Override
    protected OkHttpClient createClient() {
        return null;
    }

    @Override
    protected Retrofit.Builder createRetrofitBuilder() {
        return null;
    }

    @Override
    protected Converter.Factory createConverterFactory() {
        return null;
    }
}
