package com.kuassivi.october.repository.mapper;

import java.util.Collection;

public interface OctoberDataMapper<T, R> {

    Collection<R> transform(Collection<T> dataCollection);
    R transform(T data);
}
