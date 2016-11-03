package com.elliottj.rxandroidpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String CLS_TAG = MainActivity.class.getSimpleName();

    private Subscription counterSubscription;

    private Button mUnsubscribeButton;
    private TextView mCounterTextView;

    private View.OnClickListener unsubscribeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "Unsubscribed!", Toast.LENGTH_SHORT).show();
            counterSubscription.unsubscribe();
            mUnsubscribeButton.setEnabled(false);
        }
    };

    // emits each number, one at a time.
    Observable<Integer> computeNumbersObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            int i = 0;

            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    subscriber.onError(e); // report error
                }

                subscriber.onNext(i++); // emit data

                if (i == 10) {
                    break;
                }

            }

            subscriber.onCompleted(); // indicate stream completion
        }
    }).cache();

    // Observer to take an action on an Observable
    Observer<Integer> integerObserver = new Observer<Integer>() {
        @Override
        public void onCompleted() {
            // There are no additional events coming and we can perform a cleanup.
            Log.d(CLS_TAG, "onCompleted");
            mCounterTextView.setText("Done!");
            mUnsubscribeButton.setEnabled(true);
        }

        @Override
        public void onError(Throwable e) {
            // An error has occurred and there will be no more events coming (Exceptions, etc.)
            Log.d(CLS_TAG, "onError", e);
        }

        @Override
        public void onNext(Integer integer) {
            Log.d(CLS_TAG, "onNext: " + integer);
            mCounterTextView.setText(String.valueOf(integer));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnsubscribeButton = (Button) findViewById(R.id.unsubscribe_button);
        mUnsubscribeButton.setOnClickListener(unsubscribeClickListener);

        mCounterTextView = (TextView) findViewById(R.id.counter_text_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        counterSubscription.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();

        counterSubscription = computeNumbersObservable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integerObserver);
    }
}
