package com.jungh0.w3w_map.ui.intro;

import com.github.paolorotolo.appintro.model.SliderPage;

public interface IntroContract {

    interface View{

        void show(SliderPage page);

        void intro_config();

    }

    interface Presenter{

        void make();

    }
}
