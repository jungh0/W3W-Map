package com.jungh0.w3w_map;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import org.aviran.cookiebar2.CookieBar;


//광천 추가 클래스
public class GetLocationService extends Service {

    static boolean is_geting = false; // 위치 추적할 때 온오프
    static int get_location_first_move = 0; //위치 추적할 때 처음 한번은 카메라 이동하고 나중에 안함 - 0은 한번만 1은 안함 2는 계속
    static double get_long, get_lati;
    Handler mHandler = null;
    //static Boolean switch_ = true;
    Thread t = null;

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
           return Service.START_NOT_STICKY;
       }
       else {
           processCommand(intent);
       }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exit();
    }

    private void processCommand (Intent intent) {
        //switch_ = true;
        is_geting = true;
        int first_move = intent.getIntExtra("first_move",0);
        get_location_first_move = first_move;
        final String id = intent.getStringExtra("id");

        mHandler = new Handler();
        t = new Thread(new Runnable(){
            @Override
            public void run() {
                main_r(id);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        main_r(id);
                        Thread.sleep(1000);
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
                    if (get.length() > 3 && !get.contains("Unknown") && !get.contains("error")){
                        get_long = Double.parseDouble(get.split(" ")[0]);
                        get_lati = Double.parseDouble(get.split(" ")[1]);
                    }else{
                        exit();
                    }
                }
            });
        }catch (Exception e){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    exit();
                }
            });
        }
    }

    private void exit(){
        t.interrupt();
        //switch_ = false;
        is_geting = false;
        get_location_first_move = 0;
        Collection.ToastMD(getBaseContext(), "정보가 없습니다. 공유를 종료합니다.", 4);
        CookieBar.dismiss(MainActivity.main_act);
        stopSelf();
    }
}
