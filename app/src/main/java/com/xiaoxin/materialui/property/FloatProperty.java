package com.xiaoxin.materialui.property;

import android.util.Property;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: Float属性
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午8:00:12
 *
 */
public abstract class FloatProperty<T> extends Property<T, Float>{
  public FloatProperty(String paramString){
    super(Float.class, paramString);
  }

  public final void set(T paramT, Float paramFloat){
    setValue(paramT, paramFloat.floatValue());
  }

  public abstract void setValue(T paramT, float paramFloat);
}