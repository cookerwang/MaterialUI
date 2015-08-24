package com.xiaoxin.materialui.interpolator;

import android.view.animation.Interpolator;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 插值器
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月24日 上午8:10:2
 *
 */
public class QuintInOut implements Interpolator {
  public float getInterpolation(float input) {
    float firstHalf = input * 2.0F;
    if (firstHalf < 1.0F) {
      return (firstHalf * firstHalf * firstHalf * firstHalf * 0.5F * firstHalf);
    }
    float nextHalf = firstHalf - 2.0F;
    return (0.5F * (2.0F + nextHalf * nextHalf * nextHalf * nextHalf * nextHalf));
  }
}