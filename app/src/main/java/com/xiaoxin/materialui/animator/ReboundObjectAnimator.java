package com.xiaoxin.materialui.animator;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
  * 对facebook的Rebound库使用的再次封装
  * @ClassName: ReboundObjectAnimator
  * @Description: TODO
  * @author xiaoxin wangrenxing@1872.net
  * @date 2015年8月21日 上午8:30:50
  *
 */
public class ReboundObjectAnimator {
	private ReboundUpdateListener animatorUpdateListener;
	private float from;
	private Method propertyMethod;
	private float to;
	private Spring spring;
	private Object target;
	private double friction = 6.0; // 阻力
	private double tension = 150.0; // 张力

	@SuppressLint("DefaultLocale")
	public ReboundObjectAnimator(Object target, @NonNull String propertyName,
			float from, float to) {
		StringBuffer setName = new StringBuffer();
		setName.append("set");
		setName.append(propertyName.substring(0, 1).toUpperCase());
		if (propertyName.length() > 1) {
			setName.append(propertyName.substring(1));
		}
		propertyMethod = getPropertyMethod(target, setName.toString());
		this.from = from;
		this.to = to;
		this.target = target;
	}

	private static Method findMethod(Class<?> clzz, String meathodName)
			throws NoSuchMethodException {
		for (Method method : clzz.getMethods()) {
			if (method.getName().equals(meathodName)) {
				return method;
			}
		}
		throw new NoSuchMethodException();
	}

	private static Method getPropertyMethod(Object target, String methodName) {
		try {
			Class<? extends Object> clzz = target.getClass();
			return findMethod(clzz, methodName);
		} catch (NoSuchMethodException noSuchMethodException) {
			noSuchMethodException.printStackTrace();
		}
		return null;
	}

	public static ReboundObjectAnimator ofFloat(Object target,
			String propertyName, float from, float to) {
		return new ReboundObjectAnimator(target, propertyName, from, to);
	}

	public void addUpdateListener(ReboundUpdateListener reboundUpdateListener) {
		animatorUpdateListener = reboundUpdateListener;
	}

	public void cancel() {
		if (spring != null) {
			spring.destroy();
		}
	}

	public double getFriction() {
		return friction;
	}

	public double getTension() {
		return tension;
	}

	public ReboundObjectAnimator setFriction(double friction) {
		this.friction = friction;
		return this;
	}

	public ReboundObjectAnimator setTension(double tension) {
		this.tension = tension;
		return this;
	}

	public void start() {
		spring = SpringSystem.create().createSpring();
		spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(
				tension, friction));

		spring.addListener(new SimpleSpringListener() {
			public void onSpringAtRest(Spring spring) {
				super.onSpringAtRest(spring);
				spring.destroy();
			}

			public void onSpringUpdate(Spring spring) {
				float value = (float) spring.getCurrentValue();
				if (animatorUpdateListener != null) {
					animatorUpdateListener.onAnimationUpdate(value);
				}
				try {
					if (propertyMethod != null) {
						Object[] values = new Object[1];
						values[0] = Float.valueOf(from + value * (to - from));
						propertyMethod.invoke(target, values);
					}
				} catch (IllegalAccessException illegalAccessException) {
					illegalAccessException.printStackTrace();
				} catch (InvocationTargetException invocationTargetException) {
					invocationTargetException.printStackTrace();
				}
			}
		});
		spring.setEndValue(1.0);
	}

	public static abstract interface ReboundUpdateListener {
		public abstract void onAnimationUpdate(float value);
	}
}