package com.jungh0.w3w_map;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHttpURLConnection {

    public String request(String _url) {
        try{
            URL Url = new URL(_url); // URL화 한다.
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); // URL을 연결한 객체 생성.
            conn.setRequestMethod("GET"); // get방식 통신
            conn.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
            HttpURLConnection.setFollowRedirects(true);

            conn.setDoOutput(true); // 쓰기모드 지정
            conn.setDoInput(true); // 읽기모드 지정
            conn.setUseCaches(false); // 캐싱데이터를 받을지 안받을지
            conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정
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
        return null;

    }

    /*
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String get_w3w) {
            super.onPostExecute(get_w3w);
        }
    }*/
}