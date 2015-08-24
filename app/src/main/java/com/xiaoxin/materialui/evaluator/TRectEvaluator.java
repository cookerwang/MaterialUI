package com.xiaoxin.materialui.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Rect;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: Rect估算
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午8:14:12
 *
 */
public class TRectEvaluator implements TypeEvaluator<Rect> {
  public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
    return new Rect(
    		startValue.left + (int)(fraction * (endValue.left - startValue.left)), 
    		startValue.top + (int)(fraction * (endValue.top - startValue.top)), 
    		startValue.right + (int)(fraction * (endValue.right - startValue.right)), 
    		startValue.bottom + (int)(fraction * (endValue.bottom - startValue.bottom)));
  }
}