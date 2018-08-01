package com.alfanthariq.broadcastpromofirebase.helper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.alfanthariq.broadcastpromofirebase.Constant.DURATION;

/**
 * Created by alfanthariq on 29/07/2018.
 */

public class MyFunction {

    public static void animatePagerTransition(final boolean forward, final ViewPager pager) {
        ValueAnimator animator = ValueAnimator.ofInt(0, pager.getWidth() - ( forward ? pager.getPaddingLeft() : pager.getPaddingRight() ));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                pager.fakeDragBy(dragOffset * (forward ? -1 : 1));
            }
        });

        animator.setDuration(DURATION);
        pager.beginFakeDrag();
        animator.start();
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmapImage, String fileName){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/images
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String fileName)
    {
        Bitmap b = null;
        try {
            File f=new File(path, fileName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public static Bitmap getBitmpatFromUri(Context context, Uri givenUri){
        Bitmap bitmap = null;
        try {
            if(givenUri!=null){
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , givenUri);
            }
        }
        catch (Exception e) {
            //handle exception
        }
        return bitmap;
    }

    public static boolean isConnectToInternet(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;

    }
}
