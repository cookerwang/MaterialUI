package com.xiaoxin.materialui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: wave动画
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午9:40:12
 *
 */
public class CenterWaveInView extends View {
	private static final int HORIZONTAL_SLICES = 25;
	private static final int VERTICAL_SLICES = 5;
	private static final int TOTAL_SLICES_COUNT = 156; //( mesh (25+1)*(5+1) )
	
	private long animDuration;
	private Paint backgroundPaint = new Paint();
	private Bitmap bitmap;	
	private float drawPadding;
	private final float[] staticVerts = new float[312]; // 依次存放x, y
	private final float[] drawingVerts = new float[312];
	private TimeInterpolator interpolator;
	boolean isListenerAdded = false;
	
	public CenterWaveInView(Context context) {
		super(context);
	}

	public CenterWaveInView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public CenterWaveInView(Context context, AttributeSet attributeSet, int defRes) {
		super(context, attributeSet, defRes);
	}

	// CenterWaveInView放置在view底部，添加到父布局，padding是dist
	private static void adToParentLayout(View view, float dist, CenterWaveInView centerWaveInView) {
		((ViewGroup) view.getParent()).addView(centerWaveInView,
				new ViewGroup.LayoutParams(
						(int) (view.getWidth() + dist * 2.0F),
						(int) (view.getHeight() + dist * 2.0F)));
		centerWaveInView.setX(view.getLeft() - dist);
		centerWaveInView.setY(view.getTop() - dist);
		centerWaveInView.setDrawPadding(dist);
	}

	// CenterWaveInView放置在view的底部，添加到view的根布局，padding是dist
	private static void addRootToLayout(View view, float dist, CenterWaveInView centerWaveInView) {
		((ViewGroup) view.getRootView()).addView(centerWaveInView,
				new ViewGroup.LayoutParams(
						(int) (view.getWidth() + dist * 2.0F),
						(int) (view.getHeight() + dist * 2.0F)));
		int[] loc = new int[2];
		view.getLocationInWindow(loc);
		centerWaveInView.setX(loc[0] - dist);
		centerWaveInView.setY(loc[1] - dist);
		centerWaveInView.setDrawPadding(dist);
	}

	// 创建CenterWaveInView并添加到view相关布局中
	private static void addView(View view, TimeInterpolator timeInterpolator, 
							long duration, int bgColor, boolean addToParentLayout) {
		float dist = 50.0f * view.getResources().getDisplayMetrics().density;
		CenterWaveInView centerWaveInView = new CenterWaveInView(view.getContext());
		centerWaveInView.setInterpolator(timeInterpolator);
		centerWaveInView.setColorForBackgroundRect(bgColor);
		centerWaveInView.setAnimDuration(duration);
		centerWaveInView.createBitmap(view);
		view.setVisibility(View.INVISIBLE);	
		if (!addToParentLayout ) {
			addRootToLayout(view, dist, centerWaveInView);			
		} else {
			adToParentLayout(view, dist, centerWaveInView);
		}
	}

	// 启动动画
	private void animateIn(View view) {		
		for (int i = -1 + this.drawingVerts.length / 2; i >= 0; --i) {
			float x = this.drawingVerts[(0 + i * 2)];
			float y = this.drawingVerts[(1 + i * 2)];
			int xPos = i;
			float ratio = Math.max(0.001f, x) / view.getWidth() - 0.5F;
			animateYCoords(view, y, x, xPos, ratio);
			animateXCoords(view, y, x, xPos, ratio);
		}
	}
	
	// x数据更新
	private void animateXCoords(View view, float y, float x, final int xPos, float ratio) {
		float[] values = new float[2];
		values[0] = (x - (ratio * view.getWidth() / 2.0F));
		values[1] = x;
		ValueAnimator va = ValueAnimator.ofFloat(values)
				                                        .setDuration(this.animDuration);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
				CenterWaveInView.this.setXA(
						CenterWaveInView.this.drawingVerts, 
						xPos,
						((Float)paramValueAnimator.getAnimatedValue()).floatValue()
						);
			}
		});
		va.setInterpolator(this.interpolator);
		va.start();
	}

	// y数据更新
	private void animateYCoords(View view, float y, final float x, final int xPos, float ratio) {
	    float[] values = new float[2];
	    values[0] = view.getHeight();	    
	    values[1] = y;
	    ValueAnimator va = ValueAnimator.ofFloat(values)
	    		                                        .setDuration(this.animDuration);
	    setXY(this.drawingVerts, xPos, x, view.getHeight());
	    va.addUpdateListener(
	    		new ValueAnimator.AnimatorUpdateListener() {
					public void onAnimationUpdate(ValueAnimator paramValueAnimator){
						CenterWaveInView.this.setXY(CenterWaveInView.this.drawingVerts, 
								xPos,
								x, 
								((Float)paramValueAnimator.getAnimatedValue()).floatValue());
					}
	    });
	    va.setInterpolator(this.interpolator);	    
	    va.setStartDelay((long) Math.abs(ratio * 0.7f * (float)this.animDuration));
	    if ((!this.isListenerAdded) && (ratio == 0.5F)){
	      this.isListenerAdded = true;
	      va.addListener(getWaveFinishedListener(view));
	    }
	    va.start();
	}

	// 创建动画位图
	private void createBitmap(View view) {
		this.bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(this.bitmap);
		canvas.drawColor(Color.TRANSPARENT);
		view.draw(canvas);
		createVerts();
		animateIn(view);
	}

	// 创建网格数据，网格 5 * 25
	private void createVerts() {
		float width = this.bitmap.getWidth();
		float height = this.bitmap.getHeight();
		int i = 0;
		for (int j = 0; j <= VERTICAL_SLICES; ++j) {
			float y = height * j / (float)VERTICAL_SLICES;
			for (int k = 0; k <= HORIZONTAL_SLICES; ++k) {
				float x = width * k / (float)HORIZONTAL_SLICES; 
				setXY(this.drawingVerts, i, x, y);
				setXY(this.staticVerts, i, x, y);
				++i;
			}
		}
	}

	// view实现波浪动画
	public static void doWaveInAnimForView(final View view, 
			final TimeInterpolator timeInterpolator, final long duration,
			final int bgColor, final boolean addToParentLayout) {
		if (view.getWidth() == 0) {
			view.getViewTreeObserver().addOnPreDrawListener(
					new ViewTreeObserver.OnPreDrawListener() {
						public boolean onPreDraw() {
							view.getViewTreeObserver().removeOnPreDrawListener(this);							
							addView(view, timeInterpolator, duration, bgColor, addToParentLayout);							
							return true;
						}
					});
			return;
		}
		addView(view, timeInterpolator, duration, bgColor, addToParentLayout);
	}

	// 动画结束监听
	private AnimatorListenerAdapter getWaveFinishedListener(final View view) {
		return new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animator) {
				super.onAnimationEnd(animator);
				view.setVisibility(View.VISIBLE);
				((ViewGroup) CenterWaveInView.this.getParent()).removeView(CenterWaveInView.this);				
				CenterWaveInView.this.bitmap.recycle();
			}
		};
	}

	public float getDrawPadding() {
		return this.drawPadding;
	}

	public TimeInterpolator getInterpolator() {
		return this.interpolator;
	}

	// 绘制图形
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.bitmap != null) {
			canvas.drawRect(this.drawPadding, this.drawPadding, 
					getWidth() - this.drawPadding, getHeight() - this.drawPadding,
					this.backgroundPaint);
			canvas.save();
			canvas.translate(this.drawPadding, this.drawPadding);
			canvas.drawBitmapMesh(this.bitmap, HORIZONTAL_SLICES, VERTICAL_SLICES, this.drawingVerts, 0, null, 0, null);
			canvas.restore();
		}
		invalidate();
	}

	public void setAnimDuration(long animDuration) {
		this.animDuration = animDuration;
	}

	public void setColorForBackgroundRect(int bgColor) {		
		this.backgroundPaint.setColor(bgColor);
	}

	public void setDrawPadding(float drawPadding) {
		this.drawPadding = drawPadding;
	}

	public void setInterpolator(TimeInterpolator interpolator) {
		this.interpolator = interpolator;
	}

	public void setXA(float[] verts, int pos, float value) {
		verts[(0 + pos * 2)] = value;
	}

	public void setYA(float[] verts, int pos, float value) {
		verts[(1 + pos * 2)] = (value + this.staticVerts[(1 + pos * 2)]);
	}
	
	public void setXY(float[] verts, int pos, float x, float y) {
		verts[(0 + pos * 2)] = x;
		verts[(1 + pos * 2)] = y;
	}
}