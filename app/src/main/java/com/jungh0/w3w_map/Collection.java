package com.jungh0.w3w_map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

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

    public static  void init_network(){
        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static String gethttp(Context context, String sstr1) {
        if (isNetworkAvailable(context)) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet post = new HttpGet();
                post.setURI(new URI(sstr1));
                HttpResponse resp = client.execute(post);
                BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(),"utf8"));
                String str = null;
                StringBuilder sb = new StringBuilder();
                while ((str = br.readLine()) != null) {
                    sb.append(str).append("\n");
                }
                br.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            ToastMD(context, "인터넷 연결에 실패 했습니다.",3);
        }
        return "";
    }

    public static void ToastMD(Context context, String str,int num) {
        //1느낌표 2성공 3경고 4실패
        MDToast mdToast = MDToast.makeText(context, str, Toast.LENGTH_LONG,num);
        mdToast.show();
    }

    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String split_(String m, String s1 ,int c1 ,String s2, int c2) {
            return m.split(s1)[c1].split(s2)[c2];
    }

}
