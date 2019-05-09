package com.jungh0.w3w_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pepperonas.materialdialog.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.jungh0.w3w_map.Collection.ToastMD;

public class MapsActivity extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mGoogleMap = null;
    private static final String TAG = "w3w_";
    String w3w_apikey = "CD39RHMH";

    double last_long , last_lati;
    TextView word_3, country, nearest, longitude_t, latitude_t;
    ViewGroup rootView;

    static double get_long , get_lati, is_geting = 0, is_geting2 = 0, is_get_auto = -1 ,is_seting = 0;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        rootView = (ViewGroup) inflater.inflate(R.layout.activity_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Collection.init_network();

        RelativeLayout card_up = (RelativeLayout) rootView.findViewById(R.id.card_up);

        LayoutInflater inflater2 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater2.inflate(R.layout.relative_card_up, card_up, true);
        word_3 = (TextView) card_up.findViewById(R.id.w3w_t);
        country = (TextView) card_up.findViewById(R.id.nearestPlace_t);
        nearest = (TextView) card_up.findViewById(R.id.country_t);
        longitude_t = (TextView) card_up.findViewById(R.id.longitude_t);
        latitude_t = (TextView) card_up.findViewById(R.id.latitude_t);

        //슬라이드 업 패널 리스너
        final FrameLayout willgone = (FrameLayout) card_up.findViewById(R.id.willgone);
        willgone.setVisibility(View.GONE);

        final SlidingUpPanelLayout mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                String tmp = newState.toString();
                String tmp2 = previousState.toString();
                if (tmp.equals("DRAGGING")) {
                    if (tmp2.equals("EXPANDED")) {
                        willgone.setVisibility(View.GONE);
                    }
                } else if (tmp.equals("EXPANDED")) {
                    willgone.setVisibility(View.VISIBLE);
                } else if (tmp.equals("COLLAPSED")) {
                    willgone.setVisibility(View.GONE);
                }
            }
        });
        mLayout.setShadowHeight(0);
        mLayout.setOverlayed(true);

        FloatingActionButton copy_word = rootView.findViewById(R.id.fab2);
        copy_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collection.clip_copy(getActivity(),getContext(),word_3.getText().toString());
            }
        });

        fab_each(rootView,R.id.sms);
        fab_each(rootView,R.id.kakao);
        fab_each(rootView,R.id.hangout);

        //검색 엔터키 눌렀을 때
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.search.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            keyboard_search();
                            return true;
                        }
                        return false;
                    }
                });
            }
        }, 1000);


        final Handler mHandler = new Handler();
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        if (!Collection.isNetworkAvailable(getContext())) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastMD(getContext(), "인터넷 연결에 실패 했습니다.",3);
                                }
                            });
                        }
                    } catch (Exception ex) {
                        Collection.ToastMD(getContext(), "추적 오류", 3);
                    }


                }
            }
        });
        t.start();

        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        if (is_get_auto == 1){
                            startLocationUpdates(true);
                        }
                        if (is_geting == 1){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (is_geting2 == 0){
                                        moveMap(mGoogleMap, get_lati, get_long,15);
                                        is_geting2 = 1;
                                    }else if (is_geting2 == 2){
                                        moveMap(mGoogleMap, get_lati, get_long,15);
                                    }
                                    markerMap(mGoogleMap, get_lati, get_long);
                                }
                            });
                        }
                    } catch (Exception ex) {
                        Collection.ToastMD(getContext(), "추적 오류", 3);
                    }


                }
            }
        });
        t2.start();

        return rootView;
    }

    public void fab_each(ViewGroup aa, int find_id){
        FloatingActionButton tmp = aa.findViewById(find_id);
        tmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collection.share_sns(getActivity(),word_3.getText().toString());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mGoogleMap = googleMap;

        //위치 권한
        int permission = Collection.location_permission(getContext(), getActivity());
        if (permission == 1) {
            startLocationUpdates(true);
        } else {
            setDefaultLocation();
        }

        //터치 했을 때 핀 생김
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (is_geting == 0 && is_get_auto != 1){
                    markerMap(mGoogleMap, point.latitude, point.longitude);
                }else{
                    Collection.ToastMD(getContext(), "위치 공유중에는 핀을 수정 할 수 없습니다.", 4);
                }
            }
        });

        //현재 위치 버튼
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_geting == 0 && is_get_auto != 1){
                    startLocationUpdates(true);
                }else {
                    Collection.ToastMD(getContext(), "위치 공유중에는 핀을 수정 할 수 없습니다.", 4);
                }
            }
        });

        //확대
        FloatingActionButton up = rootView.findViewById(R.id.up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_get_auto != 1){
                    Float zoom = Float.parseFloat(mGoogleMap.getCameraPosition().toString().split("zoom=")[1].split(",")[0]) + 1;
                    moveMap(mGoogleMap, last_lati, last_long,zoom);
                }else{
                    Collection.ToastMD(getContext(), "위치 공유중에는 움직일 수 없습니다.", 4);
                }
            }
        });

        //축소
        FloatingActionButton down = rootView.findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_get_auto != 1){
                    Float zoom = Float.parseFloat(mGoogleMap.getCameraPosition().toString().split("zoom=")[1].split(",")[0]) - 1;
                    moveMap(mGoogleMap, last_lati, last_long,zoom);
                }else{
                    Collection.ToastMD(getContext(), "위치 공유중에는 움직일 수 없습니다.", 4);
                }

            }
        });
    }

    //인터넷 연결 없을 시 기본 서울로 지정
    public void setDefaultLocation() {
        markerMap(mGoogleMap, 37.450989, 127.127156);
        moveMap(mGoogleMap, 37.450989, 127.127156,15);
    }

    //사용자 현재 위치
    private void startLocationUpdates(final boolean move) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastMD(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", 3);
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    markerMap(mGoogleMap, latitude, longitude);
                    if (move)
                        moveMap(mGoogleMap, latitude, longitude,15);
                } else {
                    ToastMD(getContext(), "현재위치를 찾을 수 없습니다.", 3);
                    setDefaultLocation();
                }
            }
        });
    }

    //좌표로 카메라 이동
    public void moveMap(GoogleMap gMap, double latitude, double longitude, float size) {
        Log.v(TAG, "mapMoved: " + gMap);
        LatLng latlng = new LatLng(latitude, longitude);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlng, size);
        gMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pin",100,100))));
        gMap.moveCamera(cu);
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //좌표에 핀 찍기
    public void markerMap(GoogleMap gMap, final double latitude, final double longitude) {
        last_long = longitude;
        last_lati = latitude;
        Log.v(TAG, "mapMarked: " + gMap);
        MarkerOptions mOptions = new MarkerOptions();
        //mOptions.title("마커 좌표");//mOptions.snippet(latitude + ", " + longitude);
        mOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pin",100,100)));
        mOptions.position(new LatLng(latitude, longitude));

        mGoogleMap.clear();
        mGoogleMap.addMarker(mOptions);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                word_3.setText("");
                String get_w3w = Collection.gethttp(getContext(), "https://api.what3words.com/v3/convert-to-3wa?coordinates=" + latitude + "%2C" + longitude + "&key=" + w3w_apikey + "&language=ko");
                try {
                    String word_parse = get_w3w.split("\"words\":\"")[1].split("\"")[0];
                    word_3.setText(word_parse);
                    word_parse = get_w3w.split("\"country\":\"")[1].split("\"")[0];
                    country.setText(word_parse);
                    word_parse = get_w3w.split("\"nearestPlace\":\"")[1].split("\"")[0];
                    nearest.setText(word_parse);
                    word_parse = get_w3w.split("\"lng\":")[3].split(",")[0];
                    longitude_t.setText(word_parse);
                    word_parse = get_w3w.split("\"lat\":")[3].split("\\}")[0];
                    latitude_t.setText(word_parse);
                } catch (Exception e) {
                    word_3.setText("API 오류");
                }
            }
        }, 0);
        if (is_seting == 1){
            Collection.gethttp(getContext(),"http://thousand419.dothome.co.kr/InsertNew.php?id="
                    + MainActivity.set__id + "&x=" + longitude + "&y=" + latitude);
        }
    }

    public void keyboard_search() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(MainActivity.search, 0);
        imm.hideSoftInputFromWindow(MainActivity.search.getWindowToken(), 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String get_w3w = Collection.gethttp(getContext(), "https://api.what3words.com/v3/autosuggest?key=" + w3w_apikey + "&input=" + MainActivity.search.getText().toString() + "&n-results=5");
                try {
                    String[] get_s = get_w3w.split("\"country\":\"");
                    int cnt = get_s.length;
                    String[] get_w = new String[cnt - 2];
                    String[] get_d = new String[cnt - 2];
                    for (int i = 0; i < cnt - 2; i++) {
                        get_w[i] = get_s[i + 1].split("\"words\":\"")[1].split("\"")[0];
                        get_d[i] = get_w[i] + " - " + get_s[i + 1].split("\"")[0];
                        get_d[i] = get_d[i] + " - " + get_s[i + 1].split("\"nearestPlace\":\"")[1].split("\"")[0];
                    }
                    showMaterialDialogList(get_w, get_d);
                } catch (Exception e) {
                    ToastMD(getContext(), "오류", 3);
                }
            }
        }, 0);
    }


    private void showMaterialDialogList(final String[] list, final String[] list2) {
        new MaterialDialog.Builder(getActivity())
                .title("선택하세요")
                .negativeText("취소")
                .negativeColor(R.color.pink_500)
                .listItems(true, list2)
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
                        //ToastMD(getApplicationContext(), "onClick (" + list[position] + ")",3);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String get_w3w = Collection.gethttp(getContext(), "https://api.what3words.com/v3/convert-to-coordinates?key=" + w3w_apikey + "&words=" + list[position] + "&format=json");
                                try {
                                    //ToastMD(getApplicationContext(),get_w3w,3);
                                    Double longitude = Double.parseDouble(get_w3w.split("\"lng\":")[3].split(",")[0]);
                                    Double latitude = Double.parseDouble(get_w3w.split("\"lat\":")[3].split("\\}")[0]);
                                    //ToastMD(getApplicationContext(), Double.toString(longitude),3);
                                    markerMap(mGoogleMap, latitude, longitude);
                                    moveMap(mGoogleMap, latitude, longitude,15);
                                } catch (Exception e) {
                                    ToastMD(getContext(), "오류", 3);
                                }
                            }
                        }, 0);

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

}
