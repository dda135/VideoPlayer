package fanjh.mine.player;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

/**
* @author fanjh
* @date 2018/1/15 15:09
* @description 用于处理列表出现的复用等情况下的播放管理
* @note
**/
public class ListPlayerController {
    private Context context;
    private SparseArray<VideoPlayerClient> clients;
    private SparseIntArray playerTypes;
    private @MediaPlayerClient.PlayerType int defaultPlayerType;

    public ListPlayerController(Context context) {
        this.context = context;
        defaultPlayerType = MediaPlayerClient.TYPE_MEDIAPLAYER;
    }

    public ListPlayerController(Context context, @MediaPlayerClient.PlayerType int defaultPlayerType) {
        this.context = context;
        this.defaultPlayerType = defaultPlayerType;
    }

    public void init(int count){
        if(null == clients){
            clients = new SparseArray<>(count);
        }else{
            clients.clear();
        }
    }

    public void setPlayerTypes(int position, @MediaPlayerClient.PlayerType int playerType){
        if(null == playerTypes){
            playerTypes = new SparseIntArray();
        }
        playerTypes.put(position,playerType);
    }

    public void attachView(ViewGroup viewGroup, int position, Uri uri){
        VideoPlayerClient oldClient = (VideoPlayerClient) viewGroup.getTag(R.id.video_tag);
        if(null != oldClient){
            oldClient.detachView();
        }
        VideoPlayerClient client = clients.get(position);
        if(null == client){
            @MediaPlayerClient.PlayerType
            int playerType = MediaPlayerClient.TYPE_MEDIAPLAYER;
            if(null != playerTypes){
                playerType = playerTypes.get(position,MediaPlayerClient.TYPE_MEDIAPLAYER);
            }
            client = new MediaPlayerClient(context,playerType);
            clients.put(position,client);
        }
        viewGroup.setTag(R.id.video_tag,client);
        client.attachView(viewGroup);
        try {
            client.setDataResource(uri);
        } catch (DataResourceException e) {
            e.printStackTrace();
        }
    }

    public void setViewVisible(int position,boolean visible){
        if(null == clients){
            return;
        }
        VideoPlayerClient client = clients.get(position);
        if(null == client){
            return;
        }
        client.setViewVisible(visible);
    }

    private void setViewVisible(RecyclerView recyclerView,View view,boolean visible){
        int position = recyclerView.getChildAdapterPosition(view);
        if(RecyclerView.NO_POSITION != position){
            setViewVisible(position,visible);
        }
    }

    public void onStop(){
        for(int i = 0;i < clients.size();++i){
            VideoPlayerClient client = clients.get(clients.keyAt(i));
            if(null != client){
                client.onActivityStop();
            }
        }
    }

    public void destroy(){
        for(int i = 0;i < clients.size();++i){
            VideoPlayerClient client = clients.get(clients.keyAt(i));
            if(null != client){
                client.onActivityDestroy();
            }
        }
    }

    public RecyclerOnChildAttachChangeListener getRecyclerOnChildAttachChangeListener(RecyclerView recyclerView){
        return new RecyclerOnChildAttachChangeListener(recyclerView);
    }

    public void setRecyclerViewVisible(RecyclerView recyclerView,boolean visible){
        for(int i = 0;i < recyclerView.getChildCount();++i){
            View child = recyclerView.getChildAt(i);
            setViewVisible(recyclerView,child,visible);
        }
    }

    public class RecyclerOnChildAttachChangeListener implements RecyclerView.OnChildAttachStateChangeListener{
        private RecyclerView recyclerView;

        public RecyclerOnChildAttachChangeListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onChildViewAttachedToWindow(View view) {

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            int position = recyclerView.getChildAdapterPosition(view);
            if(RecyclerView.NO_POSITION != position){
                setViewVisible(position,false);
            }
        }
    }


}
