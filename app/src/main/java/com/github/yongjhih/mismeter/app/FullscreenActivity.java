package com.github.yongjhih.mismeter.app;

import android.animation.ValueAnimator;
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
import rx.android.schedulers.AndroidSchedulers;
import com.github.yongjhih.mismeter.*;

public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        View contentView = findViewById(R.id.fullscreen);
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        final MisMeter meter = (MisMeter) findViewById(R.id.meter);
        final MisMeter meter2 = (MisMeter) findViewById(R.id.meter2);
        final MisMeter meter3 = (MisMeter) findViewById(R.id.meter3);


        final Handler handler =  new Handler();

        Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

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
        });


    }

    private void animate(@NonNull MisMeter meter) {
        animate(meter, (new Random().nextFloat() % 0.5f) + 0.3f);
    }

    private void animate(@NonNull final MisMeter meter, float progress) {
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
