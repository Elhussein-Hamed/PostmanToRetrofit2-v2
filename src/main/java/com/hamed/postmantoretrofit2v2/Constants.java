package com.hamed.postmantoretrofit2v2;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String[] retrofit2RawTypes = { "Call<T>", "Call<Response<T>>" };

    public static final String[] retrofit2RawTypesKotlinCoroutines = { "T", "Response<T>" };

    public static final String[] rxJavaReturnTypes = { "Observable<T>", "Observable<Response<T>>", "Observable<Result<T>>",
            "Flowable<T>", "Flowable<Response<T>>", "Flowable<Result<T>>", "Single<T>", "Single<Response<T>>",
            "Single<Result<T>>", "Maybe<T>", "Maybe<Response<T>>", "Maybe<Result<T>>", "Completable" };

    public static final ArrayList<String> listOfReturnTypesWithoutClass =  new ArrayList<>(List.of(new String[]{"Completable"}));

    public static final ArrayList<String> supportedClassFileExtensions =  new ArrayList<>(List.of(new String[]{"java", "kt"}));


}
