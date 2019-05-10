package com.jungh0.w3w_map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        String aa = "#617D8C";
        String bb = "#0D0F0D";

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("환영합니다!");
        sliderPage1.setDescription("W3W 기반 실시간 위치 공유 서비스");
        sliderPage1.setImageDrawable(R.drawable._intro1);
        sliderPage1.setBgColor(Color.parseColor(aa));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("W3W - What3Words");
        sliderPage3.setDescription("어느곳에 있든 단어 3개로 찾아가세요!");
        sliderPage3.setImageDrawable(R.drawable._intro2);
        sliderPage3.setBgColor(Color.parseColor(aa));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("위치 공유");
        sliderPage4.setDescription("실시간 위치를 익명으로 공유하세요!!");
        sliderPage4.setImageDrawable(R.drawable._intro3);
        sliderPage4.setBgColor(Color.parseColor(aa));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("공유 기능");
        sliderPage2.setDescription("현재 위치 정보가 있는 단어 3개를 공유하세요!");
        sliderPage2.setImageDrawable(R.drawable._intro4);
        sliderPage2.setBgColor(Color.parseColor(aa));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("실시간 확인");
        sliderPage5.setDescription("이제 WAY를 사용 해보세요!");
        sliderPage5.setImageDrawable(R.drawable._intro5);
        sliderPage5.setBgColor(Color.parseColor(aa));
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}