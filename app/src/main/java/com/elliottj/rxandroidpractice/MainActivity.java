package com.elliottj.rxandroidpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    public static final String CLS_TAG = MainActivity.class.getSimpleName();

    // emits each number, one at a time.
    Observable<Integer> integerObservable = Observable.just(4, 8, 15, 16, 23, 42);

    // Observer to take an action on an Observable
    Observer<Integer> integerObserver = new Observer<Integer>() {
        @Override
        public void onCompleted() {
            // There are no additional events coming and we can perform a cleanup.
            Log.d(CLS_TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            // An error has occurred and there will be no more events coming (Exceptions, etc.)
            Log.d(CLS_TAG, "onError", e);
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(CLS_TAG, "onNext: " + integer);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        integerObservable.subscribe(integerObserver);
    }
}
