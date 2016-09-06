package com.xy.rxbinding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class MainActivity extends RxAppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate==>");
        Button skipBtn = (Button) findViewById(R.id.skip_btn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RxBindingActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "onCreate==>Unsubscribing subscription from onCreate()");
                    }
                })
                //Note:手动设置在activity onPause的时候取消订阅
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "onCreate==>Started in onCreate(), running until onPause(): " + aLong);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart==>");
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "onStart==>Unsubscribing subscription from onStart()");
                    }
                })
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "onStart==>Started in onCreate(), running until onStop(): " + aLong);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume==>");
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG, "onResume==>Unsubscribing subscription from onResume()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "onResume==>Started in onResume(), running until onDestroy(): " + aLong);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause==>");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop==>");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy==>");
    }
}
