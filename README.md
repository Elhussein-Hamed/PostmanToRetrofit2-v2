# PostmanToRetrofit2
This is an Android Studio plugin which can generate Retrofit2's java code from Postman's collection.

## Install
### From Android Studio plugin marketplace
Open Android Studio:
- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studio</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> >  Search and install **Postman to Retrofit2 V2**

### Installing manually
- Download the latest plugin jar file from [here](https://github.com/Elhussein-Hamed/PostmanToRetrofit2-v2/releases/latest)

- Open Android Studio
	- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studion</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk..</kbd> > import **PostmanToRetrofit2-v2-Plugin-*.jar**

- Restart Android Studio

## Usage
### Import Retrofit2
gradle

	implementation 'com.squareup.retrofit2:retrofit:2.5.0'

Other versions can be found in [Maven](https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit)

### Import RxJava2
gradle

	implementation "io.reactivex.rxjava2:rxjava:2.0.1"

Other versions can be found in [Maven](https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava)

![plugin_preview](https://user-images.githubusercontent.com/36441143/205748419-1330b67f-49e0-4c4f-9bfb-eef37ef1e7d7.gif)

### Modify the generated response type
The plugin currently supports RxJava response types only. The default type used for the response is `Observable<T>`, where T is the object created from the APIs in Postman collection.

To change the type,
<kbd>Right click</kbd> > <kbd>Generate</kbd> > <kbd>Retrofit2Generator</kbd> > <kbd>Options</kbd> > Select the RxJava type from the drop-down menu.

The other types supported are
* `Observable<Response<T>>`
* `Observable<Result<T>>`
* `Flowable<T>`
* `Flowable<Response<T>>`
* `Flowable<Result<T>>`
* `Single<T>`
* `Single<Response<T>>`
* `Single<Result<T>>`
* `Maybe<T>`
* `Maybe<Response<T>>`
* `Maybe<Result<T>>`
* `Completable`
