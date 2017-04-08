package com.cdx.onestepsos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private  TextView tv_sos;

    private Button btn_capture;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothTools.ACTION_DATA_TO_GAME)){
              // String s  = (String) intent.getSerializableExtra(BluetoothTools.DATA);
                //if(s.equals("SOS")){
                    Toast.makeText(MainActivity.this,"SOS",Toast.LENGTH_LONG).show();
                    tv_sos.setText("SOS");
                    SendMessage sendMessage = new SendMessage("18850042915","SOS");
                    sendMessage.Send();
               // }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv_location);
        tv_sos = (TextView)findViewById(R.id.tv_sos);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        Location location = new Location();
        location.init(getApplicationContext());
        location.setTv_location(textView);
        location.startLocation();

        Intent intent = new Intent(MainActivity.this,BluetoothServerService.class);
        startService(intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        registerReceiver(broadcastReceiver,intentFilter);

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
