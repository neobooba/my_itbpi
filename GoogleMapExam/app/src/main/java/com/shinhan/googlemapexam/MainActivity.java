package com.shinhan.googlemapexam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    SupportMapFragment mapFragment;
    GoogleMap map;

    class MyMarker { //마커 정보 저장용 클래스
        String name;
        LatLng latLng;

        MyMarker(String name, LatLng latLng) {
            this.name = name;
            this.latLng = latLng;
        }
    }

    MyMarker[] markers = {
            new MyMarker("일산 롯데백화점", new LatLng(37.6603567, 126.7702051)),
            new MyMarker("광화문 교보문고", new LatLng(37.5709792, 126.9755652)),
            new MyMarker("정동진", new LatLng(37.725881, 128.991237)),
            new MyMarker("제주공항", new LatLng(33.510418, 126.4891594)),
            new MyMarker("타지마할", new LatLng(31.0913669, 84.2817526)),
            new MyMarker("에펠탑", new LatLng(48.8583736, 2.2922873)),
            new MyMarker("타임스퀘어", new LatLng(40.7589705, -73.9937563)),
            new MyMarker("뉴욕 배터리공원", new LatLng(40.7032815, -74.0192219)),
            new MyMarker("시애틀 스타벅스", new LatLng(47.6115116, -122.3397523)),
            new MyMarker("헤이리 모티프원", new LatLng(37.7863138, 126.6959126))
    };
    int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap; //비동기방식으로 구글지도 객체 얻기
                //마커 출력
                PolylineOptions rectOptions = new PolylineOptions();
                rectOptions.color(Color.GREEN);
                for (int i = 0; i < markers.length; i++) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(markers[i].latLng);
                    marker.title(markers[i].name);
                    map.addMarker(marker);
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) { //마커 클릭시 확대
                            // Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                            for (int j = 0; j < markers.length; j++) {
                                if (markers[j].name.equals(marker.getTitle())) {
                                    currentPos = j + 1;
                                    if (currentPos >= markers.length)
                                        currentPos = 0;
                                    break;
                                }
                            }


                            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                            return false;
                        }
                    });

                    rectOptions.add(markers[i].latLng);
                }
                Polyline polyline = map.addPolyline(rectOptions);
            }
        });
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "GPS 연동 권한 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GPS 권한 승인!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS 권한 거부!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GPSListener implements LocationListener { //인터페이스 명까지 입력하고 alt+엔터

        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            TextView textView = (TextView) findViewById(R.id.location);
            textView.setText("내 위치:" + latitude + "," + longitude);
            Toast.makeText(MainActivity.this, "위도:" + latitude + ", 경도:" + longitude, Toast.LENGTH_SHORT).show();

            LatLng curPoint = new LatLng(latitude, longitude);
            if (map != null) { //맵 객체가 null이 아니면 현재 위치로 맵 이동시킴
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    public void startLocationService(View view) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Log.d(TAG,"known location:"+location);
            if (location != null) {
                TextView textView = (TextView) findViewById(R.id.location);
                textView.setText("내 위치:" + location.getLatitude() + "," + location.getLongitude());
                Toast.makeText(this, "Last Known Location 위도:" +
                        location.getLatitude() + ",경도:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
            if (map != null) { //맵 객체가 null이 아니면 현재 위치로 맵 이동시킴
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            }

            GPSListener gpsListener = new GPSListener();
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsListener);
        }
    }

    public void onWorldMapButtonClicked(View view) {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.moveCamera(CameraUpdateFactory.zoomTo(1));
        }
    }

    public void onTourButtonClicked(View view) {


        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers[currentPos].latLng, 19));
        }
        if (currentPos >= markers.length - 1) {
            currentPos = 0;
        } else
            currentPos++;

        // Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
//        if (map != null) {
//            for(int i=0; i<markers.length; i++) {
//                MarkerOptions marker = new MarkerOptions();
//                marker.position(markers[i].latLng);
//                marker.title(markers[i].name);
//                map.addMarker(marker);
//
//            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
//            }
//        }


    }

}
