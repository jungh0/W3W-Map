package com.jungh0.w3w_map.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jungh0.w3w_map.R;
import com.jungh0.w3w_map.model.Fireuser;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.LicenseInfo;
import com.raycoarana.codeinputview.CodeInputView;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainPresenter implements MainContract.Presenter{

    private MainContract.View mView;
    private Intent intent;
    private Activity act;

    public MainPresenter (MainContract.View view, Intent inten, Activity ac){
        mView = view;
        intent = inten;
        act = ac;
    }

    @Override
    public List<LicenseInfo> getLicenseInfos() {
        List<LicenseInfo> licenseInfos = new ArrayList<>();
        String Apache = mView.getresource(R.string.Apache);
        licenseInfos.add(new LicenseInfo("AndroidSlidingUpPanel", "https://github.com/umano/AndroidSlidingUpPanel",Apache));
        licenseInfos.add(new LicenseInfo("MaterialDesign-Toast", "https://github.com/pepperonas/MaterialDialog",Apache));
        licenseInfos.add(new LicenseInfo("MaterialDialog", "https://github.com/pepperonas/MaterialDialog",Apache));
        licenseInfos.add(new LicenseInfo("AppIntro", "https://github.com/AppIntro/AppIntro",Apache));
        licenseInfos.add(new LicenseInfo("CookieBar2", "https://github.com/AviranAbady/CookieBar2",Apache));
        licenseInfos.add(new LicenseInfo("material-code-input", "https://github.com/raycoarana/material-code-input",Apache));
        licenseInfos.add(new LicenseInfo("lottie", "https://github.com/airbnb/lottie-android",Apache));
        licenseInfos.add(new LicenseInfo("CircleImageView", "https://github.com/hdodenhof/CircleImageView",Apache));
        return licenseInfos;
    }

    @Override
    public void check_deeplink() {
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            try{
                CookieBar.dismiss(MainActivity.main_act);
            }catch (Exception e){

            }
            MapsActivity.is_seting = false;
            String uri = intent.getDataString();
            //mView.showMaterialDialog("dd",uri);
            uri = uri.replace("way_w3w://start/","").replace("/","").replace("?","");
            this.makeMaterialDialogSearch(uri);
        }
    }

    @Override
    public void makeMaterialDialogSearch(final String s_data) {
        mView.showMaterialDialogSearch(new MaterialDialog.ShowListener() {
            @Override
            public void onShow(final AlertDialog d) {
                super.onShow(d);
                final CodeInputView info = (CodeInputView) d.findViewById(R.id.editText);
                if (s_data != null)
                    info.setCode(s_data);
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

                        mView.startService(codee,first_move);
                        mView.make_cookie("위치 추적중..","상대방의 위치가 실시간으로 표시됩니다.","위치 추적 취소", new OnActionClickListener() {
                            @Override
                            public void onClick() {
                                mView.stopService();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void makeMaterialDialogShare() {
        mView.showMaterialDialogShare(new MaterialDialog.ShowListener() {
            @Override
            public void onShow(final AlertDialog d) {
                super.onShow(d);
                CodeInputView info = (CodeInputView) d.findViewById(R.id.editText);
                String tmp = Fireuser.get_database();
                int p;
                if (tmp == null){
                    Random rnd = new Random();
                    p = rnd.nextInt(899999);
                    p = p + 100000;
                }else{
                    int num = Integer.parseInt(tmp.split("phone=")[1].split(",")[0]);
                    p = num % 999999;
                }
                final String ss = Integer.toString(p);
                info.setCode(ss);

                Button start = (Button) d.findViewById(R.id.start) ;
                start.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        makeSelectMethod(ss);
                    }
                });
            }
        });
    }

    private void makeSelectMethod(final String ss) {
        mView.showSelectMethod(new MaterialDialog.ItemClickListener() {
            @Override
            public void onClick(View v, int position, long id) {
                super.onClick(v, position, id);
                MapsActivity.is_get_auto = position;
                MainActivity.set__id = ss;
                MapsActivity.is_seting = true;

                mView.make_cookie("위치 공유중..","터치/현재 위치가 실시간으로 공유됩니다.","위치 공유 취소",
                        new OnActionClickListener() {
                            @Override
                            public void onClick() {
                                MapsActivity.is_seting = false;
                                MapsActivity.is_get_auto = -1;
                                mView.showMaterialDialog("","위치 공유가 취소되었습니다.");
                            }
                        });
            }
        });
    }

    @Override
    public void login_() {
        mView.close_drawer();
        String tmp = Fireuser.get_database();
        if (tmp == null){
            make_logindialog();
        }else{
            mView.showMaterialDialog_listen("로그아웃","로그아웃을 하시겠습니까?",new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    Fireuser.logout();
                    logout_ui();
                    mView.showMaterialDialog("","로그아웃 되었습니다.");
                }
            });
        }
    }

    private void make_logindialog(){
        mView.showMaterialDialogLogin(new MaterialDialog.ShowListener() {
            @Override
            public void onShow(final AlertDialog d) {
                super.onShow(d);
                final TextView resi = d.findViewById(R.id.resi);
                resi.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        mView.go_intent();
                    }
                });
                final EditText id = d.findViewById(R.id.id_edit);
                final EditText pass = d.findViewById(R.id.pass_edit);
                Button start = (Button) d.findViewById(R.id.go_login) ;
                start.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        login_id_pass(id.getText().toString(),pass.getText().toString());

                    }
                });
            }
        });
    }

    private void make_loading_dialog(final Fireuser user){
        mView.showMaterialDialogLoading(new Dialog.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Handler mHandler = new Handler();
                Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        String tmp = null;
                        while (tmp == null) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            tmp = user.get_database();
                        }
                        final String r_tmp = tmp;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                update_user(r_tmp);
                            }
                        });
                    }
                });
                t.start();
            }

        });
    }

    private void login_id_pass(String id ,String pass){
        final Fireuser user = new Fireuser(act,id, pass);
        make_loading_dialog(user);
    }

    private void update_user(String data){
        if (!data.equals(new String("error"))){
            String bitstr = data.split("photo=")[1].split(",")[0];
            String nick = data.split("nickname=")[1].split(",")[0];
            mView.change_login_info(strTobit(bitstr),nick);
            mView.open_drawer();
            mView.ToastMD("로그인 성공",1);
        }else{
            Fireuser.logout();
            mView.open_drawer();
            mView.ToastMD("로그인 실패",3);
        }
    }

    private Bitmap strTobit(String str){
        byte[] decodedByteArray = Base64.decode(str, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

    private void logout_ui(){
        mView.change_login_info(null,"로그인해주세요.");
    }

    public void makemyinfo(){
        mView.close_drawer();
        String tmp = Fireuser.get_database();
        if (tmp == null){
            //mView.showMaterialDialog("","로그인이 필요합니다.");
            make_logindialog();
        }else{
            try{
                List<String> data = new ArrayList<String>();
                data.add("닉네임 : " + tmp.split("nickname=")[1].split(",")[0]);
                data.add("학교 : " + tmp.split("university=")[1].split(",")[0]);
                data.add("학과 : " + tmp.split("department=")[1].split(",")[0]);
                data.add("이메일 : " + tmp.split("email=")[1].split(",")[0]);
                data.add("휴대폰 번호 : " + tmp.split("phone=")[1].split(",")[0]);
                int num = Integer.parseInt(tmp.split("phone=")[1].split(",")[0]);
                num = num % 999999;
                data.add("WAY 고유 번호 : " + String.valueOf(num));

                String[] resultArray = new String[data.size()];
                resultArray = data.toArray(resultArray);
                mView.showMaterialDialogList(resultArray);
            }catch (Exception e){
                mView.showMaterialDialog("","오류발생");
            }
        }
    }

    public void makefriend(){
        mView.close_drawer();
        String tmp = Fireuser.get_database();
        if (tmp == null){
            //mView.showMaterialDialog("","로그인이 필요합니다.");
            make_logindialog();
        }else{
            try{
                //mView.showMaterialDialog("",tmp);

                String friend_str = tmp.split("friends=")[1];
                String[] friend = friend_str.split("gender=");

                List<String> data = new ArrayList<String>();
                for (int i = 1 ;i < friend.length;i++){
                    String nick = friend[i].split("nickname=")[1].split(",")[0];
                    String num = friend[i].split("phone=")[1].split(",")[0];
                    data.add(nick + " - " + num);
                    //data.add(friend[i]);
                }

                String[] resultArray = new String[data.size()];
                resultArray = data.toArray(resultArray);
                final String[] to_fin = resultArray;
                mView.showMaterialDialogList_friend(resultArray, new MaterialDialog.ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, long id) {
                        super.onClick(v, position, id);

                        String phone = to_fin[position].split(" - ")[1];
                        int num = Integer.parseInt(phone);

                        makeMaterialDialogSearch( Integer.toString(num % 999999));
                        //mView.showMaterialDialog("",to_fin[position]);
                    }
                });
            }catch (Exception e){
                mView.showMaterialDialog("","친구가 없습니다.");
            }
        }
    }
}
