# PostmanToRetrofit2
This is an Android Studio plugin which can generate Retrofit2's java code from Postman's collection.

## Install
### From Android Studio plugin marketplace
Open Android Studio:
- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studio</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> >  Search and install **Postman to Retrofit2 V2**

### Installing manually
- Clone or download **PostmanToRetrofit2-v2.jar**

- Open Android Studio
	- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studion</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> >  click <img src="gear_icon.png" align="center" alt="Gear Icon" style="height: 25px; width:30px;"/> > <kbd>Install plugin from disk..</kbd> > import **PostmanToRetrofit2-v2.jar**

- Restart Android Studio

## Usage
### Import Retrofit2
gradle

	implementation 'com.squareup.retrofit2:retrofit:2.5.0'

The version can be changed to suit the required need.
### Import RxJava2
gradle

	implementation "io.reactivex.rxjava2:rxjava:2.0.1"

The version can be changed to suit the required need.
	
### Create ApiService.java

<kbd>Right click</kbd>  > <kbd>Generate</kbd> > <kbd>Retrofit2Generator</kbd> > Paste your postman collection > <kbd>OK</kbd>

Or  

<kbd>Right click</kbd>  > <kbd>Generate</kbd> > <kbd>Retrofit2Generator</kbd> > <kbd>Select File</kbd> > Find and select postman collection file > <kbd>OK</kbd>

### Modify the generated response type
The plugin currently supports RxJava response types only. The default type used for the response is `Observable<T>`, where T is the object created from the APIs in Postman collection.

To change the type,
<kbd>Right click</kbd> > <kbd>Generate</kbd> > <kbd>Retrofit2Generator</kbd> > <kbd>Options</kbd> > Select the RxJava type from the drop-down menu

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
