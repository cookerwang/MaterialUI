package com.xiaoxin.materialui.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.RectF;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: RectF估算
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午8:22:12
 *
 */
public class TRectFEvaluator implements TypeEvaluator<RectF> {
  public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
    return new RectF(
    		startValue.left + (int)(fraction * (endValue.left - startValue.left)), 
    		startValue.top + (int)(fraction * (endValue.top - startValue.top)), 
    		startValue.right + (int)(fraction * (endValue.right - startValue.right)), 
    		startValue.bottom + (int)(fraction * (endValue.bottom - startValue.bottom)));
  }
}