package com.adimodi96.snapfeatures;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

public class SplashActivity extends Activity {

    private static int SPLASH_DISPLAY_TIME = 1500;
    private int ALL_PERMISSIONS_CODE = 197;
    RelativeLayout relative_content;

    Button button;

    private String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        relative_content = findViewById(R.id.relative_content);
        button = findViewById(R.id.button_proceed);

        Animation scaleAnimation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.scale_animation);

        relative_content.startAnimation(scaleAnimation);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                button.setVisibility(View.VISIBLE);
                button.animate().alphaBy(1.0f).setDuration(1500);
            }
        }, SPLASH_DISPLAY_TIME);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, ALL_PERMISSIONS_CODE);
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}