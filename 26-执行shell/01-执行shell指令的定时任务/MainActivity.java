package com.alless.rxsensedemo;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn_start;
    private Button mBtn_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn_start = (Button) findViewById(R.id.btn_start);
        mBtn_stop = (Button) findViewById(R.id.btn_stop);
        mBtn_start.setOnClickListener(this);
        mBtn_stop.setOnClickListener(this);
        init();
    }

    private void init() {
        CommandExecution.execCommand(CmdString.INIT_CMD,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
               startTask();
                break;
            case R.id.btn_stop:
               stopTask();
                break;
        }
    }

    private void startTask() {
        startService(new Intent(MainActivity.this, RxSenseService.class));
    }
    private void stopTask(){
        stopService(new Intent(MainActivity.this, RxSenseService.class));
    }


}
