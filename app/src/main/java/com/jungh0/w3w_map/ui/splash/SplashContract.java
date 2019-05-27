package com.jungh0.w3w_map.ui.splash;

public interface SplashContract {

    interface View{

        void move_map();

        void move_intro();

    }

    interface Presenter{

        void check_start();

    }

}
