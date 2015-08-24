package com.xiaoxin.materialui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description:
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午14:40:12
 *
 */
public class LeftWaveInView extends View {
  public static final int ANIM_DURATION = 350;
  private static final int HORIZONTAL_SLICES = 15;
  private static final int VERTICAL_SLICES = 5;
  private static final int TOTAL_SLICES_COUNT = 96;
  private Bitmap bitmap;
  private float drawPadding;
  private final float[] drawingVerts = new float[192];
  private TimeInterpolator interpolator;
  boolean isListenerAdded = false;
  private int startDelay;
  private final float[] staticVerts = new float[192];

  public LeftWaveInView(Context context) {
    super(context);
  }

  public LeftWaveInView(Context context, AttributeSet attributeSet) {
	  super(context, attributeSet);
  }

  public LeftWaveInView(Context context, AttributeSet attributeSet, int defRes) {
		super(context, attributeSet, defRes);
  }

  private static void adToParentLayout(View view, float dist, LeftWaveInView leftWaveInView) {
    ((ViewGroup)view.getParent()).addView(leftWaveInView, new ViewGroup.LayoutParams((int)(view.getWidth() + dist * 2.0F), (int)(dist * 2.0F + view.getHeight())));
    leftWaveInView.setX(view.getLeft() - dist);
    leftWaveInView.setY(view.getTop() - dist);
    leftWaveInView.setDrawPadding(dist);
  }

  private static void addToLayoutRoot(View view, float dist, LeftWaveInView leftWaveInView) {
    ((ViewGroup)view.getRootView()).addView(leftWaveInView, new ViewGroup.LayoutParams((int)(view.getWidth() + dist * 2.0F), (int)(dist * 2.0F + view.getHeight())));
    int[] xy = new int[2];
    view.getLocationInWindow(xy);
    leftWaveInView.setX(xy[0] - dist);
    leftWaveInView.setY(xy[1] - dist);
    leftWaveInView.setDrawPadding(dist);
  }

  private static void addView(TimeInterpolator interpolator, View view, int statDelay, boolean addToParent) {
    float dist = 50.0F * view.getResources().getDisplayMetrics().density;
    LeftWaveInView leftWaveInView = new LeftWaveInView(view.getContext());
    leftWaveInView.setInterpolator(interpolator);
    leftWaveInView.setStartDelay(statDelay);
    if (addToParent) {
    	adToParentLayout(view, dist, leftWaveInView);
    } else {
    	addToLayoutRoot(view, dist, leftWaveInView);
    }
    leftWaveInView.createBitmap(view);
    view.setVisibility(View.INVISIBLE);
    leftWaveInView.animateIn(view);
  }

  private void animateIn(View view) {
    for (int i = -1 + this.drawingVerts.length / 2; i >= 0; --i) {
    	float x = this.drawingVerts[0 + (i * 2)];
    	float y = this.drawingVerts[(1 + i * 2)];
    	int pos = i;
    	animateXCoords(view, y, x, pos, animateYCoords(view, y, x, pos));
    }
  }

  private void animateXCoords(View view, float y, float x, final int pos, float ratioX) {
    float ratio = 1.0F - (Math.max(0.001F, y) / view.getHeight());
    float[] values = new float[2];
    values[0] = (x - (ratio * view.getHeight() / 2.0f));
    values[1] = x;
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(values).setDuration(ANIM_DURATION);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        LeftWaveInView.this.setXA(LeftWaveInView.this.drawingVerts, pos, ((Float)valueAnimator.getAnimatedValue()).floatValue());
      }
    });
    valueAnimator.setInterpolator(this.interpolator);
    valueAnimator.setStartDelay(this.startDelay + (int)(245.0F * ratioX));
    valueAnimator.start();
  }

  private float animateYCoords(View view, float y, final float x, final int pos) {
    float[] values = new float[2];
    values[0] = view.getHeight();
    values[1] = y;
    ValueAnimator va = ValueAnimator.ofFloat(values).setDuration(ANIM_DURATION);
    float ratio = Math.max(0.001F, x) / view.getWidth();
    setXY(this.drawingVerts, pos, x, view.getHeight());
    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
        LeftWaveInView.this.setXY(LeftWaveInView.this.drawingVerts, pos, x, ((Float)paramValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    va.setInterpolator(this.interpolator);
    va.setStartDelay(this.startDelay + (int)(245.0F * ratio));
    va.start();
    if( (ratio == 1.0f) && (!this.isListenerAdded) ) {
      va.addListener(getWaveFinishedListener(view));
      this.isListenerAdded = true;
    }
    return ratio;
  }

  private void createBitmap(View paramView) {
    this.bitmap = Bitmap.createBitmap(paramView.getWidth(), paramView.getHeight(), Bitmap.Config.ARGB_8888);
    paramView.draw(new Canvas(this.bitmap));
    createVerts();
  }

  private void createVerts() {
    float w = this.bitmap.getWidth();
    float h = this.bitmap.getHeight();
    int i = 0;
    for (int j = 0; j <= VERTICAL_SLICES; ++j) {
      float y = h * j / (float)VERTICAL_SLICES;
      for (int k = 0; k <= HORIZONTAL_SLICES; ++k) {
        float x = w * k / (float)HORIZONTAL_SLICES;
        setXY(this.drawingVerts, i, x, y);
        setXY(this.staticVerts, i, x, y);
        ++i;
      }
    }
  }

  public static void doWaveInAnimForView(final TimeInterpolator interpolator, final int statDelay, final View view, final boolean addToParent) {
    if (view.getWidth() == 0) {
      view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        public boolean onPreDraw() {
          view.getViewTreeObserver().removeOnPreDrawListener(this);
          if (view.getWidth() != 0) {
        	  Log.e("LeftWaveInView", Log.getStackTraceString(new Exception()));            
        	  LeftWaveInView.addView(interpolator, view, statDelay, addToParent);
          }
          return true;
        }
      });
      return;
    }
    addView(interpolator, view, statDelay, addToParent);                     
  }

  private AnimatorListenerAdapter getWaveFinishedListener(final View view) {
    return new AnimatorListenerAdapter() {
      public void onAnimationEnd(Animator animator) {
        super.onAnimationEnd(animator);
        view.setVisibility(View.VISIBLE);
        ((ViewGroup)LeftWaveInView.this.getParent()).removeView(LeftWaveInView.this);
        LeftWaveInView.this.bitmap.recycle();
      }
    };
  }

  public float getDrawPadding() {
    return this.drawPadding;
  }

  public TimeInterpolator getInterpolator() {
    return this.interpolator;
  }

  public int getStartDelay() {
    return this.startDelay;
  }

  protected void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    if (this.bitmap != null) {
      paramCanvas.save();
      paramCanvas.translate(this.drawPadding, this.drawPadding);
      paramCanvas.drawBitmapMesh(this.bitmap, HORIZONTAL_SLICES, VERTICAL_SLICES, this.drawingVerts, 0, null, 0, null);
      paramCanvas.restore();
    }
    invalidate();
  }

  public void setDrawPadding(float drawPadding) {
    this.drawPadding = drawPadding;
  }

  public void setInterpolator(TimeInterpolator interpolator) {
    this.interpolator = interpolator;
  }

  public void setStartDelay(int startDelay) {
    this.startDelay = startDelay;
  }

  public void setXA(float[] verts, int pos, float y) {
    verts[(0 + pos * 2)] = y;
  }

  public void setXY(float[] verts, int pos, float x, float y) {
    verts[(0 + pos * 2)] = x;
    verts[(1 + pos * 2)] = y;
  }

  public void setYA(float[] verts, int x, float y) {
    verts[(1 + x * 2)] = (y + this.staticVerts[(1 + x * 2)]);
  }
}