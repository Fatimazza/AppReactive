package com.example.android.appreactive;

import com.jakewharton.rxbinding2.widget.RxTextView;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = (EditText) findViewById(R.id.email);


        //Object Observable
        Observable<CharSequence> email = RxTextView.textChanges(etEmail);

        //Map mengubah tipe data
        Observable<Boolean> emailMap =
            email
                .map(new Function<CharSequence, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull CharSequence charSequence) throws Exception {
                        if (charSequence.toString().contains("y")) {
                            return true;
                        }
                        return false;
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                        //return-nya adalah masukan
                        return aBoolean;
                    }
                });

        //Map: diterima sbg 1 item ListString baru diubah jd String
        //FlatMap: langsung dipecah, jd diterima sbg String (5x karena isinya 5 misal)

        Observable<Character> emailChar = email.flatMap(new Function<CharSequence, ObservableSource<Character>>() {
            @Override
            public ObservableSource<Character> apply(@NonNull CharSequence charSequence) throws Exception {
                List<Character> ret = new ArrayList<Character>();
                for(int i=0; i < charSequence.length(); i++){
                    ret.add(charSequence.charAt(i));
                }
//                Character[] ray = {'a','b','s'};
                return Observable.fromIterable(ret);
//                return Observable.fromArray(ray);
            }
        });

        //abaikan
        //Tiap chain (.) itu stream-nya beda
        RxTextView
            .textChanges(etEmail)
            .map(new Function<CharSequence, Object>() {
                @Override
                public Object apply(@NonNull CharSequence charSequence) throws Exception {
                    return null;
                }
            });


        //Object Observer
        //ctrl+G buat ganti return type
        Observer<CharSequence> emailObserver = new Observer<CharSequence>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull CharSequence charSequence) {
                Log.d("Rx onNext", charSequence.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };

        Observer<Boolean> emailObserverAfterMap = new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                Log.d("Rx onNext", String.valueOf(aBoolean));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };

        Observer<Character> emailObserverFlatMap = new Observer<Character>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Character character) {
                Log.d("Rx onNext char", String.valueOf(character));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };

        //proses Subscribing
        //Observable-nya disubscribe
        email.subscribe(emailObserver); //di main thread

        //proses Subscribing yg lain, observable di thread yang lain
        email.subscribeOn(Schedulers.newThread()) //diproses di thread lain
            .observeOn(AndroidSchedulers.mainThread()) //nilai dikembalikan ke main thread
            .subscribe(emailObserver);

        emailMap.subscribe(emailObserverAfterMap);

        emailChar.subscribe(emailObserverFlatMap);

    }
}
