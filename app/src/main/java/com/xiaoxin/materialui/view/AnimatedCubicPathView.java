package com.xiaoxin.materialui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.xiaoxin.materialui.R;
import com.xiaoxin.materialui.interpolator.QuintInOut;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 贝塞尔曲线动画
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午8:30:12
 *
 */
public class AnimatedCubicPathView extends View {
	public static final int ANIM_DURATION = 1200;
	public static final float CIRCLE_RADIUS = 6.0F;
	public static final float LINE_HEIGHT = 1.0f;
	public static final int NUMBER_OF_WAVES = 9;
	public static final int START_DELAY = 400;
	private float circleRadius = 0.0F;
	private int color = Color.RED;
	private boolean isRunning = false;
	private float[] leftCircleCoordinates = new float[2];
	private float lineHeight;
	private float linePadding = 0.0f;
	private Paint linePaint = new Paint();
	private Path path = new Path();
	private Paint pathPaint = new Paint();
	private float pathWaveY = 0.0F;
	private float percentOffset = 0.0F;
	private float[] rightCircleCoordinates = new float[2];
	private long startDelay = 400L;

	public AnimatedCubicPathView(Context context) {
		super(context);
	}

	public AnimatedCubicPathView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init(attributeSet);
	}

	public AnimatedCubicPathView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		init(attributeSet);
	}

	private void init(AttributeSet attributeSet) {
		TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.AnimatedPathView);
		startDelay = a.getInteger(R.styleable.AnimatedPathView_start_delay, START_DELAY);
		color = a.getColor(R.styleable.AnimatedPathView_path_color, Color.TRANSPARENT);
		a.recycle();
	}
	
	private void animateCircles() {		
		final float maxCircleRadius = CIRCLE_RADIUS * getResources().getDisplayMetrics().density;
		circleRadius = maxCircleRadius;
		ValueAnimator va = ValueAnimator.ofFloat(new float[] { 0.0f, 0.5f }).setDuration(ANIM_DURATION);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.setStartDelay(startDelay);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				percentOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
				circleRadius = (maxCircleRadius * 2.0f * (0.5f - percentOffset) + lineHeight);
			}
		});
		va.start();
	}

	private void animatePathWaveY() {
		float[] values = new float[2];
		values[0] = pathWaveY;
		values[1] = 0.0f;
		ValueAnimator va = ValueAnimator.ofFloat(values).setDuration(ANIM_DURATION);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.setStartDelay(startDelay);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				pathWaveY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
			}
		});
		va.start();
	}

	private void animatePathWidth() {
		float[] values = new float[2];
		values[0] = linePadding;
		values[1] = 0.0F;
		ValueAnimator va = ValueAnimator.ofFloat(values).setDuration(ANIM_DURATION);
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(startDelay);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				linePadding = ((Float) valueAnimator.getAnimatedValue()).floatValue();
				createPath();
				invalidate();
			}
		});
		va.start();
	}

	private void createPath() {
	    int h = getHeight() / 2;
	    path.reset();
	    path.moveTo(linePadding, getHeight() / 2);
	    int waveLength = (int)((getWidth() - (2.0f * linePadding)) / NUMBER_OF_WAVES);	       
	    for( int i=0; i<NUMBER_OF_WAVES; i++ ) {
	    	if( (i & 0x01) == 0 ) {
	    		path.cubicTo(linePadding + waveLength * i,
	    			     h, 
	    				 linePadding + waveLength * i + waveLength / 2, 
	    				 h + pathWaveY, linePadding + waveLength * i + waveLength, h);
	    	} else {
	    		path.cubicTo(linePadding + waveLength * i,
	    			     h, 
	    				 linePadding + waveLength * i + waveLength / 2, 
	    				 h - pathWaveY, linePadding + waveLength * i + waveLength, h);
	    	}
	    }
	    PathMeasure pathMeasure = new PathMeasure(path, false);
	    pathMeasure.getPosTan(pathMeasure.getLength() * (0.5f - percentOffset), leftCircleCoordinates, null);
	    pathMeasure.getPosTan(pathMeasure.getLength() * (0.5f + percentOffset), rightCircleCoordinates, null);	    
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if( !isRunning ) {
			initLinePaint();
			isRunning = true;
			linePadding = (getWidth() / 6);
			pathWaveY = (getHeight() / 2);
			createPath();
			animateCircles();
			animatePathWaveY();
			animatePathWidth();
		}
	}
	
	private void initLinePaint() {
		linePaint.setColor(color);
		linePaint.setAntiAlias(true);
		linePaint.setDither(true);
		pathPaint.setColor(color);
		pathPaint.setAntiAlias(true);
		pathPaint.setDither(true);
		pathPaint.setStyle(Paint.Style.STROKE);
		lineHeight = (LINE_HEIGHT * getResources().getDisplayMetrics().density);
		pathPaint.setStrokeWidth(lineHeight);
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(leftCircleCoordinates[0], leftCircleCoordinates[1], circleRadius, linePaint);
		canvas.drawCircle(rightCircleCoordinates[0], rightCircleCoordinates[1], circleRadius, linePaint);
		canvas.drawPath(path, pathPaint);
	}

	public void setColor(int color) {
		linePaint.setColor(color);
	}

	public void setStartDelay(long startDelay) {
		this.startDelay = startDelay;
	}
}