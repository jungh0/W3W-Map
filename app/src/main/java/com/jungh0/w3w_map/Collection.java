package com.jungh0.w3w_map;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Collection  {

    public static int location_permission(Context context, Activity activity){
        String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        int PERMISSIONS_REQUEST_CODE = 100;
        int result = 0;

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            result = 1;
        } else {
            result = 0;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, REQUIRED_PERMISSIONS[0])) {
                ToastMD(context, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",3);
                ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
        return result;
    }

    public static String gethttp(Context context, String sstr1) {
        if (isNetworkAvailable(context)) {
            try{
                URL Url = new URL(sstr1); // URL화 한다.
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); // URL을 연결한 객체 생성.
                conn.setRequestMethod("GET"); // get방식 통신
                conn.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
                HttpURLConnection.setFollowRedirects(true);
                //conn.setDoOutput(true); // 쓰기모드 지정
                //conn.setDoInput(true); // 읽기모드 지정
                //conn.setUseCaches(false); // 캐싱데이터를 받을지 안받을지
                //conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정
                //String strCookie = conn.getHeaderField("Set-Cookie"); //쿠키데이터 보관
                InputStream is = conn.getInputStream(); //input스트림 개방
                StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8")); //문자열 셋 세팅
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line+ "\n");
                }

                String result = builder.toString();
                return result;

            }catch(Exception e) {
            }
        }else{
            //ToastMD(context, "인터넷 연결에 실패 했습니다.",3);
        }
        return "";
    }

    public static void ToastMD(Context context, String str,int num) {
        //1느낌표 2성공 3경고 4실패
        try {
            MDToast mdToast = MDToast.makeText(context, str, Toast.LENGTH_SHORT,num);
            mdToast.show();
        }catch (Exception e){

        }
    }

    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String split_(String m, String s1 ,int c1 ,String s2, int c2) {
            return m.split(s1)[c1].split(s2)[c2];
    }

    public static void clip_copy(Activity act, Context con, String m) {
        ClipboardManager clipboard = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("WAY", m);
        clipboard.setPrimaryClip(clip);
        ToastMD(con, "(" + m + ") 클립보드에 복사되었습니다.", 1);
    }

    public static void share_sns(Activity act, String m) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subject = "WAY - w3w location sharing";
        String text = m;
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        act.startActivity(chooser);
    }

}
