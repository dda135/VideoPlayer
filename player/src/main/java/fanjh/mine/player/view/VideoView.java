package fanjh.mine.player.view;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import fanjh.mine.player.R;
import fanjh.mine.player.VideoPlayerClient;

import static fanjh.mine.player.VideoPlayerClient.STATE_COMMON;
import static fanjh.mine.player.VideoPlayerClient.STATE_FULL_SCREEN;
import static fanjh.mine.player.VideoPlayerClient.STATE_SMALL_WINDOW;


public class VideoView extends FrameLayout implements ITextureView{
    private TextureView textureView;
    private BaseToolbarView toolbarView;
    private VideoViewListener videoViewListener;
    private int currentDuration;
    private int maxDuration;
    private int currentBufferedValue;
    @VideoPlayerClient.ShowState
    private int currentShowState;

    @Override
    public void setVideoViewListener(VideoViewListener videoViewListener) {
        this.videoViewListener = videoViewListener;
        if(null != toolbarView) {
            toolbarView.setVideoViewListener(videoViewListener);
        }
    }

    @Override
    public void showToolbar(boolean shouldAutoHidden) {
        toolbarView.showToolbar();
        if(shouldAutoHidden) {
            toolbarView.autoHiddenToolbar();
        }
    }

    @Override
    public void onPlayingError() {
        if(null != toolbarView){
            toolbarView.showToolbar();
            toolbarView.updateBufferBar(0);
            toolbarView.updateProgressBar(0);
        }
    }

    public VideoView(@NonNull Context context) {
        this(context,null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        LayoutInflater.from(context).inflate(R.layout.view_video,this,true);
        textureView = findViewById(R.id.tv_video_view);
        textureView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        textureView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showToolbar(!toolbarView.isShowing());
                return false;
            }
        });
    }

    @Override
    public void setTextureViewListener(TextureView.SurfaceTextureListener listener){
        textureView.setSurfaceTextureListener(listener);
    }

    @Override
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textureView.setSurfaceTexture(surfaceTexture);
        }
    }

    @Override
    public void onFullScreen(boolean isPlaying) {
        currentShowState = STATE_FULL_SCREEN;
        initCommonToolbar();
        toolbarView.onFullScreen(isPlaying,currentDuration,maxDuration,currentBufferedValue);
    }

    @Override
    public void onRecoverNormal(boolean isPlaying) {
        currentShowState = STATE_COMMON;
        initCommonToolbar();
        toolbarView.onRecoverNormal(isPlaying,currentDuration,maxDuration,currentBufferedValue);
    }

    private void initCommonToolbar(){
        removeView(toolbarView);
        toolbarView = new CommonToolbar(getContext());
        toolbarView.setVideoViewListener(videoViewListener);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(toolbarView,params);
    }

    private void initEasyToolbar(){
        removeView(toolbarView);
        toolbarView = new EasyToolbar(getContext());
        toolbarView.setVideoViewListener(videoViewListener);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(toolbarView,params);
    }

    @Override
    public void onSmallWindow(boolean isPlaying) {
        currentShowState = STATE_SMALL_WINDOW;
        initEasyToolbar();
        toolbarView.onSmallWindow(isPlaying,currentDuration,maxDuration,currentBufferedValue);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPlayingMillisUpdate(int millis) {
        if(millis < 0){
            return;
        }
        currentDuration = millis;
        if(null == toolbarView){
            return;
        }
        toolbarView.updateProgressBar(currentDuration);
    }

    @Override
    public void onBufferedValueUpdate(int value) {
        currentBufferedValue = value;
        if(null != toolbarView){
            toolbarView.updateBufferBar(value);
        }
    }

    @Override
    public void onDataResourcePrepared(int totalDuration) {
        if(totalDuration <= 0){
            return;
        }
        maxDuration = totalDuration;
        if(null == toolbarView){
            return;
        }
        toolbarView.setProgressBarMax(maxDuration);
    }
}
