package com.xiaoxin.materialui.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: Point坐标估算
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午8:25:13
 *
 */
public class PointFEvaluator implements TypeEvaluator<PointF> {
	public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
		return new PointF(
				startValue.x + fraction * (endValue.x - startValue.x), 
				startValue.y + fraction * (endValue.y - startValue.y));
	}	
}