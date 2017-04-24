package com.example.lavender.wifilocation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;

import static com.example.lavender.wifilocation.Common.toJson;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLocation,btnScan,btnCollection;
    private String apData;
    private TextView showData,txtCoord,txtRoom;
    private Spinner spinner;
    private String room_id;
    private String[] data = new String[]{"科协办公室","实验室1","实验室2"};

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            apData = intent.getStringExtra("ap");
            showData.setText(apData);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
        registerReceiver(receiver,new IntentFilter("apData"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void init()
    {
        btnLocation = (Button)findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(this);
        btnScan = (Button)findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        btnCollection = (Button)findViewById(R.id.btnCollection);
        btnCollection.setOnClickListener(this);
        showData = (TextView)findViewById(R.id.showRssiData);
        txtCoord = (TextView)findViewById(R.id.textCoord);
        txtRoom = (TextView)findViewById(R.id.txtRoom);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                room_id = id + 1 + "";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnScan:
                Intent intent = new Intent(Main2Activity.this, GetRSSIService.class);
                startService(intent);
                break;
            case R.id.btnLocation:
                locationPost();
                break;
            case R.id.btnCollection:
                Intent intent1 = new Intent(Main2Activity.this, GetRssiActivity.class);
                startActivity(intent1);
        }
    }

    private void locationPost()
    {
        Intent intent = new Intent(Main2Activity.this,GetRSSIService.class);
        stopService(intent);

        JSONObject json = new JSONObject();
        try
        {
            json.put("room_id",room_id);
            json.put("mobile_id","3");
            json.put("ap",toJson(apData));
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        HttpConnect http = new HttpConnect(new HttpConnect.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                txtCoord.setText("您的当前位置为：" + output);
            }
        });
        http.execute("POST",http.LOCATION,json.toString());
    }
}