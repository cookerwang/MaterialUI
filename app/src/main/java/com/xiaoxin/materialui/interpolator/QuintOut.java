package com.xiaoxin.materialui.interpolator;

import android.view.animation.Interpolator;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 插值器
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午8:20:12
 *
 */
public class QuintOut implements Interpolator {
	public float getInterpolation(float input) {
		float f = input - 1.0F;
		return (1.0F + f * f * f * f * f);
	}
}