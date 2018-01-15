package fanjh.mine.videoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import fanjh.mine.player.DataResourceException;
import fanjh.mine.player.MediaPlayerClient;
import fanjh.mine.player.VideoPlayerClient;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private VideoPlayerClient videoPlayerClient;
    private Button smallButton;
    private Button fullButton;
    private Button normalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smallButton = findViewById(R.id.btn_small);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120,getResources().getDisplayMetrics());
                videoPlayerClient.smallWindowShow(size,size);
            }
        });
        fullButton = findViewById(R.id.btn_full);
        fullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayerClient.fullScreenShow();
            }
        });
        normalButton = findViewById(R.id.btn_normal);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayerClient.attachView(frameLayout);
            }
        });
        frameLayout = findViewById(R.id.fl_video_layout);
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(PackageManager.PERMISSION_GRANTED == result){
            initMedia();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])){
                if(PackageManager.PERMISSION_GRANTED == grantResults[0]){
                    initMedia();
                }
            }
        }
    }

    private void initMedia(){
        videoPlayerClient = new MediaPlayerClient(this,MediaPlayerClient.TYPE_MEDIAPLAYER);
        videoPlayerClient.attachView(frameLayout);
        //Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"Starry_Night.mp4"));
        Uri uri = Uri.parse("http://10.1.93.196:8080/IM/Starry_Night.mp4");
        try {
            videoPlayerClient.setDataResource(uri);
        } catch (DataResourceException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != videoPlayerClient){
            videoPlayerClient.setViewVisible(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != videoPlayerClient){
            videoPlayerClient.setViewVisible(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != videoPlayerClient){
            videoPlayerClient.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != videoPlayerClient){
            videoPlayerClient.onActivityDestroy();
        }
    }
}
