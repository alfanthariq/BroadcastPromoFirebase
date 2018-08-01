package com.alfanthariq.broadcastpromofirebase;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alfanthariq.broadcastpromofirebase.adapter.PagerAdapter;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentActivePromo;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentCoomingPromo;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentMenu;
import com.alfanthariq.broadcastpromofirebase.helper.AfterCropListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private RelativeLayout modal;
    private float thresholdOffset = 0.01f;
    private boolean goRight, checkDirection = true, stopScroll;
    private AfterCropListener afterCropListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);

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

    public void setAfterCropListener(AfterCropListener afterCropListener) {
        this.afterCropListener = afterCropListener;
    }
}
