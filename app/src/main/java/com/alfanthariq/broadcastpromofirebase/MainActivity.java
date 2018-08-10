package com.alfanthariq.broadcastpromofirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.broadcastpromofirebase.adapter.PagerAdapter;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentActivePromo;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentCoomingPromo;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentMenu;
import com.alfanthariq.broadcastpromofirebase.helper.AfterCropListener;
import com.alfanthariq.broadcastpromofirebase.rest.ApiLibrary;
import com.alfanthariq.broadcastpromofirebase.rest.RetrofitService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;

import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.openSettings;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private RelativeLayout modal;
    private float thresholdOffset = 0.01f;
    private boolean goRight, checkDirection = true, stopScroll;
    private AfterCropListener afterCropListener;
    public ApiLibrary apiLibrary;
    private TextView txt_location;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private String mProvider;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String TAG = "Location";
    private int requestOnce = 1; //1 : once, 0 : updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        apiLibrary = RetrofitService.createService(ApiLibrary.class);

        initLocation();

        final FragmentActivePromo fragmentActivePromo = FragmentActivePromo.newInstance();
        FragmentMenu fragmentMenu = FragmentMenu.newInstance();
        FragmentCoomingPromo fragmentCoomingPromo = FragmentCoomingPromo.newInstance();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add((Fragment) fragmentMenu);
        fragments.add((Fragment) fragmentActivePromo);
        fragments.add((Fragment) fragmentCoomingPromo);

        fragmentActivePromo.setAfterCreateListener(new FragmentActivePromo.AfterCreateListener() {
            @Override
            public void onAfterCreate() {
                modal = fragmentActivePromo.getLayoutModal();
                modal.setVisibility(View.GONE);

                txt_location = fragmentActivePromo.getTxt_location();
            }
        });

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (checkDirection) {
                    if (thresholdOffset < positionOffset) {
                        goRight = true;
                    } else {
                        goRight = false;
                    }
                    checkDirection = false;
                }
                float alpha = 0.0f;
                if (positionOffset>0.0f) {
                    modal.setVisibility(View.VISIBLE);
                    if (position==1) {
                        if (!goRight) {
                            alpha = positionOffset - 0.2f;
                            modal.setAlpha(alpha);
                        } else {
                            alpha = (1 - positionOffset) - 0.3f;
                            modal.setAlpha(alpha);
                        }
                    } else {
                        if (goRight) {
                            alpha = positionOffset - 0.2f;
                            modal.setAlpha(alpha);
                        } else {
                            alpha = (1 - positionOffset) - 0.3f;
                            modal.setAlpha(alpha);
                        }
                    }
                } else {
                    if (position==1) {
                        modal.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position==1){
                    modal.setVisibility(View.GONE);
                } else {
                    modal.setVisibility(View.VISIBLE);
                }
                //Log.d("MainActivity", "Position : "+Integer.toString(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        startLocationUpdates();
                    }
                } else {
                    openSettings(this);
                }

                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem()!=1) {
            pager.setCurrentItem(1, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                afterCropListener.onAfterCrop(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onStop(){
        stopLocationUpdates();
        super.onStop();
    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                updateUI();

                if (requestOnce == 1) {
                    stopLocationUpdates();
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mProvider = mLocationManager.getBestProvider(new Criteria(), false);

        if (checkLocationPermission()){
            startLocationUpdates();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location permission")
                        .setMessage("Aplikasi ini membutuhkan akses lokasi")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void updateUI() {
        if (txt_location!=null) {
            if (mCurrentLocation != null) {
                txt_location.setText("Location - Lat : " + mCurrentLocation.getLatitude() + ", Lon : " + mCurrentLocation.getLongitude());
            } else {
                txt_location.setText("Location : unknown");
            }
        }
    }

    public void setAfterCropListener(AfterCropListener afterCropListener) {
        this.afterCropListener = afterCropListener;
    }

    public ApiLibrary getApiLibrary() {
        return apiLibrary;
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "Location settings on");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());

                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings off");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateUI();
                    }
                });
    }

    private void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback);
    }

}
