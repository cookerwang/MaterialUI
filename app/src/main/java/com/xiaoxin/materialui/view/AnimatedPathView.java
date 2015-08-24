package com.xiaoxin.materialui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoxin.materialui.R;
import com.xiaoxin.materialui.evaluator.TRectEvaluator;
import com.xiaoxin.materialui.evaluator.TRectFEvaluator;
import com.xiaoxin.materialui.interpolator.QuintInOut;

/**
 * 
  * @ClassName: AnimatedPathView
  * @Description: 分割线显示动画，线粗1dp
  * @author xiaoxin wangrenxing@1872.net
  * @date 2015年8月24日 上午8:40:12
  *
 */
public class AnimatedPathView extends View {
	public static final int ANIM_DURATION = 1500;
	public static final int VERTICAL_ORIENTAION = 0;
	public static final int HORIZONTAL_ORIENTAION = 1;
	public static final float LINE_HEIGHT = 0.5F;
	public static final int START_DELAY = 400;
	private int animDuration = -1;
	private RectF firstCircleRect = new RectF();
	private Rect firstLineRect = new Rect();
	private boolean isRunning = false;
	private Paint linePaint = new Paint();
	private int orientation = 1;
	private RectF secondCircleRect = new RectF();
	private Rect secondLineRect = new Rect();
	private long startDelay = 400L;
	private float density = 0;
	public AnimatedPathView(Context context) {
		super(context);
	}

	public AnimatedPathView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init(attributeSet);
	}

	public AnimatedPathView(Context context, AttributeSet attributeSet, int paramInt) {
		super(context, attributeSet, paramInt);
		init(attributeSet);
	}

	private void init(AttributeSet attributeSet) {
		density = getResources().getDisplayMetrics().density;
		initLinePaint();
		TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.AnimatedPathView);
		orientation = a.getInt(R.styleable.AnimatedPathView_orientation, HORIZONTAL_ORIENTAION);
		startDelay = a.getInteger(R.styleable.AnimatedPathView_start_delay, START_DELAY);		
		linePaint.setColor(a.getColor(R.styleable.AnimatedPathView_path_color, Color.BLUE));
		a.recycle();
	}
	
	private void initLinePaint() {
		linePaint.setColor(Color.BLUE);
		linePaint.setAntiAlias(true);
		linePaint.setDither(true);
	}
	
	// 水平方向，左边圆动画，最后没入到线条里面
	private void createLeftCircleAnim(int cx, int yTop, int yBottom, int cy) {
		this.firstCircleRect = new RectF(cx - cy, 0.0F, cx + cy, getMeasuredHeight());
		
		Object[] values = new Object[2];
		values[0] = new RectF(cx - cy, 0.0F, cx + cy, getMeasuredHeight()); // view的高度为直径	
		values[1] = new RectF(0.0F, yTop, (int) (0.5F * density), yBottom); // 线条高度为半径
		ValueAnimator va = ValueAnimator.ofObject(new TRectFEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(this.startDelay);
		va.addUpdateListener(getLeftCircleListener());
		va.start();
	}

	// 水平方向，左边直线动画，中点--> 0
	private void createLeftLineHorizontalAnim(int cx, int yTop, int yBottom) {		
		Object[] values = new Object[2];
		values[0] = new Rect(cx, yTop, cx, yBottom);
		values[1] = new Rect(0, yTop, cx, yBottom);
		ValueAnimator va = ValueAnimator.ofObject(new TRectEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(this.startDelay);
		va.addUpdateListener(getFirstLineListener());
		va.start();
	}

	// 水平方向，右边圆动画，最后没入到线条里面
	private void createRightCircleAnim(int cx, int yTop, int yBottom, int cy) {
		this.secondCircleRect = new RectF(cx - cy, 0.0F, cx + cy, getMeasuredHeight());		
		Object[] values = new Object[2];
		values[0] = new RectF(cx - cy, 0.0F, cx + cy, getMeasuredHeight()); // view的高度为直径
		values[1] = new RectF(getWidth() - (int) (0.5F * density), yTop, getWidth(), yBottom); // 线条高度为半径
		ValueAnimator va = ValueAnimator.ofObject(new TRectFEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.addUpdateListener(getRightCircleListener());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(this.startDelay);
		va.start();
	}

	// 水平方向，左边直线动画，中点--> width
	private void createRightLineAnim(int cx, int yTop, int yBottom) {		
		Object[] values = new Object[2];
		values[0] = new Rect(cx, yTop, cx, yBottom);
		values[1] = new Rect(cx, yTop, getWidth(), yBottom);
		ValueAnimator va = ValueAnimator.ofObject(new TRectEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.addUpdateListener(getSecondLineListener());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(this.startDelay);
		va.start();
	}

	// 垂直方向，顶部圆动画
	private void createTopCircleAnim(int cx, int xLeft, int xRight, int cy) {
		this.firstCircleRect = new RectF(0.0F, cy - cx, getMeasuredWidth(), cy + cx);
		Object[] values = new Object[2];
		values[0] = new RectF(0.0F, cy - cx, getMeasuredWidth(), cy + cx);
		values[1] = new RectF(xLeft, 0.0F, xRight, (int) (0.5F * density));
		ValueAnimator va = ValueAnimator.ofObject(new TRectFEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(this.startDelay);
		va.addUpdateListener(getLeftCircleListener());
		va.start();
	}

	// 垂直方向，顶部直线动画
	private void createTopLineVerticalAnim(int cy, int xLeft, int xRight) {
		TRectEvaluator localTRectEvaluator = new TRectEvaluator();
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = new Rect(xLeft, cy, xRight, cy);
		arrayOfObject[1] = new Rect(xLeft, 0, xRight, cy);
		ValueAnimator localValueAnimator = ValueAnimator.ofObject(localTRectEvaluator, arrayOfObject);
		localValueAnimator.setDuration(getAnimDuration());
		localValueAnimator.setInterpolator(new QuintInOut());
		localValueAnimator.setStartDelay(this.startDelay);
		localValueAnimator.addUpdateListener(getFirstLineListener());
		localValueAnimator.start();
	}
	
	// 垂直方向，底部圆动画
	private void createBottomCircleAnim(int cx, int xLeft, int xRight, int cy) {
		secondCircleRect = new RectF(0.0F, cy - cx, getMeasuredWidth(), cy + cx);		
		Object[] values = new Object[2];
		values[0] = new RectF(0.0F, cy - cx, getMeasuredWidth(), cy + cx);
		values[1] = new RectF(xLeft, getMeasuredHeight()
				- (int) (0.5F * density), xRight, getMeasuredHeight());
		ValueAnimator va = ValueAnimator.ofObject(new TRectFEvaluator(), values);
		va.setDuration(getAnimDuration());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(startDelay);
		va.addUpdateListener(getRightCircleListener());
		va.start();
	}
	
	// 垂直方向，顶部直线动画
	private void createBottomLineVerticalAnim(int cy, int xLeft, int xRight) {
		TRectEvaluator evaluator = new TRectEvaluator();
		Object[] values = new Object[2];
		values[0] = new Rect(xLeft, cy, xRight, cy);
		values[1] = new Rect(xLeft, cy, xRight, getHeight());
		ValueAnimator va = ValueAnimator.ofObject(evaluator, values);
		va.setDuration(getAnimDuration());
		va.setInterpolator(new QuintInOut());
		va.setStartDelay(startDelay);
		va.addUpdateListener(getSecondLineListener());
		va.start();
	}
	
	private int getAnimDuration() {
		if (animDuration == -1)
			return 1500;
		return animDuration;
	}

	private ValueAnimator.AnimatorUpdateListener getFirstLineListener() {
		return new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				firstLineRect = (Rect) valueAnimator.getAnimatedValue();
			}
		};
	}

	private ValueAnimator.AnimatorUpdateListener getLeftCircleListener() {
		return new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				firstCircleRect = (RectF) valueAnimator.getAnimatedValue();
			}
		};
	}

	private ValueAnimator.AnimatorUpdateListener getRightCircleListener() {
		return new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				secondCircleRect = (RectF) valueAnimator.getAnimatedValue();
				invalidate();
			}
		};
	}

	private ValueAnimator.AnimatorUpdateListener getSecondLineListener() {
		return new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				secondLineRect = (Rect) valueAnimator.getAnimatedValue();
			}
		};
	}

	// 开始水平动画
	private void runAnimHorizontal(int cx, int cy, int yTop, int yBottom) {
		createLeftLineHorizontalAnim(cx, yTop, yBottom);
		createRightLineAnim(cx, yTop, yBottom);
		createLeftCircleAnim(cx, yTop, yBottom, cy);
		createRightCircleAnim(cx, yTop, yBottom, cy);
	}

	// 开始垂直动画
	private void runAnimVertical(int cx, int cy, int xLeft, int xRight) {
		createTopLineVerticalAnim(cy, xLeft, xRight);
		createBottomLineVerticalAnim(cy, xLeft, xRight);
		createTopCircleAnim(cx, xLeft, xRight, cy);
		createBottomCircleAnim(cx, xLeft, xRight, cy);
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		// 画线
		paramCanvas.drawRect(firstLineRect, linePaint);
		paramCanvas.drawRect(secondLineRect, linePaint);
		// 画圆
		paramCanvas.drawOval(firstCircleRect, linePaint);
		paramCanvas.drawOval(secondCircleRect, linePaint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if( !isRunning ) {
			isRunning = true;
			int cx = getMeasuredWidth() / 2;
			int cy = getMeasuredHeight() / 2;
			int yTop = (int) (cy - (0.5F * density));
			int yBottom = (int) (cy + 0.5F * density);
			int xLeft = (int) (cx - (0.5F * density));
			int xRight = (int) (cx + 0.5F * density);
			if ( orientation == HORIZONTAL_ORIENTAION ) {				
				runAnimHorizontal(cx, cy, yTop, yBottom);
			} else {
				runAnimVertical(cx, cy, xLeft, xRight);
			}
		}
	}

	public void setAnimDuration(int animDuration) {
		this.animDuration = animDuration;
	}

	public void setColor(int color) {
		linePaint.setColor(color);
	}

	public void setStartDelay(long startDelay) {
		this.startDelay = startDelay;
	}
}