package com.jungh0.w3w_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.Task;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.LicenseInfo;
import com.raycoarana.codeinputview.CodeInputView;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import static com.jungh0.w3w_map.Collection.ToastMD;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static EditText search;
    static String deeplink = "";
    static Activity main_act;
    Context main_cont;
    static String set__id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search = (EditText) toolbar.findViewById(R.id.search);

        getSupportActionBar().setElevation(0);

        main_act = MainActivity.this;
        main_cont = getApplicationContext();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        if(first==false){
           // Log.d("Is first Time?", "first");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",true);
            editor.commit();
            startActivity(new Intent(this, IntroActivity.class));
        }else{
            //Log.d("Is first Time?", "not first");
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
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("email", "iveinvalue@gmail.com");
            clipboardManager.setPrimaryClip(clipData);
            showMaterialDialog("개발자 이메일","클립보드에 복사 되었습니다.");
        } else if (id == R.id.version) {
            showMaterialDialog("버전 정보","1.0.1(23)");
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
                        Button start = (Button) d.findViewById(R.id.start) ;
                        final CheckBox auto_move = (CheckBox) d.findViewById(R.id.checkBox);
                        start.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                d.dismiss();
                                if (auto_move.isChecked()){
                                    MapsActivity.is_geting2 = 2;
                                }
                                String codee = info.getCode();
                                //showMaterialDialog("",codee);
                                final Intent  intent = new Intent(getApplication(),GetLocationService.class);
                                intent.putExtra("id",codee); //여기선 1로함
                                startService(intent);
                                MapsActivity.is_geting = 1;
                                CookieBar.build(MainActivity.this)
                                        .setTitle("위치 추적중..")
                                        .setMessage("상대방의 위치가 실시간으로 표시됩니다.")
                                        .setCookiePosition(CookieBar.TOP)
                                        .setEnableAutoDismiss(false)
                                        .setSwipeToDismiss(false)
                                        .setBackgroundColor(R.color.search)
                                        .setAction("위치 추적 취소", new OnActionClickListener() {
                                            @Override
                                            public void onClick() {
                                                MapsActivity.is_geting = 0;
                                                MapsActivity.is_geting2 = 0;
                                                stopService(intent);
                                                GetLocationService.switch_ = false;
                                                showMaterialDialog("","위치 추척이 취소되었습니다.");
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                })
                .show();
    }

    private String[] ITEMS = new String[]{"터치 한 위치 공유 (기본)", "현재 위치 자동 공유 (조작이 제한 됩니다.)"};

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
                        MapsActivity.is_seting = 1;

                        CookieBar.build(MainActivity.this)
                                .setTitle("위치 공유중.. (공유 코드 : " + ss + ")")
                                .setMessage("터치하는 위치 / 현재위치 가 실시간으로 공유됩니다.")
                                .setCookiePosition(CookieBar.TOP)
                                .setEnableAutoDismiss(false)
                                .setSwipeToDismiss(false)
                                .setBackgroundColor(R.color.share)
                                .setAction("위치 공유 취소", new OnActionClickListener() {
                                    @Override
                                    public void onClick() {
                                        MapsActivity.is_seting = 0;
                                        MapsActivity.is_get_auto = -1;
                                        showMaterialDialog("","위치 공유가 취소되었습니다.");
                                    }
                                })
                                .show();

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
                "https://github.com/umano/AndroidSlidingUpPanel",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "MaterialDesign-Toast",
                "https://github.com/pepperonas/MaterialDialog",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "MaterialDialog",
                "https://github.com/pepperonas/MaterialDialog",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "AppIntro",
                "https://github.com/AppIntro/AppIntro",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "CookieBar2",
                "CookieBar2",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "CookieBar2",
                "https://github.com/AviranAbady/CookieBar2",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));

        licenseInfos.add(new LicenseInfo(
                "material-code-input",
                "https://github.com/raycoarana/material-code-input",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License."));


        return licenseInfos;
    }
}
