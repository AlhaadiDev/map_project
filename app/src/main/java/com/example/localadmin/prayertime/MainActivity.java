package com.example.localadmin.prayertime;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.localadmin.prayertime.classes.PrayerTime;
import com.example.localadmin.prayertime.classes.Times;
import com.example.localadmin.prayertime.cloud.ApiServiceInterface;
import com.example.localadmin.prayertime.util.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout drawer;
    private LinearLayout mainDrawer, dropDown;
    private GoogleMap gMap;
    private ImageView mapIcon;
    private EditText address1, address2, edtSearch;

    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private double lat = 0;
    private double longi = 0;
    private LatLng latLng;
    private Geocoder geocoder;
    private Dialog dialogBox;
    private Boolean change = false;
    static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        drawer = findViewById(R.id.drawer_layout);
        mainDrawer = findViewById(R.id.main_drawer);
        mapIcon = findViewById(R.id.map_icon);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        edtSearch = findViewById(R.id.edit_search);
        dropDown = findViewById(R.id.drop_down);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationDrawer();
            }
        });

        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapType();
            }
        });

        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.clear();
                getLocation();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    gMap.clear();
                    getSearchAddress(edtSearch.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            lat = location.getLatitude();
            longi = location.getLongitude();
            latLng = new LatLng(lat, longi);

            Log.d("location", "getLocation: " + lat + " " + longi);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            gMap.animateCamera(cameraUpdate);
            gMap.addMarker(new MarkerOptions().position(latLng).title("You're Here").snippet(String.valueOf(latLng)));

            getAddress(lat, longi);

        }
    }

    private void getAddress(double lati, double longit) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lati, longit, 1);
            Address obj = addresses.get(0);

            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode/();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getThoroughfare();

//            String fulladdress = add;
            Log.d("map", "getAddress: " + add);
            address1.setText(add);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSearchAddress(String locationName) {
        geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 5);

            Address address = addressList.get(0);
            StringBuffer str = new StringBuffer();
            str.append("Name: " + address.getLocality() + "\n");
            str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
            str.append("Admin Area: " + address.getAdminArea() + "\n");
            str.append("Country: " + address.getCountryName() + "\n");
            str.append("Country Code: " + address.getCountryCode() + "\n");

            double latSearch = address.getLatitude();
            double longiSearch = address.getLongitude();
            LatLng latLngSearch = new LatLng(latSearch, longiSearch);

            String strAddress = str.toString();

            Log.d("searchAdd", "getSearchAddress: " + strAddress);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngSearch, 16);
            gMap.animateCamera(cameraUpdate);
            gMap.addMarker(new MarkerOptions().position(latLngSearch).title("You Picked Here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet(address.getAdminArea()));

            getAddress(latSearch, longiSearch);

        } catch (IOException e) {
            Toast.makeText(this, "Place not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        /*In Xioami phone can't run?*/
//        googleMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        getLocation();

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(latLng).title("You Picked Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                double a = latLng.latitude;
                double b = latLng.longitude;

                getAddress(a, b);
            }

        });

//        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                gMap.clear();
//                gMap.addMarker(new MarkerOptions().position(cameraPosition.target).title(cameraPosition.toString()));
//
//            }
//        });

    }

    private void onNavigationDrawer() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_open_drawer, R.string.navigation_close_drawer);
        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        findViewById(R.id.main_drawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_Tconditon) {
                    Intent compass = new Intent(MainActivity.this, CompassActivity.class);
                    startActivity(compass);
                } else if (id == R.id.nav_privacy_policy) {

                } else if (id == R.id.nav_faqs) {

                } else if (id == R.id.nav_log_out) {

                }

                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void onMapType() {
        dialogBox = new Dialog(MainActivity.this);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.setContentView(R.layout.dialog_map_type);
        dialogBox.show();

        dialogBox.findViewById(R.id.normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                dialogBox.dismiss();
            }
        });

        dialogBox.findViewById(R.id.hybrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                dialogBox.dismiss();
            }
        });

        dialogBox.findViewById(R.id.satellite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                dialogBox.dismiss();
            }
        });

        dialogBox.findViewById(R.id.terrain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                dialogBox.dismiss();
            }
        });

        dialogBox.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
            }
        });
    }
}
