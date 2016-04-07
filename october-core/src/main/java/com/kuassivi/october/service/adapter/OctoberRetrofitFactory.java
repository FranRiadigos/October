package com.kuassivi.october.service.adapter;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Implementation of an Abstract Retrofit Rest Factory
 */
public abstract class OctoberRetrofitFactory {

    private Retrofit retrofit;
    private String apiUrl;

    final public <S> S create(Class<S> service) {
        return build().create(service);
    }

    final protected OkHttpClient.Builder getDefaultClientBuilder() {
        return new OkHttpClient.Builder();
    }

    final protected Retrofit.Builder getDefaultRetrofitBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(createConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    final protected Retrofit build() {
        retrofit = createRetrofitBuilder()
                .baseUrl(getApiUrl())
                .client(createClient())
                .build();
        return retrofit;
    }

    final protected Retrofit getRetrofit() {
        return retrofit;
    }

    final protected String getApiUrl() {
        return apiUrl;
    }

    final protected void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    protected abstract OkHttpClient createClient();

    protected abstract Retrofit.Builder createRetrofitBuilder();

    protected abstract Converter.Factory createConverterFactory();
}
