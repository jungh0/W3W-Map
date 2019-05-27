package com.jungh0.w3w_map.ui.intro;

import android.graphics.Color;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.jungh0.w3w_map.R;

public class IntroPresenter implements IntroContract.Presenter {

    private IntroContract.View mView;
    private String bg_c = "#617D8C";

    public IntroPresenter(IntroContract.View view) {
        mView = view;
    }

    @Override
    public void make() {
        SliderPage SliderPage;

        SliderPage = makeslider("환영합니다!","W3W 기반 실시간 위치 공유 서비스",R.drawable._intro1);
        mView.show(SliderPage);
        SliderPage = makeslider("W3W - What3Words","어느곳에 있든 단어 3개로 찾아가세요!",R.drawable._intro2);
        mView.show(SliderPage);
        SliderPage = makeslider("위치 공유","실시간 위치를 익명으로 공유하세요!!",R.drawable._intro3);
        mView.show(SliderPage);
        SliderPage = makeslider("공유 기능","현재 위치 정보가 있는 단어 3개를 공유하세요!",R.drawable._intro4);
        mView.show(SliderPage);
        SliderPage = makeslider("실시간 확인","이제 WAY를 사용 해보세요!",R.drawable._intro5);
        mView.show(SliderPage);

        mView.intro_config();
    }

    private SliderPage makeslider(String title, String detail, int img){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setDescription(detail);
        sliderPage.setImageDrawable(img);
        sliderPage.setBgColor(Color.parseColor(bg_c));
        return sliderPage;
    }
}
