package com.xiaoxin.materialui.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description:
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午15:40:12
 *
 */
public class PageIndicator extends View {
	public static final int DURATION = 5000;
	List<ValueAnimator> animations = new ArrayList<ValueAnimator>();
	private List<PointF> stopPoints = new ArrayList<PointF>();
	float backCircleRadius;
	float backCircleX;
	float frontCircleRadius;
	float frontCircleX;
	private List<Integer> colors;
	private Paint paint = new Paint();
	Path path = new Path();

	public PageIndicator(Context context) {
		super(context);
		init();
	}

	public PageIndicator(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public PageIndicator(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		init();
	}
	
	private void init() {
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL);
	}

	private void createAnimations(int index) {
		final int nextIndex = Math.min(colors.size() - 1, index + 1);
		float[] values = null;
		ValueAnimator va = null;
		
		values = new float[2];
		values[0] = ((PointF) stopPoints.get(index)).x;
		values[1] = ((PointF) stopPoints.get(nextIndex)).x;
		va = ObjectAnimator.ofFloat(values).setDuration(DURATION);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				frontCircleX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
			}
		});
		va.setInterpolator(new DecelerateInterpolator());
		animations.add(va);
		
		values = new float[2];
		values[0] = ((PointF) stopPoints.get(index)).x;
		values[1] = ((PointF) stopPoints.get(nextIndex)).x;
		va = ObjectAnimator.ofFloat(values).setDuration(DURATION);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				backCircleX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
			}
		});
		va.setInterpolator(new AccelerateInterpolator(1.8F));		
		animations.add(va);
		
		float rStart = 0.12F * getHeight();
		float rEnd = 0.42F * getHeight();
		va = ObjectAnimator.ofFloat(new float[] { rStart, rEnd }).setDuration(DURATION);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				frontCircleRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
			}
		});
		va.setInterpolator(new AccelerateInterpolator(1.8F));
		animations.add(va);
		
		va = ObjectAnimator.ofFloat(new float[] { rEnd, rStart }).setDuration(DURATION);
		va.setInterpolator(new DecelerateInterpolator(0.8F));
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
				backCircleRadius = ((Float) paramValueAnimator.getAnimatedValue()).floatValue();
			}
		});
		animations.add(va);
		
		
		Object[] objValues = new Object[2];
		objValues[0] = this.colors.get(index);
		objValues[1] = this.colors.get(nextIndex);
		va = ObjectAnimator.ofObject(new ArgbEvaluator(), objValues).setDuration(DURATION);
		va.setInterpolator(new AccelerateDecelerateInterpolator());
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {				
				paint.setColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
			}
		});
		animations.add(va);
	}

	private void drawPathBetweenCircles(Canvas canvas, float extraYOffsetBasedOnDistance) {
		path.reset();
		path.moveTo(backCircleX, getHeight() / 2);
		path.lineTo(backCircleX, getHeight() / 2 - backCircleRadius);
		path.cubicTo(backCircleX + 0.4F * backCircleRadius, 
					getHeight() / 2 - backCircleRadius,
					backCircleX + (frontCircleX - backCircleX) / 2.0F, 
					extraYOffsetBasedOnDistance + getHeight() / 2,
					frontCircleX, getHeight() / 2 - frontCircleRadius);
		
		path.lineTo(frontCircleX, getHeight() / 2 + frontCircleRadius);
		path.cubicTo(frontCircleX, getHeight() / 2 + frontCircleRadius, 
					backCircleX + (frontCircleX - backCircleX) / 2.0F, 
					getHeight() / 2 - extraYOffsetBasedOnDistance, 
					backCircleX + 0.25F * backCircleRadius, 
					getHeight() / 2 + backCircleRadius);
		path.close();
		canvas.drawPath(path, paint);
	}

	private float getExtraYOffsetBasedOnDistance() {
		return ((frontCircleX - backCircleX) / 
				(((PointF) stopPoints.get(1)).x - ((PointF) stopPoints.get(0)).x) * getHeight() / 5);
	}

	

	private void seekAnimations(float percent) {
	    Iterator<ValueAnimator> it = animations.iterator();
	    while (it.hasNext()) {
	      ((ValueAnimator)it.next()).setCurrentPlayTime((long)(DURATION * percent));
	    }
	    invalidate();
  }

	public void addStopPoint(PointF point) {
		stopPoints.add(point);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!(isInEditMode())) {
			canvas.drawCircle(frontCircleX, getHeight() / 2, frontCircleRadius, paint);
			canvas.drawCircle(backCircleX, getHeight() / 2, backCircleRadius, paint);
			drawPathBetweenCircles(canvas, getExtraYOffsetBasedOnDistance());
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			float[] values = new float[2];
			values[0] = 0.0F;
			values[1] = (0.42F * getHeight());
			ValueAnimator va = ObjectAnimator.ofFloat(values).setDuration(500L);
			va.setInterpolator(new OvershootInterpolator());
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					backCircleRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
					invalidate();
				}
			});
			va.setStartDelay(700L);
			va.start();
			post(new Runnable() {
				public void run() {
					backCircleRadius = 0.0F;
					frontCircleRadius = 0.0F;
					invalidate();
				}
			});
		}
	}

	// 从index --> index + 1， 0.0f<-->index, 1.0f<-->index+1
	public void seekJelly(int index, float percent) {
		if (stopPoints.size() != 0) {
			animations.clear();
			createAnimations(index);
			seekAnimations(percent);
		}
	}

	public void setColors(List<Integer> colors) {
		this.colors = colors;
	}
}