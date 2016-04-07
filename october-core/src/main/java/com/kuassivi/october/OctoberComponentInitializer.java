package com.kuassivi.october;

import android.app.Application;

public interface OctoberComponentInitializer<T extends Application> {
    OctoberComponent initialize(T application);
}
