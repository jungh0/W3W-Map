package com.jungh0.w3w_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.wang.avi.AVLoadingIndicatorView;

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

import static com.jungh0.w3w_map.Collection.ToastMD;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mGoogleMap = null;
    private static final String TAG = "w3w_";
    private FusedLocationProviderClient fusedLocationClient;

    TextView word_3;
    AVLoadingIndicatorView loading;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_maps);

        //getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Collection.init_network();
        word_3 = (TextView) findViewById(R.id.word_three);
        loading = (AVLoadingIndicatorView) findViewById(R.id.load) ;
        loading.setIndicatorColor(Color.rgb(40,40,40));

        final SlidingUpPanelLayout mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    public int getStatusBarHeight()
    {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mGoogleMap = googleMap;

        //위치 권한
        int permission = Collection.location_permission(getApplicationContext(),this);
        if (permission == 1){
            startLocationUpdates();
        }else{
            setDefaultLocation();
        }

        //터치 했을 때 핀 생김
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                markerMap(mGoogleMap,point.latitude, point.longitude);
            }
        });

        //현재 위치 버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
            }
        });
    }

    //인터넷 연결 없을 시 기본 서울로 지정
    public void setDefaultLocation() {
        markerMap(mGoogleMap,37.56,126.97);
        moveMap(mGoogleMap,37.56,126.97);
    }

    //사용자 현재 위치
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastMD(getApplicationContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",3);
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    markerMap(mGoogleMap,latitude,longitude);
                    moveMap(mGoogleMap,latitude,longitude);
                }
            }
        });
    }

    //좌표로 카메라 이동
    public void moveMap(GoogleMap gMap, double latitude, double longitude) {
        Log.v(TAG, "mapMoved: " + gMap);
        LatLng latlng = new LatLng(latitude, longitude);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        gMap.addMarker(new MarkerOptions().position(latlng));
        gMap.moveCamera(cu);
    }

    //좌표에 핀 찍기
    public void markerMap(GoogleMap gMap, final double latitude, final double longitude) {
        Log.v(TAG, "mapMarked: " + gMap);
        MarkerOptions mOptions = new MarkerOptions();
        //mOptions.title("마커 좌표");//mOptions.snippet(latitude + ", " + longitude);
        mOptions.position(new LatLng(latitude, longitude));
        mGoogleMap.clear();
        mGoogleMap.addMarker(mOptions);

        word_3.setText("");
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String get_w3w = Collection.gethttp(getApplicationContext(),"https://api.what3words.com/v3/convert-to-3wa?coordinates=" + latitude + "%2C" + longitude + "&key=CD39RHMH&language=ko");
                try{
                    String word_parse = get_w3w.split("\"words\":\"")[1].split("\"")[0];
                    word_3.setText(word_parse);
                    loading.setVisibility(View.INVISIBLE);
                }catch(Exception e){
                    word_3.setText("인터넷 접속 실패");
                    loading.setVisibility(View.INVISIBLE);
                }
            }
        }, 1000);
    }


}
