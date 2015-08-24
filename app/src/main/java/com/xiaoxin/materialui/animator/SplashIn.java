package com.xiaoxin.materialui.animator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.xiaoxin.materialui.evaluator.TRectFEvaluator;
import com.xiaoxin.materialui.interpolator.QuintInOut;

/**
 * 
  * @ClassName: SplashIn
  * @Description: splash圈圈动画
  * @author xiaoxin wangrenxing@1872.net
  * @date 2015年8月24日 上午9:02:44
  *
 */
public class SplashIn {
	private int backgroundColor;
	private List<CircledDrawable> circles = new ArrayList<CircledDrawable>();
	ScaleSuper scaleSuper;
	private int splashColor;
	private int startDelay;
	private View view;

	public SplashIn(View view, int splashColor, int bgColor) {
		this.view = view;
		this.splashColor = splashColor;
		this.backgroundColor = bgColor;
	}

	public void draw(Canvas canvas) {
		Iterator<CircledDrawable> it = circles.iterator();
		while( it.hasNext() ) {
			((CircledDrawable) it.next()).draw(canvas);
		}
	}

	public float getCurrentScale() {
		if( scaleSuper == null ) {
			return 1.0f;
		}
		return scaleSuper.currentScale;
	}

	public int getStartDelay() {
		return startDelay;
	}

	public void initAnims() {
		RectF rc1 = new RectF(0.0F, 0.0f, view.getHeight(), view.getHeight());
		RectF rc2 = new RectF(-1.0F, -1.0f, view.getHeight() + 1, view.getHeight() + 1);
		circles.add(new CircleColorExpand(rc1, startDelay, 1200L, splashColor));
		circles.add(new CircleColorExpand(rc2, startDelay + 80, 1200L, backgroundColor));
		Iterator<CircledDrawable> it = circles.iterator();
		while (it.hasNext()) {
			((CircledDrawable) it.next()).startAnim();
		}
		scaleSuper = new ScaleSuper(startDelay + 200, 1250L);
		scaleSuper.startAnim();
	}

	public void setStartDelay(int startDelay) {
		this.startDelay = startDelay;
	}

	public class CircleColorExpand implements CircledDrawable {
		long animDuration;
		RectF drawingRect;
		private Paint paint = new Paint();
		long startDelay;
		RectF targetSize;

		public CircleColorExpand(RectF targetSize, long startDelay, long animDuration, int color) {
			this.targetSize = targetSize;
			this.startDelay = startDelay;
			this.animDuration = animDuration;
			this.paint.setColor(color);
			this.paint.setAntiAlias(true);
			this.paint.setDither(true);
		}
		
		public void draw(Canvas canvas) {
			if (drawingRect != null) {
				canvas.drawOval(drawingRect, paint);
			}
		}

		public void startAnim() {
			RectF rc = new RectF(targetSize.centerX(), targetSize.centerY(), 
								 targetSize.centerX(), targetSize.centerY());
			TRectFEvaluator evaluator = new TRectFEvaluator();
			Object[] values = new Object[2];
			values[0] = rc;
			values[1] = targetSize;
			ValueAnimator va = ValueAnimator.ofObject(evaluator, values);
			va.setDuration(animDuration);
			va.setStartDelay(startDelay);
			va.setInterpolator(new QuintInOut());
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							drawingRect =((RectF) valueAnimator.getAnimatedValue());
							view.invalidate();
						}});
			va.start();
		}
	}

	static abstract interface CircledDrawable {
		public abstract void draw(Canvas paramCanvas);
		public abstract void startAnim();
	}

	private class ScaleSuper {
		long animDuration;
		float currentScale = 0.0F;
		long startDelay;

		public ScaleSuper(long startDelay, long animDuration) {
			this.startDelay = startDelay;
			this.animDuration = animDuration;
		}

		public void startAnim() {
			ValueAnimator va = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0f });
			va.setDuration(animDuration);
			va.setInterpolator(new QuintInOut());
			va.setStartDelay(startDelay);
			va.addUpdateListener(
					new ValueAnimator.AnimatorUpdateListener() {
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							currentScale = ((Float) valueAnimator.getAnimatedValue())
									                             .floatValue();
							view.invalidate();
						}
					});
			va.start();
		}
	}
}