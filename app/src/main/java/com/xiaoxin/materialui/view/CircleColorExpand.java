package com.xiaoxin.materialui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.xiaoxin.materialui.evaluator.TRectFEvaluator;
import com.xiaoxin.materialui.interpolator.QuintInOut;

/**
 *
 * @ClassName: AnimatedPathView
 * @Description:
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午11:40:12
 *
 */
public class CircleColorExpand {
	private long animDuration;
	RectF drawingRect;
	private TimeInterpolator interpolator;
	boolean isActive = true;
	boolean killAfterAnim = false;
	float offsetY;
	private Paint paint = new Paint();
	private long startDelay;
	private RectF targetSize;

	public CircleColorExpand(RectF targetSize, long startDelay, long animDuration, int color) {
		this.targetSize = targetSize;
		this.startDelay = startDelay;
		this.animDuration = animDuration;
		this.paint.setColor(color);
		this.paint.setAntiAlias(true);
		this.paint.setDither(true);		
	}

	private void animateSize(boolean reversed) {
		ValueAnimator animator;
		this.drawingRect = getStartRect();
		
		TRectFEvaluator trectFEvaluator = new TRectFEvaluator();
		Object[] values = new Object[2];
		values[0] = this.drawingRect;
		if (reversed) {
			values[1] = new RectF(this.drawingRect.centerX(), this.drawingRect.centerY(), 
					this.drawingRect.centerX(), this.drawingRect.centerY());
		} else {
			values[1] = this.targetSize;
		}
		animator = ValueAnimator.ofObject(trectFEvaluator, values);
		animator.setDuration(this.animDuration);
		if (this.interpolator == null) {
			this.interpolator = new QuintInOut();
		}
		animator.setInterpolator(this.interpolator);
		animator.setStartDelay(this.startDelay);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						CircleColorExpand.this.drawingRect = ((RectF) valueAnimator.getAnimatedValue());
					}
				});
		if (this.killAfterAnim) {
			animator.addListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator paramAnimator) {
					super.onAnimationEnd(paramAnimator);
					CircleColorExpand.this.isActive = false;
				}
			});
		}
		animator.start();			
	}

	private RectF getStartRect() {
		if (this.drawingRect == null) {
			return new RectF(this.targetSize.centerX(), this.targetSize.centerY(), 
					this.targetSize.centerX(), this.targetSize.centerY());
		}
		return this.drawingRect;
	}

	public void animateYTo(float newOffsetY) {
		float[] values = new float[2];
		values[0] = this.offsetY;
		values[1] = newOffsetY;
		ValueAnimator valueAnimator = ValueAnimator.ofFloat(values);
		valueAnimator.setDuration(500L);
		valueAnimator.setInterpolator(new OvershootInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						offsetY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
					}
				});
		valueAnimator.start();
	}

	public void draw(Canvas canvas) {
		if ((this.drawingRect != null) && (this.isActive)) {
			canvas.save();
			canvas.translate(0.0F, offsetY);
			canvas.drawOval(drawingRect, paint);
			canvas.restore();
		}
	}

	public CircleColorExpand fadeOut() {
		ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { 255, 0 });
		localValueAnimator.setDuration(this.animDuration);
		localValueAnimator.setInterpolator(new AccelerateInterpolator(1.6F));
		localValueAnimator.setStartDelay(this.startDelay);
		localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						paint.setAlpha(((Integer)valueAnimator.getAnimatedValue()).intValue());
					}
				});
		localValueAnimator.start();
		return this;
	}

	public float getCenterX() {
		return this.drawingRect.centerX();
	}

	public float getCenterY() {
		return this.drawingRect.centerY();
	}

	public CircleColorExpand killAfterAnim() {
		this.killAfterAnim = true;
		return this;
	}

	public void setAnimDuration(long animDuration) {
		this.animDuration = animDuration;
	}

	public CircleColorExpand setInterpolator(TimeInterpolator interpolator) {
		this.interpolator = interpolator;
		return this;
	}

	public CircleColorExpand setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public CircleColorExpand setStartRect(RectF drawingRect) {
		this.drawingRect = drawingRect;
		return this;
	}

	public CircleColorExpand startAnim() {
		animateSize(false);
		return this;
	}

	public void startAnimReversed() {
		animateSize(true);
	}
}