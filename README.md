# PostmanToRetrofit2
This is an Android Studio plugin which can generate Retrofit2's Java/Kotlin code from Postman's collection.

## Features:
<ul>
<li>Supports RxJava and Coroutines with Retrofit2</li>
<li>Class generation and selection for each request in the collection from the directory of your choice</li>
<li>Automatic class generation from saved responses in a collection</li>
<li>Sub-collections within a collection handling</li>
<li>Postman Url parameters (i.e. :param) handling</li>
<li>Postman form-data, form-urlencoded and raw body types handling</li>
<li>GUI to help selecting the Postman collection from the file system and to choose the conversion settings</li>
<li>mnemonics support in the GUI</li>
<li>Saving the last user settings per project</li>
</ul>

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
