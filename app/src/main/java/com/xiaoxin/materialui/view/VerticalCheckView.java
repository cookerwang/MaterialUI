package com.xiaoxin.materialui.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xiaoxin.materialui.interpolator.QuintInOut;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description:
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午10:40:12
 *
 */
public class VerticalCheckView extends TextView implements Checkable {
	public static final int ANIM_DURATION = 300;
	private List<CircleColorExpand> circles = new ArrayList<CircleColorExpand>();
	private boolean isChecked;
	private float textYCoord;
	private int colorChecked = Color.WHITE;
	private int colorUnchecked = Color.GRAY;
	private float dotRadius = 12.0f;
	
	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

	public VerticalCheckView(Context context) {
		this(context, null);
	}
	
	public VerticalCheckView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);		
	}
	
	public VerticalCheckView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				toggle();
				if( onCheckedChangeListener != null ) {
					onCheckedChangeListener.onCheckedChanged(null, isChecked);
				}
			}
		});
	}
	
	private void animateColor(int startColor, int endColor, long duration) {
		ArgbEvaluator argbEvaluator = new ArgbEvaluator();
		Object[] values = new Object[2];
		values[0] = Integer.valueOf(startColor);
		values[1] = Integer.valueOf(endColor);
		ObjectAnimator oa = ObjectAnimator.ofObject(this, "textColor", argbEvaluator, values);
		oa.setDuration(duration);
		oa.setInterpolator(new QuintInOut());
		oa.start();
	}

	private void animateToChecked(long duration) {
		float[] values = new float[2];
		values[0] = this.textYCoord;
		values[1] = 0.0f;
		ValueAnimator va = ValueAnimator.ofFloat(values);
		va.setDuration(duration);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
				textYCoord = ((Float) paramValueAnimator.getAnimatedValue()).floatValue();
				invalidate();
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if( circles.size() == 0 ) {
					expandCircle(0);
				}
			}
		});
		va.start();
		expandCircle(duration);
		animateColor(getPaint().getColor(), colorChecked, duration);
	}

	private void animateToUnchecked(long duration) {
		float[] arrayOfFloat = new float[2];
		arrayOfFloat[0] = this.textYCoord;
		arrayOfFloat[1] = getBottomCoord();
		ValueAnimator va = ValueAnimator.ofFloat(arrayOfFloat);
		va.setDuration(duration);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				textYCoord = ((Float) valueAnimator.getAnimatedValue()).floatValue();
				invalidate();
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				circles.clear();
			}
		});
		va.start();
		if( circles.size() != 0 ) {
			circles.get(0).setAnimDuration(3L);
			circles.get(0).startAnimReversed();
		}
		animateColor(getPaint().getColor(), colorUnchecked, duration);
	}

	private void expandCircle(long animDuration) {
		circles.clear();
		float radius = dotRadius * getResources().getDisplayMetrics().density;
		RectF rec = new RectF(getWidth() / 2 - (radius / 2.0F), getHeight() - radius, 
							  getWidth() / 2 + radius / 2.0F, getHeight());
		circles.add(new CircleColorExpand(rec, 0L, animDuration, colorChecked).startAnim());
	}

	public float getBottomCoord() {
		Rect rec = new Rect();
		getPaint().getTextBounds("SIZE", 0, 3, rec);
		return (getHeight() - (1.5F * rec.height()));
	}

	public CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
		return this.onCheckedChangeListener;
	}

	public boolean isChecked() {
		return this.isChecked;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (isChecked) {
			textYCoord = 0.0F;
			setChecked(true, 0L);
			return;
		}
		textYCoord = getBottomCoord();
		setChecked(false, 0L);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Iterator<CircleColorExpand> it = circles.iterator();
		while( it.hasNext() ) {
			((CircleColorExpand) it.next()).draw(canvas);
		}
		canvas.save();
		canvas.translate(0.0F, textYCoord);
		super.onDraw(canvas);
		canvas.restore();
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
		if (isChecked) {
			animateToChecked(ANIM_DURATION);			
		} else {
			animateToUnchecked(ANIM_DURATION);
		}
	}

	public void setChecked(boolean checked, long duration) {
		this.isChecked = checked;
		if (this.isChecked) {
			animateToChecked(duration);			
		} else {
			animateToUnchecked(duration);
		}
	}

	public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public void toggle() {
		setChecked(!isChecked);		
	}
}