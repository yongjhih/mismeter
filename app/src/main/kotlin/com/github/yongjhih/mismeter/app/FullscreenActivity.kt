package com.github.yongjhih.mismeter.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.view.View

import java.util.Random
import java.util.concurrent.TimeUnit

import rx.Observable
import rx.subscriptions.CompositeSubscription

import com.github.yongjhih.mismeter.*
import kotterknife.bindView
import com.larswerkman.lobsterpicker.OnColorListener
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

// RxJava2, RxLifecycle instead?
class FullscreenActivity : AppCompatActivity() {
    val mSubs = CompositeSubscription()
    val mContentView: View by bindView(R.id.fullscreen)
    val meter: MisMeter by bindView(R.id.meter)
    val meter2: MisMeter by bindView(R.id.meter2)
    val meter3: MisMeter by bindView(R.id.meter3)
    val progress: DiscreteSeekBar by bindView(R.id.progress)
    val colors: LobsterShadeSlider by bindView(R.id.colors)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        val handler = Handler()

        mSubs.add(Observable.interval(1, TimeUnit.SECONDS).subscribe({ l ->
            handler.post {
                animate(meter2)
                animate(meter3)
            }
        }))

        progress.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                // nothing
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                // nothing
            }

            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                meter.setProgress(value / 100f)
            }
        })

        colors.addOnColorListener(object: OnColorListener {
            override fun onColorSelected(color: Int) {
                // nothing
            }

            override fun onColorChanged(@ColorInt color: Int) {
                meter.setTextColor(color)
                meter.setProgress(meter.progress)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        mContentView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onDestroy() {
        super.onDestroy()

        mSubs.unsubscribe()
    }

    private fun animate(meter: MisMeter) {
        meter.animate(Random().nextFloat() % 0.5f + 0.3f)
    }
}
