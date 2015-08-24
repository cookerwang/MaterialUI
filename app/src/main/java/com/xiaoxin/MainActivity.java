package com.xiaoxin;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.xiaoxin.materialui.R;
import com.xiaoxin.materialui.animator.ReboundObjectAnimator;
import com.xiaoxin.materialui.view.CenterWaveInView;
import com.xiaoxin.materialui.view.MenuAndBackButton;
import com.xiaoxin.materialui.view.PageIndicator;
import com.xiaoxin.materialui.view.RippleDrawableCompBat;

import java.util.ArrayList;
/**
 *
 * @ClassName: AnimatedPathView
 * @Description: 分割线显示动画，线粗1dp
 * @author xiaoxin wangrenxing@1872.net
 * @date 2015年8月25日 上午9:40:12
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PageIndicator pageIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init revealdrabelcompat
        RippleDrawableCompBat.createRipple(findViewById(R.id.reveal_view), Color.BLUE);
        // init pageindicator
        initPageIndicator();
    }

    private void initPageIndicator() {
        final View root = findViewById(R.id.root);
        pageIndicator = ((PageIndicator) root.findViewById(R.id.page_indicator));
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                View localView1 = findViewById(R.id.ic_1);
                pageIndicator.addStopPoint(new PointF(localView1.getLeft()
                        + localView1.getWidth() / 2, 0.0F));
                View localView2 = findViewById(R.id.ic_2);
                pageIndicator.addStopPoint(new PointF(localView2.getLeft()
                        + localView2.getWidth() / 2, 0.0F));
                View localView3 = findViewById(R.id.ic_3);
                pageIndicator.addStopPoint(new PointF(localView3.getLeft()
                        + localView3.getWidth() / 2, 0.0F));
                View localView4 = findViewById(R.id.ic_4);
                pageIndicator.addStopPoint(new PointF(localView4.getLeft()
                        + localView4.getWidth() / 2, 0.0F));
                ArrayList<Integer> localArrayList = new ArrayList<Integer>();
                localArrayList.add(Integer.valueOf(Color.parseColor("#43A4B6")));
                localArrayList.add(Integer.valueOf(Color.parseColor("#519494")));
                localArrayList.add(Integer.valueOf(Color.parseColor("#53D2AA")));
                localArrayList.add(Integer.valueOf(Color.parseColor("#FDAF65")));
                pageIndicator.setColors(localArrayList);
                pageIndicator.seekJelly(0, 3.0F);
                root.getViewTreeObserver().removeOnPreDrawListener(this);

                localView1.setOnClickListener(MainActivity.this);
                localView2.setOnClickListener(MainActivity.this);
                localView3.setOnClickListener(MainActivity.this);
                localView4.setOnClickListener(MainActivity.this);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if( testPageIndicator(v) ) {
            return ;
        }
        if( v.getId() == R.id.imageView ) {
            ReboundObjectAnimator.ofFloat(v, "scaleX", 0.0f, 0.6f).start();
            ReboundObjectAnimator.ofFloat(v, "scaleY", 0.0f, 0.6f).start();
        }

        if( v.getId() == R.id.menu ) {
            MenuAndBackButton mb = (MenuAndBackButton)v;
            if( mb.isInBackState() ) {
                mb.goToMenu();
            } else {
                mb.goToArrow();
            }
        }

        if( v.getId() == R.id.test ) {
            CenterWaveInView.doWaveInAnimForView(v, new BounceInterpolator(), 1000, Color.TRANSPARENT, false);
        }
    }
    private int currentId = 0;
    // 测试PageIndicator，结合ViewPger的滑动事件更好
    private boolean testPageIndicator(View v) {
        final int id = v.getId();
        if( id < R.id.ic_1 || id > R.id.ic_4 ) {
            return false;
        }
        float start = 0.0f;
        float end = 1.0f;
        if( id == R.id.ic_1 ) {
            end = 0.0f;
        }
        if( id < currentId ) {
            start = 1.0f;
            end = 0.0f;
        }
        currentId = id;
        ValueAnimator va = ValueAnimator.ofFloat(new float[]{start, end});
        va.setInterpolator(new LinearInterpolator());
        va.setDuration(600);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = ((Float)animation.getAnimatedValue()).floatValue();
                int idx = Math.max(0, currentId - R.id.ic_1 - 1);
                pageIndicator.seekJelly(idx, f);
            }
        });
        va.start();
        return true;
    }


}
