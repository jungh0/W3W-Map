package com.jungh0.w3w_map.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.jungh0.w3w_map.R;
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

    public MainPresenter (MainContract.View view, Intent inten){
        mView = view;
        intent = inten;
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
        return licenseInfos;
    }

    @Override
    public void check_deeplink() {
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            CookieBar.dismiss(MainActivity.main_act);
            MapsActivity.is_seting = false;
            String uri = intent.getDataString();
            //showMaterialDialog("개발자 이메일",uri);
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


}
