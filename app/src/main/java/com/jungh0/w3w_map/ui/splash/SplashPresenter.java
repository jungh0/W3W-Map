package com.jungh0.w3w_map.ui.splash;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;

public class SplashPresenter implements SplashContract.Presenter{

    SplashContract.View mView;
    SharedPreferences Pref;

    public SplashPresenter(SplashContract.View view, SharedPreferences pref){
        mView = view;
        Pref = pref;
    }

    @Override
    public void check_start() {
        boolean first = Pref.getBoolean("isFirst", false);
        if(first==false){
            SharedPreferences.Editor editor = Pref.edit();
            editor.putBoolean("isFirst",true);
            editor.commit();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try{
                    move_intro();
                }catch (Exception e){
                    move_map();
                }
            }
        }else{
            move_map();
        }
    }

    private void move_map(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.move_map();
            }
        },1200);
    }

    private void move_intro(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.move_intro();
            }
        },1200);
    }
}
