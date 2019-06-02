package com.jungh0.w3w_map.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jungh0.w3w_map.R;
import com.jungh0.w3w_map.model.Fireuser;
import com.jungh0.w3w_map.ui.intro.IntroActivity;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.LicenseInfo;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private MainContract.Presenter mPresenter;

    static EditText search;
    static Activity main_act;
    static String set__id = "";

    Context main_cont;
    CircleImageView login_image;
    TextView login_text;
    Intent service_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mPresenter = new MainPresenter(this,this.getIntent(),MainActivity.this);
        mPresenter.check_deeplink();

        search = toolbar.findViewById(R.id.search);
        View nav_header_view = navigationView.getHeaderView(0);
        login_text = nav_header_view.findViewById(R.id.login_text);
        login_image = nav_header_view.findViewById(R.id.login_image);
        login_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login_();
            }
        });
        main_act = MainActivity.this;
        main_cont = getApplicationContext();
    }

    public void change_login_info(Bitmap bit,String txt){
        login_text.setText(txt);
        if (bit == null){
            Drawable d = getResources().getDrawable(R.drawable.user_login);
            login_image.setImageDrawable(d);
        }else{
            login_image.setImageBitmap(bit);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opensource) {
            this.showMaterialDialogLicenseInfo();
        } else if (id == R.id.contact) {
            this.clip_copy("iveinvalue@gmail.com");
        } else if (id == R.id.version) {
            this.showMaterialDialog("버전 정보","1.0.6(28)");
        } else if (id == R.id.intro) {
            startActivity(new Intent(this, IntroActivity.class));
        } else if (id == R.id.location_share) {
            mPresenter.makeMaterialDialogShare();
        } else if (id == R.id.location_search) {
            mPresenter.makeMaterialDialogSearch(null);
        } else if (id == R.id.myinfo) {
            mPresenter.makemyinfo();
        } else if (id == R.id.friend) {
            mPresenter.makefriend();
        }
        close_drawer();
        return true;
    }

    public void close_drawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void open_drawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void showMaterialDialogSearch(MaterialDialog.ShowListener listener){
        new MaterialDialog.Builder(this)
                .title("위치 추적을 시작합니다.")
                .customView(R.layout.search_dialog)
                .showListener(listener)
                .show();
    }

    @Override
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

    @Override
    public void startService(String codee,int first_move) {
        service_intent = new Intent(getApplication(), GetLocationService.class);
        service_intent.putExtra("id",codee);
        service_intent.putExtra("first_move",first_move); //여기선 1로함
        startService(service_intent);
    }

    @Override
    public void stopService() {
        stopService(service_intent);
        this.showMaterialDialog("","위치 추척이 취소되었습니다.");
    }

    @Override
    public void showMaterialDialogShare(MaterialDialog.ShowListener listener) {
        new MaterialDialog.Builder(this)
                .title("위치 공유를 시작합니다.")
                .customView(R.layout.share_dialog)
                .showListener(listener)
                .show();
    }

    @Override
    public void showMaterialDialogLogin(MaterialDialog.ShowListener listener) {
        new MaterialDialog.Builder(this)
                .title("로그인해주세요")
                .customView(R.layout.login_dialog)
                .showListener(listener)
                .show();
    }

    @Override
    public void showMaterialDialogLoading(Dialog.OnShowListener listener) {
        Dialog builder = new Dialog(this);
        builder.setTitle("Select The Difficulty Level");
        builder.setContentView(R.layout.loading_dialog);
        builder.setOnShowListener(listener);
        builder.setCancelable(false);
        builder.setCanceledOnTouchOutside(false);
        builder.setCancelable(false);
        builder.show();
    }

    private String[] ITEMS = new String[]{"터치 위치 수동 전송 (기본)", "현재 위치 자동 전송 (조작 제한)"};
    @Override
    public void showSelectMethod(MaterialDialog.ItemClickListener listener) {
        new MaterialDialog.Builder(this)
                .title("공유방식을 선택해 주세요.")
                .negativeText("취소")
                .negativeColor(R.color.red)
                .listItems(true, ITEMS)
                .itemClickListener(listener)
                .show();
    }

    @Override
    public void showMaterialDialog(String str, String str2) {
        new MaterialDialog.Builder(this)
                .title(str)
                .message(str2)
                .positiveText("확인")
                .positiveColor(R.color.red)
                .show();
    }

    @Override
    public void showMaterialDialog_listen(String str, String str2,MaterialDialog.ButtonCallback listener) {
        new MaterialDialog.Builder(this)
                .title(str)
                .message(str2)
                .positiveText("확인")
                .positiveColor(R.color.green)
                .buttonCallback(listener)
                .show();
    }

    @Override
    public void showMaterialDialogLicenseInfo() {
        List<LicenseInfo> licenseInfos = mPresenter.getLicenseInfos();
        new MaterialDialog.Builder(this)
                .title("라이센스 정보")
                .licenseDialog(licenseInfos)
                .positiveText("확인")
                .show();
    }

    @Override
    public String getresource(int rid) {
        return getResources().getString(rid);
    }

    @Override
    public void clip_copy(String m) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("WAY", m);
        clipboard.setPrimaryClip(clip);
        this.ToastMD("(" + m + ") 클립보드에 복사되었습니다.", 1);
        this.showMaterialDialog("개발자 이메일","클립보드에 복사 되었습니다.");
    }

    @Override
    public void ToastMD(String str,int num) {
        //1느낌표 2성공 3경고 4실패
        try {
            MDToast mdToast = MDToast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT,num);
            mdToast.show();
        }catch (Exception e){
        }
    }

    public void showMaterialDialogList(final String[] list) {
        new MaterialDialog.Builder(this)
                .title("계정 정보")
                .positiveText("닫기")
                .positiveColor(R.color.red)
                .listItems(true, list)
                .itemSelectedListener(new MaterialDialog.ItemSelectedListener() {
                    @Override
                    public void onSelected(View view, int position, long id) {
                        super.onSelected(view, position, id);
                    }
                })
                .itemClickListener(new MaterialDialog.ItemClickListener() {
                    @Override
                    public void onClick(View v, final int position, long id) {
                        super.onClick(v, position, id);

                    }
                })
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showMaterialDialogList_friend(final String[] list,MaterialDialog.ItemClickListener listenter) {
        new MaterialDialog.Builder(this)
                .title("친구 목록")
                .positiveText("닫기")
                .positiveColor(R.color.red)
                .listItems(true, list)
                .itemClickListener(listenter)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fireuser.logout();
        FirebaseAuth.getInstance().signOut();
    }

    public void go_intent(){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("helu://start"));
            startActivity(intent);
        }catch (Exception e){
            showMaterialDialog("회원가입","Helu 앱을 설치해주세요.");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.pale_cosmos.helu")));
        }

    }
}
