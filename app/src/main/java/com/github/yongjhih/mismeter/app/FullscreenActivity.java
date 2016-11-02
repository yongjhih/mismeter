package com.github.yongjhih.mismeter.app;

import android.animation.ValueAnimator;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import com.github.yongjhih.mismeter.*;

// Kotlin, RxJava2, RxLifeCycle instead?
public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;
    private CompositeSubscription mSubs = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        // TODO BindView injection with such as Butterknife
        mContentView = findViewById(R.id.fullscreen);
        final MisMeter meter = (MisMeter) findViewById(R.id.meter);
        final MisMeter meter2 = (MisMeter) findViewById(R.id.meter2);
        final MisMeter meter3 = (MisMeter) findViewById(R.id.meter3);

        final Handler handler =  new Handler();

        mSubs.add(Observable.interval(1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                // nothing
            }

            @Override
            public void onError(Throwable e) {
                // nothing
            }

            @Override
            public void onNext(final Long aLong) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        animate(meter);
                        animate(meter2);
                        animate(meter3);
                    }
                });
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSubs.unsubscribe();
    }

    // TODO built-in MisMeter?
    private void animate(@NonNull MisMeter meter) {
        animate(meter, (new Random().nextFloat() % 0.5f) + 0.3f);
    }

    private void animate(@NonNull final MisMeter meter, @FloatRange(from = 0.0f, to=1.0f) float progress) {
        ValueAnimator anim = ValueAnimator.ofFloat(meter.progress, progress);
        if (meter.progress > progress) {
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(750);
        } else {
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(500);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                meter.setProgress((float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
    }
}
