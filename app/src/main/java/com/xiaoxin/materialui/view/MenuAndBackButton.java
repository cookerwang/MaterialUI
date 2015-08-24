package com.xiaoxin.materialui.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.xiaoxin.materialui.evaluator.PointFEvaluator;
import com.xiaoxin.materialui.interpolator.QuintInOut;

/**
  * 菜单<->返回按钮 
  * @ClassName: MenuAndBackButton
  * @Description: TODO
  * @author xiaoxin wangrenxing@1872.net
  * @date 2015年8月21日 上午8:54:54
  *
 */
public class MenuAndBackButton extends View {
	public static final int DURATION = 500;
	float density;
	boolean isInArrowState = false;
	Paint linePaint = new Paint();
	// 菜单与返回都由 三个线段组成
	/**
	 *         _____            /
	 *         _____           /________ 
	 *         _____           \
	 *                          \
	 */
	PointF one1 = new PointF();
	PointF one2 = new PointF();
	PointF two1 = new PointF();
	PointF two2 = new PointF();
	PointF three1 = new PointF();
	PointF three2 = new PointF();

	public MenuAndBackButton(Context context) {
		this(context, null);
	}

	public MenuAndBackButton(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public MenuAndBackButton(Context context, AttributeSet attributeSet, int defStyleAttr) {
		super(context, attributeSet, defStyleAttr);
		init();
	}

	private void init() {
		this.linePaint.setColor(Color.WHITE);
		this.density = getResources().getDisplayMetrics().density;
		this.linePaint.setStrokeWidth(1.3F * this.density);
		this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		this.linePaint.setAntiAlias(true);
		this.linePaint.setDither(true);
		this.one1 = new PointF(density(13.0F), density(17.0F));
		this.one2 = new PointF(density(38.0F), density(17.0F));
		
		this.two1 = new PointF(density(13.0F), density(24.0F));
		this.two2 = new PointF(density(38.0F), density(24.0F));
		
		this.three1 = new PointF(density(13.0F), density(31.0F));
		this.three2 = new PointF(density(38.0F), density(31.0F));
	}
	
	private float density(float dp) {
		return (dp * this.density);
	}

	private void gotToPositions(int startDelay, TimeInterpolator interpolator, 
			final PointF pointTarget, PointF[] values) {
		ValueAnimator va = ValueAnimator.ofObject(new PointFEvaluator(), values);
		va.setStartDelay(startDelay);
		va.setInterpolator(interpolator);
		va.setDuration(500L);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				pointTarget.x = ((PointF) valueAnimator.getAnimatedValue()).x;
				pointTarget.y = ((PointF) valueAnimator.getAnimatedValue()).y;
				MenuAndBackButton.this.invalidate();
			}
		});
		va.start();
	}

	public void fadeToGray() {
		ArgbEvaluator argbEvaluator = new ArgbEvaluator();
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = Integer.valueOf(linePaint.getColor());
		arrayOfObject[1] = Integer.valueOf(Color.GRAY);
		ObjectAnimator localObjectAnimator = ObjectAnimator.ofObject(
				linePaint, "Color", argbEvaluator, arrayOfObject);
		localObjectAnimator.addUpdateListener(getUpdaListener());
		localObjectAnimator.setDuration(250L).start();
	}

	public void fadeToWhite() {		
		ArgbEvaluator localArgbEvaluator = new ArgbEvaluator();
		Object[] values = new Object[2];
		values[0] = Integer.valueOf(linePaint.getColor());
		values[1] = Integer.valueOf(Color.WHITE);
		ObjectAnimator objectAnimator = ObjectAnimator.ofObject(
				linePaint, "Color", localArgbEvaluator, values);
		objectAnimator.addUpdateListener(getUpdaListener());
		objectAnimator.setDuration(250L).start();
	}

	public ValueAnimator.AnimatorUpdateListener getUpdaListener() {
		return new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				MenuAndBackButton.this.invalidate();
			}
		};
	}

	public void goToArrow() {
		if (!(this.isInArrowState)) {
			this.isInArrowState = true;
			AccelerateInterpolator interpolator = null;
			PointF[] values = null;
			
			// 第一条直线
			interpolator = new AccelerateInterpolator(1.9F);
			values = new PointF[2];
			values[0] = this.one1;
			values[1] = new PointF(density(28.0F), density(15.0F));
			gotToPositions(0, interpolator, one1, values);
			
			interpolator = new AccelerateInterpolator(1.9F);
			values = new PointF[2];
			values[0] = this.one2;
			values[1] = new PointF(density(38.0F), density(24.0F));
			gotToPositions(0, interpolator, one2, values);
			
			// 第二条直线
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.two1;
			values[1] = new PointF(density(13.0F), density(24.0F));
			gotToPositions(0, interpolator, two1, values);
			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.two2;
			values[1] = new PointF(density(38.0F), density(24.0F));
			gotToPositions(0, interpolator, two2, values);
			
			// 第三条直线			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.three1;
			values[1] = new PointF(density(28.0F), density(33.0F));
			gotToPositions(0, interpolator, three1, values);
			
			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.three2;
			values[1] = new PointF(density(38.0F), density(24.0F));
			gotToPositions(0, interpolator, three2, values);
			
			ObjectAnimator oa = ObjectAnimator.ofFloat(this, View.ROTATION, new float[] { 0.0F, 180.0F })
					                          .setDuration(DURATION);					                          
			oa.setInterpolator(new QuintInOut());
			oa.start();
		}
	}

	public void goToMenu() {
		if (this.isInArrowState) {
			this.isInArrowState = false;
			
			AccelerateInterpolator interpolator = null;
			PointF[] values = null;
			
			// 第一条直线
			interpolator = new AccelerateInterpolator(1.9F);
			values = new PointF[2];
			values[0] = this.one1;
			values[1] = new PointF(density(13.0F), density(17.0F));
			gotToPositions(0, interpolator, one1, values);
			
			interpolator = new AccelerateInterpolator(1.9F);
			values = new PointF[2];
			values[0] = this.one2;
			values[1] = new PointF(density(38.0F), density(17.0F));
			gotToPositions(0, interpolator, one2, values);
			
			// 第二条直线
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.two1;
			values[1] = new PointF(density(13.0F), density(24.0F));
			gotToPositions(0, interpolator, two1, values);
			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.two2;
			values[1] = new PointF(density(38.0F), density(24.0F));
			gotToPositions(0, interpolator, two2, values);
			
			// 第三条直线			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.three1;
			values[1] = new PointF(density(13.0F), density(31.0F));
			gotToPositions(0, interpolator, three1, values);
			
			
			interpolator = new AccelerateInterpolator(1.9F);			
			values = new PointF[2];
			values[0] = this.three2;
			values[1] = new PointF(density(38.0F), density(31.0F));
			gotToPositions(0, interpolator, three2, values);
						
			float[] rValues = new float[2];
			rValues[0] = getRotation();
			rValues[1] = 0.0F;
			ObjectAnimator oa = ObjectAnimator.ofFloat(this, View.ROTATION, rValues)
					                                           .setDuration(DURATION);
			oa.setInterpolator(new QuintInOut());
			oa.start();
		}
	}

	public boolean isInBackState() {
		return isInArrowState;
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(this.one1.x, this.one1.y, this.one2.x, this.one2.y, this.linePaint);
		canvas.drawLine(this.two1.x, this.two1.y, this.two2.x, this.two2.y, this.linePaint);
		canvas.drawLine(this.three1.x, this.three1.y, this.three2.x, this.three2.y, this.linePaint);
	}
}