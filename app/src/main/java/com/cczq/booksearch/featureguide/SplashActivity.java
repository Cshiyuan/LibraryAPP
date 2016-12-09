package com.cczq.booksearch.featureguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cczq.booksearch.R;
import com.cczq.booksearch.featureguide.global.AppConstants;
import com.cczq.booksearch.featureguide.utils.SpUtils;
import com.cczq.booksearch.loginresgister.LoginActivity;

import me.wangyuwei.particleview.ParticleView;


/**
 * @desc 启动屏
 * Created by bb on 16/9/22.
 */

public class SplashActivity extends Activity {

    ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用 true表示不是第一次进入
        boolean isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);
        particleView = (ParticleView) findViewById(R.id.particleView);


        //设置动画结束后的回调函数
        particleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                enterHomeActivity();
            }
        });

        //开始动画，设置延迟
        particleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                particleView.startAnim();
            }
        }, 200);

    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
