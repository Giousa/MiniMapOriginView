package com.giou.minimapview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.giou.minimapview.utils.Timer;
import com.giou.minimapview.view.MiniMapView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private String objUrl = "multiPerson/bike.obj";
    private MiniMapView mMiniMapView;

    private Timer mTimer = new Timer(600, new Timer.OnTimer() {
        @Override
        public void onTime(Timer timer) {


        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMiniMapView = (MiniMapView) findViewById(R.id.mini_map_view);

        mMiniMapView.loadObjFile(objUrl);


    }

}
