package com.som.som;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim;
        ImageView iv = (ImageView) findViewById(R.id.splash);
        TextView tv = (TextView) findViewById(R.id.web);
        TextView tv2 = (TextView) findViewById(R.id.som);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        tv.clearAnimation();
        tv.startAnimation(anim);
        tv2.clearAnimation();
        tv2.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }
            }
        };
        splashTread.start();

    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
