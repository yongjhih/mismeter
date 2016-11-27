package com.github.yongjhih.mismeter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

/**
 * Without other unnessary resources such like layout xml and drawable img.
 */
public class MisMeter extends View {
    private final static int DEFAULT_PADDING = 80;

    private final static float START_ANGLE = 130f; // TODO auto-adjust

    private final static float END_ANGLE = 280f; // TODO

    private int defaultSize; // TODO

    private int arcDistance; // ArcPadding

    private int width;

    private int height;

    private Paint mMiddleArcPaint;
    private Paint mWarningMiddleArcPaint;

    private Paint mOuterArcPaint;
    private Paint mTextPaint;
    private Paint mCurrentTextPaint;

    private Paint mArcProgressPaint;

    private float radius;

    private RectF mMiddleRect;

    private RectF mOuterRect;

    private RectF mMiddleProgressRect;

    private int mMinNum = 0;

    private int mMaxNum = 100;

    private int mCurrentNum = 0;

    private float mCurrentAngle = 0f;

    private float mMaxAngle = 280f; // TODO

    private Bitmap bitmap;

    private float[] pos;

    private float[] tan;

    private Matrix matrix;

    private Paint mBitmapPaint;

    private Paint mNeedle;

    public float progress = 0f; // TODO private

    private boolean mShowText = true;

    private String mFont = "sans-serif-condensed";
    private int mTextColor = 0x99ffffff;
    private boolean mShowStartEndText = false;

    public MisMeter(@NonNull Context context) {
        this(context, null);
    }

    public MisMeter(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MisMeter(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context.obtainStyledAttributes(attrs, R.styleable.MisMeter));
    }

    private void init(TypedArray attrs) {
        mShowText = attrs.getBoolean(R.styleable.MisMeter_show_text, mShowText);
        mFont = attrs.getString(R.styleable.MisMeter_font);
        mTextColor = attrs.getColor(R.styleable.MisMeter_text_color, mTextColor);
        mShowStartEndText = attrs.getBoolean(R.styleable.MisMeter_show_start_end_text, mShowStartEndText);
        mMaxNum = attrs.getInt(R.styleable.MisMeter_max, mMaxNum);
        mMinNum = attrs.getInt(R.styleable.MisMeter_min, mMinNum);

        defaultSize = dp2px(250);
        arcDistance = dp2px(12);

        mMiddleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleArcPaint.setStrokeWidth(18);
        mMiddleArcPaint.setColor(Color.WHITE);
        mMiddleArcPaint.setStyle(Paint.Style.STROKE);
        mMiddleArcPaint.setAlpha(30);

        mOuterArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterArcPaint.setStrokeWidth(3);
        mOuterArcPaint.setColor(Color.WHITE);
        mOuterArcPaint.setStyle(Paint.Style.STROKE);
        mOuterArcPaint.setAlpha(230);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create(mFont, Typeface.NORMAL));

        mCurrentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrentTextPaint.setColor(Color.WHITE);
        mCurrentTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentTextPaint.setTypeface(Typeface.create(mFont, Typeface.NORMAL));

        mArcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcProgressPaint.setStrokeWidth(18);
        mArcProgressPaint.setColor(Color.WHITE);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcProgressPaint.setAlpha(150);

        mNeedle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedle.setStrokeWidth(18);
        mNeedle.setColor(Color.WHITE);
        mNeedle.setStyle(Paint.Style.STROKE);
        mNeedle.setStrokeCap(Paint.Cap.ROUND);

        //mNeedle.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));

        mBitmapPaint = new Paint();
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_circle); // TODO
        pos = new float[2];
        tan = new float[2];
        matrix = new Matrix();
        mWarningMiddleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWarningMiddleArcPaint.setStrokeWidth(18);
        // TODO Allow color parameter
        mWarningMiddleArcPaint.setColor(Color.parseColor("#eef3797b"));
        mWarningMiddleArcPaint.setStyle(Paint.Style.STROKE);

        attrs.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, defaultSize),
                resolveMeasure(heightMeasureSpec, defaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
        radius = width / 2;

        mOuterRect = new RectF(
                DEFAULT_PADDING, DEFAULT_PADDING,
                width - DEFAULT_PADDING, height - DEFAULT_PADDING);

        mMiddleRect = new RectF(
                DEFAULT_PADDING + arcDistance, DEFAULT_PADDING + arcDistance,
                width - DEFAULT_PADDING - arcDistance, height - DEFAULT_PADDING - arcDistance);

        mMiddleProgressRect = new RectF(
                DEFAULT_PADDING + arcDistance, DEFAULT_PADDING + arcDistance,
                width - DEFAULT_PADDING - arcDistance, height - DEFAULT_PADDING - arcDistance);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawMiddleArc(canvas);
        drawRingProgress(canvas);
        drawCenterText(canvas);
    }

    /**
     * @param canvas
     */
    private void drawRingProgress(@NonNull Canvas canvas) {
        Path path = new Path();
        path.addArc(mMiddleProgressRect, START_ANGLE, mCurrentAngle);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength() * 1, pos, tan);
        matrix.reset();
        matrix.postTranslate(pos[0] - bitmap.getWidth() / 2, pos[1] - bitmap.getHeight() / 2);
        //canvas.drawPath(path, mArcProgressPaint); // TODO
        if (mCurrentAngle == 0) return;
        canvas.drawBitmap(bitmap, matrix, mBitmapPaint);
        mBitmapPaint.setColor(Color.WHITE);
        mBitmapPaint.setAlpha(200);
        canvas.drawCircle(pos[0], pos[1], 8, mBitmapPaint);
    }

    /**
     * @param canvas
     */
    private void drawCenterText(@NonNull Canvas canvas) {
        // TODO measureText for aligning


        if (mShowStartEndText) {
            mTextPaint.setTextSize(dp2px(12)); // FIXME
            mTextPaint.setStyle(Paint.Style.STROKE);
            canvas.drawText(String.valueOf(mMinNum), dp2px(60), height - dp2px(38), mTextPaint); // FIXME
            //mTextPaint.setTextSize(dp2px(12)); // FIXME
            canvas.drawText(String.valueOf(mMaxNum), width - dp2px(65), height - dp2px(38), mTextPaint); // FIXME
        }

        if (mShowText) {
            mCurrentTextPaint.setTextSize(dp2px(38)); // FIXME
            canvas.drawText(String.valueOf(mCurrentNum), radius, height - arcDistance, mCurrentTextPaint);
        }
    }

    /**
     * @param canvas
     */
    private void drawMiddleArc(@NonNull Canvas canvas) {
        float r = (radius - DEFAULT_PADDING) - arcDistance - arcDistance;
        float toX = width / 2 + (float) Math.cos(Math.toRadians(mCurrentAngle + START_ANGLE)) * (r);
        float toY = width / 2 + (float) Math.sin(Math.toRadians(mCurrentAngle + START_ANGLE)) * (r);
        int centerX = width / 2;
        int centerY = height / 2;
        int margin = 0;
        canvas.drawLine(centerX, centerY + margin, toX, toY, mNeedle);

        //canvas.drawArc(mOuterRect, START_ANGLE, END_ANGLE, false, mOuterArcPaint);
        canvas.drawArc(mMiddleRect, START_ANGLE, END_ANGLE, false, mMiddleArcPaint);
        // TODO Allow wraning range parameters
        //canvas.drawArc(mMiddleRect, START_ANGLE + 35, END_ANGLE - 70, false, mMiddleArcPaint);
        //canvas.drawArc(mMiddleRect, START_ANGLE, 35, false, mWarningMiddleArcPaint);
        //canvas.drawArc(mMiddleRect, END_ANGLE + 95, 35, false, mWarningMiddleArcPaint);
    }

    /**
     * @param measureSpec
     * @param defaultSize
     */
    public int resolveMeasure(int measureSpec, int defaultSize) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, defaultSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                result = defaultSize;
        }

        return result;
    }

    public void setProgress(@FloatRange(from = 0.0f, to=1.0f) float progress) {
        this.progress = progress;
        mCurrentNum = (int) ((progress * (mMaxNum - mMinNum)) + mMinNum);
        mCurrentAngle = mMaxAngle * progress;
        postInvalidate();
    }

    /**
     * dp2px
     *
     * @param values
     * @return
     */
    public int dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    public void animate(@FloatRange(from = 0.0f, to=1.0f) float progress) {
        ValueAnimator anim = ValueAnimator.ofFloat(this.progress, progress);
        if (this.progress > progress) {
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(750);
        } else {
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(500);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MisMeter.this.setProgress((float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
    }

    public void setTextColor(@ColorInt int color) {
        mCurrentTextPaint.setColor(color);
    }
}
