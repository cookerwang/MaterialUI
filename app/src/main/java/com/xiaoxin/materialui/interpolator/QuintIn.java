package com.xiaoxin.materialui.interpolator;

import android.view.animation.Interpolator;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 插值器
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午8:21:12
 *
 */
public class QuintIn implements Interpolator {
  public float getInterpolation(float input) {
    return (input * input * input * input * input);
  }
}