package com.github.yongjhih.mismeter.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

import java.util.Random
import java.util.concurrent.TimeUnit

import rx.Observable
import rx.subscriptions.CompositeSubscription

import com.github.yongjhih.mismeter.*
import kotterknife.bindView

// RxJava2, RxLifecycle instead?
class FullscreenActivity : AppCompatActivity() {
    val mSubs = CompositeSubscription()
    val mContentView: View by bindView(R.id.fullscreen)
    val meter: MisMeter by bindView(R.id.meter)
    val meter2: MisMeter by bindView(R.id.meter2)
    val meter3: MisMeter by bindView(R.id.meter3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        val handler = Handler()

        mSubs.add(Observable.interval(1, TimeUnit.SECONDS).subscribe({ l ->
            handler.post {
                animate(meter)
                animate(meter2)
                animate(meter3)
            }
        }))
    }

    override fun onResume() {
        super.onResume()

        mContentView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onDestroy() {
        super.onDestroy()

        mSubs.unsubscribe()
    }

    private fun animate(meter: MisMeter) {
        meter.animate(Random().nextFloat() % 0.5f + 0.3f)
    }
}
