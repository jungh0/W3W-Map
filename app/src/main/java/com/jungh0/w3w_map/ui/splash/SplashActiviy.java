package com.jungh0.w3w_map.ui.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
