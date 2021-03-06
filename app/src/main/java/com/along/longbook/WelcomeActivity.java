package com.along.longbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;

    @BindView(R.id.l1)
    LinearLayout up;
    @BindView(R.id.l2)
    LinearLayout down;
    Animation uptodown, downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        up.setAnimation(uptodown);
        down.setAnimation(downtoup);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent listBookIntent = new Intent(WelcomeActivity.this, ListBookActivity.class);
                startActivity(listBookIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
