package fanjh.mine.videoplayer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.player.ListPlayerController;
import fanjh.mine.player.MediaPlayerClient;

/**
* @author fanjh
* @date 2018/1/15 9:36
* @description
* @note
**/
public class TestRecyclerViewActivity extends Activity{
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ListPlayerController listPlayerController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        recyclerView = findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listPlayerController = new ListPlayerController(this, MediaPlayerClient.TYPE_IJKPLAYER);
        adapter = new RecyclerAdapter(this,listPlayerController);
        adapter.updateList(getData());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnChildAttachStateChangeListener(listPlayerController.getRecyclerOnChildAttachChangeListener(recyclerView));
    }

    private List<Uri> getData(){
        List<Uri> uris = new ArrayList<>();
        for(int i = 1;i < 14;++i){
            Uri uri = Uri.parse("http://10.1.93.196:8080/IM/"+i+".mp4");
            uris.add(uri);
        }
        return uris;
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPlayerController.setRecyclerViewVisible(recyclerView,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        listPlayerController.setRecyclerViewVisible(recyclerView,false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destroy();
    }

}
