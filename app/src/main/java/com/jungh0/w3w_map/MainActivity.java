package com.jungh0.w3w_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.LicenseInfo;
import com.raycoarana.codeinputview.CodeInputView;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static EditText search;
    static Activity main_act;
    static String set__id = "";

    Context main_cont;
    String s_data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        search = (EditText) toolbar.findViewById(R.id.search);
        main_act = MainActivity.this;
        main_cont = getApplicationContext();

        check_star();
        check_deeplink();
    }

    public void check_star(){
        //첫 실행 튜토리얼
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        if(first==false){
            // Log.d("Is first Time?", "first");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",true);
            editor.commit();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try{
                    startActivity(new Intent(this, IntroActivity.class));
                }catch (Exception e){
                }
            }
        }else{
            //Log.d("Is first Time?", "not first");
        }
    }

    public void check_deeplink(){
        //딥링크
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            //showMaterialDialog("개발자 이메일",uri);
            uri = uri.replace("way_w3w://start/","").replace("/","").replace("?","");
            s_data = uri;
            showMaterialDialogSearch();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.opensource) {
            showMaterialDialogLicenseInfo();
        } else if (id == R.id.contact) {
            Collection.clip_copy(this, getApplicationContext(), "iveinvalue@gmail.com");
            showMaterialDialog("개발자 이메일","클립보드에 복사 되었습니다.");
        } else if (id == R.id.version) {
            showMaterialDialog("버전 정보","1.0.4(26)");
        } else if (id == R.id.intro) {
            startActivity(new Intent(this, IntroActivity.class));
        } else if (id == R.id.location_share) {
            showMaterialDialogShare();
        } else if (id == R.id.location_search) {
            showMaterialDialogSearch();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMaterialDialogSearch() {
        new MaterialDialog.Builder(this)
                .title("위치 추적을 시작합니다.")
                .customView(R.layout.search_dialog)
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(final AlertDialog d) {
                        super.onShow(d);
                        final CodeInputView info = (CodeInputView) d.findViewById(R.id.editText);
                        if (s_data != null){
                            info.setCode(s_data);
                            s_data = null;
                        }
                        Button start = (Button) d.findViewById(R.id.start) ;
                        final CheckBox auto_move = (CheckBox) d.findViewById(R.id.checkBox);
                        start.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                d.dismiss();
                                int first_move;
                                if (auto_move.isChecked())
                                    first_move = 2;
                                else
                                    first_move = 0;
                                String codee = info.getCode();

                                final Intent intent = new Intent(getApplication(),GetLocationService.class);
                                intent.putExtra("id",codee);
                                intent.putExtra("first_move",first_move); //여기선 1로함
                                startService(intent);

                                make_cookie("위치 추적중..","상대방의 위치가 실시간으로 표시됩니다.","위치 추적 취소",
                                        new OnActionClickListener() {
                                    @Override
                                    public void onClick() {
                                        stopService(intent);
                                        showMaterialDialog("","위치 추척이 취소되었습니다.");
                                    }
                                });
                            }
                        });
                    }
                })
                .show();
    }

    public void make_cookie(String title, String message,String cancel_str,OnActionClickListener onActionClickListener){
        CookieBar.build(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setCookiePosition(CookieBar.TOP)
                .setEnableAutoDismiss(false)
                .setSwipeToDismiss(false)
                .setBackgroundColor(R.color.search)
                .setAction(cancel_str, onActionClickListener)
                .show();
    }

    private String[] ITEMS = new String[]{"터치 위치 수동 전송 (기본)", "현재 위치 자동 전송 (조작이 제한 됩니다.)"};
    private void showMaterialDialogShare() {
        new MaterialDialog.Builder(this)
                .title("위치 공유를 시작합니다.")
                .customView(R.layout.share_dialog)
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(final AlertDialog d) {
                        super.onShow(d);
                        CodeInputView info = (CodeInputView) d.findViewById(R.id.editText);
                        Random rnd = new Random();
                        int p = rnd.nextInt(899999);
                        p = p + 100000;
                        final String ss = Integer.toString(p);
                        info.setCode(ss);

                        Button start = (Button) d.findViewById(R.id.start) ;
                        start.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                d.dismiss();
                                show___(ss);
                            }
                        });
                    }
                })
                .show();
    }

    private void show___(final String ss){
        new MaterialDialog.Builder(this)
                .title("공유방식을 선택해 주세요.")
                .negativeText("취소")
                .negativeColor(R.color.red)
                .listItems(true, ITEMS)
                .itemClickListener(new MaterialDialog.ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, long id) {
                        super.onClick(v, position, id);
                        //showToast("onClick (" + ITEMS[position] + ")");
                        MapsActivity.is_get_auto = position;
                        set__id = ss;
                        MapsActivity.is_seting = true;

                        make_cookie("위치 공유중..","터치/현재 위치가 실시간으로 공유됩니다.","위치 공유 취소",
                                new OnActionClickListener() {
                                    @Override
                                    public void onClick() {
                                        MapsActivity.is_seting = false;
                                        MapsActivity.is_get_auto = -1;
                                        showMaterialDialog("","위치 공유가 취소되었습니다.");
                                    }
                                });
                    }
                })
                .show();
    }

    private void showMaterialDialog(String str,String str2) {
        new MaterialDialog.Builder(this)
                .title(str)
                .message(str2)
                .positiveText("확인")
                .positiveColor(R.color.red)
                .show();
    }

    private void showMaterialDialogLicenseInfo() {
        List<LicenseInfo> licenseInfos = getLicenseInfos();
        new MaterialDialog.Builder(this)
                .title("라이센스 정보")
                .licenseDialog(licenseInfos)
                .positiveText("확인")
                .show();
    }

    @NonNull
    private List<LicenseInfo> getLicenseInfos() {
        List<LicenseInfo> licenseInfos = new ArrayList<>();
        licenseInfos.add(new LicenseInfo(
                "AndroidSlidingUpPanel",
                "https://github.com/umano/AndroidSlidingUpPanel",getString(R.string.Apache)));
        licenseInfos.add(new LicenseInfo(
                "MaterialDesign-Toast",
                "https://github.com/pepperonas/MaterialDialog",getString(R.string.Apache)));
        licenseInfos.add(new LicenseInfo(
                "MaterialDialog",
                "https://github.com/pepperonas/MaterialDialog",getString(R.string.Apache)));
        licenseInfos.add(new LicenseInfo(
                "AppIntro",
                "https://github.com/AppIntro/AppIntro",getString(R.string.Apache)));
        licenseInfos.add(new LicenseInfo(
                "CookieBar2",
                "https://github.com/AviranAbady/CookieBar2",getString(R.string.Apache)));
        licenseInfos.add(new LicenseInfo(
                "material-code-input",
                "https://github.com/raycoarana/material-code-input", getString(R.string.Apache)));
        return licenseInfos;
    }
}
