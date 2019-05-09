package com.jungh0.w3w_map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.aviran.cookiebar2.CookieBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import static com.jungh0.w3w_map.MapsActivity.get_long;
import static com.jungh0.w3w_map.MapsActivity.get_lati;


//광천 추가 클래스
public class GetLocationService extends Service {

    Handler mHandler = null;
    static Boolean switch_ = true;

    public GetLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       if(intent == null) {
           return Service.START_STICKY;
       }
       else {
           processCommand(intent);
       }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void processCommand (Intent intent) {
        switch_ = true;
        final String id = intent.getStringExtra("id");

        mHandler = new Handler();

        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                main_r(id);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (switch_) {
                    try {
                        main_r(id);
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        Collection.ToastMD(getBaseContext(), "오류", 1);
                    }
                }
            }
        });
        t.start();

    }

    private void main_r(String str){
        try{
            final String get = Collection.gethttp(getBaseContext(),"http://thousand419.dothome.co.kr/get2.php?id=" + str);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Collection.ToastMD(getBaseContext(), get, 1);
                    if (get.length() > 3 && !get.contains("Unknown")){
                        get_long = Double.parseDouble(get.split(" ")[0]);
                        get_lati = Double.parseDouble(get.split(" ")[1]);
                    }else{
                        switch_ = false;
                        MapsActivity.is_geting = 0;
                        MapsActivity.is_geting2 = 0;
                        Collection.ToastMD(getBaseContext(), "정보가 없습니다. 공유를 종료합니다.", 4);
                        CookieBar.dismiss(MainActivity.main_act);
                        stopSelf();
                    }
                }
            });


        }catch (Exception e){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch_ = false;
                    MapsActivity.is_geting = 0;
                    MapsActivity.is_geting2 = 0;
                    Collection.ToastMD(getBaseContext(), "정보가 없습니다. 공유를 종료합니다.", 4);
                    CookieBar.dismiss(MainActivity.main_act);
                    stopSelf();
                }
            });
        }


    }

}
