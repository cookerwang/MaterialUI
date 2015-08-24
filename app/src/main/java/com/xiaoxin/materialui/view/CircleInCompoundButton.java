package com.xiaoxin.materialui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.xiaoxin.materialui.R;
import com.xiaoxin.materialui.animator.SplashIn;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description:
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午8:40:12
 *
 */
public class CircleInCompoundButton extends ToggleButton {
	boolean isSplashFinished;
	SplashIn splashIn;

	public CircleInCompoundButton(Context context) {
		super(context);
		init();
	}

	public CircleInCompoundButton(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
		TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.AnimatedPathView);
		splashIn.setStartDelay(a.getInteger(1, 500));
		a.recycle();
	}

	public CircleInCompoundButton(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		init();
	}

	private void init() {
		splashIn = new SplashIn(this, Color.RED, Color.CYAN);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		splashIn.draw(canvas);
		canvas.save();
		canvas.scale(splashIn.getCurrentScale(), splashIn.getCurrentScale(),  getWidth() / 2, getHeight() / 2);
		super.onDraw(canvas);
		canvas.restore();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!(isSplashFinished))
			splashIn.initAnims();
		isSplashFinished = true;
	}
}