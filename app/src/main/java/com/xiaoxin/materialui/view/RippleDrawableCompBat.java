package com.xiaoxin.materialui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.xiaoxin.materialui.property.FloatProperty;

/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 分割线显示动画，线粗1dp
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午16:40:12
 *
 */
public class RippleDrawableCompBat extends Drawable implements
		View.OnTouchListener {
	
	static final Property<RippleDrawableCompBat, Float> CREATE_TOUCH_RIPPLE = new FloatProperty<RippleDrawableCompBat>(
			"mAnimationValue") {
		public Float get(RippleDrawableCompBat rippleDrawableCompBat) {
			return Float.valueOf(rippleDrawableCompBat.getAnimationState());
		}

		public void setValue(RippleDrawableCompBat rippleDrawableCompBat, float value) {
			rippleDrawableCompBat.createTouchRipple(value, true);
		}
	};
	
	static final int DEFAULT_ANIM_DURATION = 450;
	static final float END_SCALE = 1.3F;
	static final int RIPPLE_TOUCH_MAX_ALPHA = 70;
	static final int RIPPLE_TOUCH_MIN_ALPHA = 20;
	
	public static final int START_ANIM_DURATION = 3000;
	
	private boolean eminateFromCenter;
	float mAnimationValue;
	ObjectAnimator mCurrentAnimator;
	Drawable mOriginalBackground;
	Paint mRippleBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	Circle mTouchRipple;
	
	int mViewSize = 0;

	public RippleDrawableCompBat() {
		initRippleElements();
	}

	@SuppressLint("NewApi")
	public static void createCenterRipple(View view, int color) {
		RippleDrawableCompBat rippleDrawableCompBat = new RippleDrawableCompBat();
		rippleDrawableCompBat.setDrawable(view.getBackground());
		rippleDrawableCompBat.setColor(color);
		rippleDrawableCompBat.setEminateFromCenter(true);
		rippleDrawableCompBat.setBounds(view.getPaddingLeft(),
				view.getPaddingTop(), view.getPaddingRight(),
				view.getPaddingBottom());
		view.setOnTouchListener(rippleDrawableCompBat);
		if (Build.VERSION.SDK_INT >= 16) {
			view.setBackground(rippleDrawableCompBat);
			return;
		}
		view.setBackgroundDrawable(rippleDrawableCompBat);
	}

	public static void createRipple(int cx, int cy, View view, int color) {
		if (!(view.getBackground() instanceof RippleDrawableCompBat)) {
			createRipple(view, color);
		}
		RippleDrawableCompBat rippleDrawableCompBat = (RippleDrawableCompBat) view.getBackground();
		rippleDrawableCompBat.setColor(color);
		rippleDrawableCompBat.onFingerDown(view, cx, cy);
	}

	
	@SuppressLint("NewApi")
	public static void createRipple(View view, int color) {
		if (Build.VERSION.SDK_INT >= 21) {
			createStockRipple(view, color);
			return;
		}
		RippleDrawableCompBat rippleDrawableCompBat = new RippleDrawableCompBat();
		rippleDrawableCompBat.setDrawable(view.getBackground());
		rippleDrawableCompBat.setColor(color);
		rippleDrawableCompBat.setBounds(view.getPaddingLeft(),
				view.getPaddingTop(), view.getPaddingRight(),
				view.getPaddingBottom());
		view.setOnTouchListener(rippleDrawableCompBat);		
		if (Build.VERSION.SDK_INT >= 16) {
			view.setBackground(rippleDrawableCompBat);
			return;
		}
		view.setBackgroundDrawable(rippleDrawableCompBat);
	}

	@TargetApi(21)
	protected static void createStockRipple(View view, int color) {
		view.setBackground(new RippleDrawable(new ColorStateList(
				new int[][]{{16842910}}, new int[]{color}),
				view.getBackground(), null));		
	}

	private void setTouchRippleCoords(float x, float y) {
		this.mTouchRipple.cx = x;
		this.mTouchRipple.cy = y;
	}

	private void createTouchRipple(float value, boolean fade) {
		this.mAnimationValue = value;
		this.mTouchRipple.radius = (this.mAnimationValue * 1.3F * this.mViewSize);
		int i = 20 + (int) (this.mAnimationValue * 50);
		if (fade) {
			this.mRipplePaint.setAlpha(90 - i);
		}
		invalidateSelf();
	}

	public void draw(Canvas canvas) {		
		if (this.mOriginalBackground != null) {
			this.mOriginalBackground.setBounds(getBounds());
			this.mOriginalBackground.draw(canvas);
		}
		this.mTouchRipple.draw(canvas, this.mRipplePaint);
	}

	float getAnimationState() {
		return this.mAnimationValue;
	}

	public int getOpacity() {
		return Color.TRANSPARENT;
	}

	@Override
	public int[] getState() {		
		if (this.mOriginalBackground != null) {
			int[] arrayOfInt = this.mOriginalBackground.getState();
			this.mOriginalBackground.invalidateSelf();
			return arrayOfInt;
		}
		return super.getState();
	}

	@Override
	public boolean setState(int[] states) {		
		if (this.mOriginalBackground != null) {
			boolean bool = this.mOriginalBackground.setState(states);
			this.mOriginalBackground.invalidateSelf();
			return bool;
		}
		return super.setState(states);
	}
	
	void initRippleElements() {
		this.mTouchRipple = new Circle();
		this.mRipplePaint.setStyle(Paint.Style.FILL);
		this.mRippleBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	public boolean isEminateFromCenter() {
		return this.eminateFromCenter;
	}

	void onFingerDown(View view, float x, float y) {
		onFingerMove(view, x, y);
		this.mTouchRipple.radius = 0.0F;
		if (this.eminateFromCenter) {
			this.mViewSize = Math.max(view.getWidth() / 2, view.getHeight() / 2);
		} else {
			this.mViewSize = Math.max(view.getWidth(), view.getHeight());
		}
		
		if (this.mCurrentAnimator == null) {
			this.mCurrentAnimator = ObjectAnimator.ofFloat(this,
					CREATE_TOUCH_RIPPLE, new float[] { 0.0F, 1.0F });
			this.mCurrentAnimator.setDuration(START_ANIM_DURATION);
			this.mCurrentAnimator.setInterpolator(new LinearInterpolator());
		}
		if( !this.mCurrentAnimator.isRunning() ) {
			this.mCurrentAnimator.start();
		}
	}

	void onFingerMove(View view, float x, float y) {
		if (this.eminateFromCenter) {
			setTouchRippleCoords(view.getWidth() / 2, view.getHeight() / 2);
		} else {
			setTouchRippleCoords(x, y);
		}
		invalidateSelf();		
	}

	void onFingerUp() {
		long l = 0;		
		if (this.mCurrentAnimator != null) {
			l = this.mCurrentAnimator.getCurrentPlayTime();
			this.mCurrentAnimator.cancel();
			this.mCurrentAnimator = null;
		}
		this.mCurrentAnimator = ObjectAnimator.ofFloat(this,
				CREATE_TOUCH_RIPPLE, new float[] { 0.0F, 1.0F });
		this.mCurrentAnimator.setDuration(450L);
		this.mCurrentAnimator.start();
		this.mCurrentAnimator.setInterpolator(new LinearInterpolator());
		this.mCurrentAnimator.setCurrentPlayTime((int) (450.0F * (float) l / (float)START_ANIM_DURATION));
		this.mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animator) {
				super.onAnimationEnd(animator);
				RippleDrawableCompBat.this.mCurrentAnimator = null;
			}
		});
		int[] arrayOfInt = new int[2];
		arrayOfInt[0] = this.mRipplePaint.getAlpha();
		arrayOfInt[1] = 0;
		ObjectAnimator oa = ObjectAnimator.ofInt(this, "RippleAlpha", arrayOfInt)
				                                           .setDuration(450L);
		oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						RippleDrawableCompBat.this.invalidateSelf();
					}
				});
		oa.start();	
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		default:
		case MotionEvent.ACTION_DOWN:
			onFingerDown(view, event.getX(), event.getY());
			return view.onTouchEvent(event);			
		case MotionEvent.ACTION_HOVER_MOVE:
			onFingerMove(view, event.getX(),
					event.getY());
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			onFingerUp();		
		}
		return false;
	}

	public void setAlpha(int alpha) {
		this.mRipplePaint.setAlpha(alpha);
	}

	public void setColor(int color) {		
		this.mRipplePaint.setColor(color);
		invalidateSelf();
	}

	public void setColorFilter(ColorFilter colorFilter) {		
		this.mRipplePaint.setColorFilter(colorFilter);
	}
	
	public void setDrawable(Drawable drawable) {		
		this.mOriginalBackground = drawable;
		invalidateSelf();
	}

	public void setEminateFromCenter(boolean eminateFromCenter) {
		this.eminateFromCenter = eminateFromCenter;
	}

	public void setRippleAlpha(int alpha) {
		this.mRipplePaint.setAlpha(alpha);
	}

	static final class Circle {
		float cx;
		float cy;
		float radius;
		public void draw(Canvas canvas, Paint paint) {
			canvas.drawCircle(this.cx, this.cy, this.radius, paint);
		}
	}
}