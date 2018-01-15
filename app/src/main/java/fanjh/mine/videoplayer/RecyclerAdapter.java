package fanjh.mine.videoplayer;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.player.ListPlayerController;

/**
* @author fanjh
* @date 2018/1/15 9:48
* @description
* @note
**/
public class RecyclerAdapter extends RecyclerView.Adapter{
    private Context context;
    private List<Uri> dataSets;
    private ListPlayerController listPlayerController;

    public RecyclerAdapter(Context context,ListPlayerController listPlayerController) {
        this.context = context;
        this.listPlayerController = listPlayerController;
    }

    public void updateList(List<Uri> uris){
        if(null == uris){
            return;
        }
        if(dataSets == null){
            dataSets = new ArrayList<>(uris);
        }else{
            dataSets.clear();
            dataSets.addAll(uris);
        }
        listPlayerController.init(dataSets.size());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(context).inflate(R.layout.item_video,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoHolder videoHolder = (VideoHolder) holder;
        videoHolder.titleView.setText(position+":");
        listPlayerController.attachView(videoHolder.videoLayout,position,dataSets.get(position));
    }

    @Override
    public int getItemCount() {
        return null != dataSets?dataSets.size():0;
    }

    public void setViewVisible(int position,boolean visible){
        listPlayerController.setViewVisible(position, visible);
    }

    public void onStop(){
        listPlayerController.onStop();
    }

    public void destroy(){
        listPlayerController.destroy();
    }

    static class VideoHolder extends RecyclerView.ViewHolder{
        TextView titleView;
        FrameLayout videoLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tv_title);
            videoLayout = itemView.findViewById(R.id.fl_video_layout);
        }

    }

}
