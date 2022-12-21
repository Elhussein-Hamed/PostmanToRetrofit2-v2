# PostmanToRetrofit2
This is an Android Studio plugin which can generate Retrofit2's java code from Postman's collection.

## Install
### From Android Studio plugin marketplace
Open Android Studio:
- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studio</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> >  Search and install **Postman to Retrofit2 V2**

### Installing manually
- Download the latest plugin zip file from [here](https://github.com/Elhussein-Hamed/PostmanToRetrofit2-v2/releases/latest)

- Open Android Studio
	- <kbd>File</kbd>  > <kbd>Settings</kbd> (For Mac <kbd>Android Studion</kbd> > <kbd>Preferences</kbd>) > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk..</kbd> > import **PostmanToRetrofit2-v2-Plugin-*.zip**

- Restart Android Studio

## Usage
### Import Retrofit2
gradle

	implementation 'com.squareup.retrofit2:retrofit:2.5.0'

Other versions can be found in [Maven](https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit)

### Import RxJava2 (Optional)
gradle

	implementation "io.reactivex.rxjava2:rxjava:2.0.1"

Other versions can be found in [Maven](https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava)

![plugin_preview](https://user-images.githubusercontent.com/36441143/208793968-f6bfe719-98f5-428d-8007-7912770e8c95.gif)
