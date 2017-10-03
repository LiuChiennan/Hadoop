package com.example.hadoop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * Created by 刘建南 on 2017/9/30.
 */

public class launchActivity extends Activity {
    private Integer delay=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_launch);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(launchActivity.this,MainActivity.class));
                launchActivity.this.finish();
            }
        },delay);
    }
}
