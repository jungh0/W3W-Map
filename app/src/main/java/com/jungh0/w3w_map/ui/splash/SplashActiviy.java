package com.jungh0.w3w_map.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.jungh0.w3w_map.ui.main.MainActivity;
import com.jungh0.w3w_map.R;
import com.jungh0.w3w_map.ui.intro.IntroActivity;

public class SplashActiviy extends AppCompatActivity implements SplashContract.View {

    private SplashContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activiy);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        mPresenter = new SplashPresenter(this,pref);
        mPresenter.check_start();
    }

    @Override
    public void move_map() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void move_intro() {
        startActivity(new Intent(this, MainActivity.class));
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }
}
