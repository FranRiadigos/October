# October [![Circle CI](https://circleci.com/gh/kuassivi/October/tree/master.svg?style=svg)](https://circleci.com/gh/kuassivi/October/tree/master)
[![Apache 2.0](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![Android](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)

Android MVP Framework that aims to make easier developers life.


**Some features:**

  - Safe Model View Presenter architecture following standard Android lifecycle.
  - Fast and automatic dependency injection using Dagger 2.
  - Provides some Repository Strategy sources.
  - Error handling interface for latest Retrofit version.
  
## Wiki

> Coming soon!

  - Getting started
  - Working with Fragments
  - UseCases, Repository and Cache
  - Working with Retrofit
  - Handling Exceptions
  - Providing objects with October Scopes (Dagger)
  - Dealing with Realm
  - Unit Testing
  - Espresso Testing

  
## Show me the code:

```java
/**
 * Presenters are going to respect the Android lifecycle and convention methods.
 * Common lifecycle Activity methods are going to delegate on the Presenter.
 */
@PerActivity // Per activity scope
public class SamplePresenter extends OctoberPresenter<ISampleView> 
    implements ISamplePresenter<ISampleView> {

    @Inject
    SampleUseCase sampleUseCase;
    
    @Inject
    public SamplePresenter() {}
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Setup Loading Dialog by default
        attachLoading(sampleUseCase); // (see wikis)
    }
    
    @Override
    public void onViewCreated() {
        super.onViewCreated();
        
        // Start loading some data
        sampleUseCase
            .asObservable()
            .compose(bindToLifecycle())
            .subscribe(new SampleSubscriber())
    }
    
    class SampleSubscriber extends OctoberSubscriber<Object> {
    
        @Override
        public void onError(OctoberException e) {
            e.printStackTrace();
            getView().showError(e.getMessage());
        }
    
        @Override
        public void onNext(Object data) {
            getView().render(data)
        }
    }
}
```

```java
/**
 * October will attach and detach automatically your presenter and view respectively.
 * It is only needed to provide the Presenter interface as a parameterized type.
 */
@ActivityComponent // Automatic Dagger component injection
public class SampleActivity extends OctoberCompatActivity<ISamplePresenter> 
    implements ISampleView {

    // October binds Butterknife automatically
    @Bind(R.id.sample_textview) TextView sampleTextView;

    // You don't need to initialize any Dagger Component here in order to inject
    // Just remember to annotate you class with @ActivityComponent or @FragmentComponent
    @Inject
    Navigator navigator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.activity_sample);
    }
    
    @OnClick(R.id.sample_button)
    public void onClick(View view) {
        getPresenter().onSampleButtonClicked();
    }
}
```


## Dependency

Latest stable version: 
[![Latest Version](https://api.bintray.com/packages/kuassivi/maven/october/images/download.svg) ](https://bintray.com/kuassivi/maven/october/_latestVersion) [![Bintray Version](https://img.shields.io/bintray/v/kuassivi/maven/october.svg)](http://jcenter.bintray.com/com/kuassivi/october/october/) [![Maven Central](https://img.shields.io/maven-central/v/com.kuassivi.october/october.svg)]()

In order to work with Dagger 2 and benefits from automatic dependency injection of October, 
you need to apply the [android-apt] gradle plugin to run annotation processing.

If you are working with gradle, add the dependency to your build.gradle file:
```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies{
    compile 'com.kuassivi.october:october:?.?.?'
    apt 'com.kuassivi.october:october-cache-compiler:?.?.?'
}
```

If you are working with maven, do it into your pom.xml
```xml
<dependency>
    <groupId>com.kuassivi.october</groupId>
    <artifactId>october</artifactId>
    <version>?.?.?</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.kuassivi.october</groupId>
    <artifactId>october-compiler</artifactId>
    <version>?.?.?</version>
    <type>pom</type>
</dependency>
```

If you are following any Clean Architecture design or you just need to access October features from a Java module, 
use the following dependency in your gradle java module:
```groovy
dependencies{
    compile 'com.kuassivi.october:october-core:?.?.?'
}
```


## Technologies, architectural and design patterns

  - Model View Presenter
  - Repository
  - Interactor UseCases
  - [RxJava](https://github.com/ReactiveX/RxJava)
  - [RxLifecycle](https://github.com/trello/RxLifecycle)
  - [Dagger2](http://google.github.io/dagger/)
  - [Butterknife](https://github.com/JakeWharton/butterknife)
  - [Cache](https://github.com/kuassivi/RepositoryCache)
  - [Realm](https://github.com/realm/realm-java)


## References

The following projects were references in my research.

  - [Dagger 2](http://google.github.io/dagger/)
  - [Mosby](https://github.com/sockeqwe/mosby)
  - [Android-CleanArchitecture](https://github.com/android10/Android-CleanArchitecture)
  - [Annotation Processing](http://hannesdorfmann.com/annotation-processing/annotationprocessing101)
  - [RxLifecycle](https://github.com/trello/RxLifecycle)

 
## License

    Copyright (C) 2015 Francisco Gonzalez-Armijo Riádigos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[android-apt]: https://bitbucket.org/hvisser/android-apt
