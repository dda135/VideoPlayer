package fanjh.mine.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import fanjh.mine.player.DataResourceException;
import fanjh.mine.player.VideoPlayer;
import fanjh.mine.player.VideoPlayerClient;
import fanjh.mine.player.ijkplayer.IJkPlayers;
import fanjh.mine.player.mediaplayer.MediaPlayers;
import fanjh.mine.player.view.ITextureView;
import fanjh.mine.player.view.VideoView;
import fanjh.mine.player.view.VideoViewListener;

/**
 * @author fanjh
 * @date 2018/1/11 9:42
 * @description 使用MediaPlayer实现的播放操作
 * @note
 **/
public class MediaPlayerClient implements VideoPlayerClient, TextureView.SurfaceTextureListener,
        VideoViewListener,VideoPlayer.OnPreparedListener,VideoPlayer.OnBufferingListener,VideoPlayer.OnErrorListener {
    public static final int TYPE_MEDIAPLAYER = 1;
    public static final int TYPE_IJKPLAYER = 2;
    @IntDef({TYPE_IJKPLAYER,TYPE_MEDIAPLAYER})
    public @interface PlayerType{}
    private ITextureView videoView;
    private VideoPlayer videoPlayer;
    private Context context;
    private Surface surface;
    private boolean shouldResume;
    private ViewGroup parent;

    public MediaPlayerClient(Context context, @PlayerType int type) {
        this.context = context;
        switch (type) {
            case TYPE_IJKPLAYER:
                videoPlayer = new IJkPlayers(context);
                break;
            case TYPE_MEDIAPLAYER:
                videoPlayer = new MediaPlayers(context);
                break;
            default:
                throw new IllegalArgumentException("类型不匹配！");
        }
        videoPlayer.setOnPreparedListener(this);
        videoPlayer.setOnBufferingListener(this);
        videoPlayer.setOnErrorListener(this);
    }

    @Override
    public void attachView(ViewGroup viewGroup) {
        initVideoView();
        detachView();
        viewGroup.removeView(videoView.getView());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewGroup.addView(videoView.getView(), params);
        parent = viewGroup;
        videoView.onRecoverNormal(videoPlayer.isPlaying());
    }

    @Override
    public void fullScreenShow() {
        initVideoView();
        detachView();
        ViewGroup vp = ((Activity)(context)).findViewById(Window.ID_ANDROID_CONTENT);
        vp.addView(videoView.getView(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        videoView.onFullScreen(videoPlayer.isPlaying());
    }

    @Override
    public void smallWindowShow(int width,int height) {
        initVideoView();
        detachView();
        ViewGroup vp = ((Activity)(context)).findViewById(Window.ID_ANDROID_CONTENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
        params.gravity = Gravity.CENTER;
        vp.addView(videoView.getView(),params);
        videoView.onSmallWindow(videoPlayer.isPlaying());
    }

    @Override
    public void detachView() {
        removeVideoViewFromParent();
        removeVideoViewFromWindow();
    }

    private void removeVideoViewFromWindow(){
        ViewGroup vp = ((Activity)(context)).findViewById(Window.ID_ANDROID_CONTENT);
        vp.removeView(videoView.getView());
    }

    private void removeVideoViewFromParent(){
        try {
            if (null != parent && null != videoView) {
                parent.removeView(videoView.getView());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityDestroy() {
        if(null != videoPlayer && null != videoView){
            videoPlayer.release();
            videoView = null;
            videoPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onActivityStop() {
        if(null != videoPlayer){
            videoPlayer.onActivityStop();
        }
    }

    @Override
    public void setViewVisible(boolean visible) {
        if(null == videoPlayer){
            return;
        }
        if(!videoPlayer.alreadyPrepared()){
            return;
        }
        if(visible){
            if(shouldResume) {
                videoPlayer.start();
                videoView.showToolbar(true);
                shouldResume = false;
            }
        }else{
            shouldResume = videoPlayer.isPlaying();
            videoPlayer.pause();
        }
    }

    @Override
    public void setDataResource(Uri uri) throws DataResourceException {
        videoPlayer.setDataResource(uri);
    }

    public static final int MSG_PROGRESS_UPDATE = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS_UPDATE:
                    if (null != videoView && null != videoPlayer) {
                        videoView.onPlayingMillisUpdate(videoPlayer.getCurrentMillis());
                        handler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initVideoView() {
        if(null == videoView) {
            videoView = new VideoView(context);
            videoView.setTextureViewListener(this);
            videoView.setVideoViewListener(this);
        }
    }

    private SurfaceTexture surfaceTexture;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture newSurface, int width, int height) {
        if(null == surfaceTexture) {
            surfaceTexture = newSurface;
            surface = new Surface(surfaceTexture);
            videoPlayer.attachSurface(surface);
        }else {
            if (null != videoView) {
                videoView.setSurfaceTexture(surfaceTexture);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture newSurface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return surface == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture newSurface) {
    }

    @Override
    public void onPause() {
        if(videoPlayer.pause()){
            handler.removeMessages(MSG_PROGRESS_UPDATE);
        }
    }

    @Override
    public void onPlay() {
        if(videoPlayer.start()){
            handler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
        }
    }

    @Override
    public void onStop() {
        if(videoPlayer.stop()){
            handler.removeMessages(MSG_PROGRESS_UPDATE);
            if (null != videoView) {
                videoView.onPlayingMillisUpdate(0);
            }
        }
    }

    @Override
    public void onSeekChanged(int progress, boolean fromUser) {
        if (!fromUser || null == videoPlayer || null == videoView) {
            return;
        }
        if(videoPlayer.seekTo(progress)){
            if(null == videoView){
                return;
            }
            videoView.onPlayingMillisUpdate(progress);
        }
    }

    @Override
    public void onStartTrackingTouch() {
        if(videoPlayer.pause()){
            handler.removeMessages(MSG_PROGRESS_UPDATE);
        }
    }

    @Override
    public void onStopTrackingTouch() {
        if(videoPlayer.start()){
            handler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 1000);
        }
    }

    @Override
    public void fullScreen() {
        fullScreenShow();
    }

    @Override
    public void recoverNormal() {
        if(null == parent){
            return;
        }
        attachView(parent);
    }

    @Override
    public void smallWindow(int width,int height) {
        smallWindowShow(width, height);
    }

    @Override
    public void onPrepared(boolean autoStart, int totalMillis) {
        if(null == videoView){
            return;
        }
        videoView.onDataResourcePrepared(totalMillis);
        if(autoStart){
            videoPlayer.start();
            handler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE,1000);
        }
    }

    @Override
    public void onBuffered(int value) {
        if(null != videoView){
            videoView.onBufferedValueUpdate(value);
        }
    }

    @Override
    public void onError(int code1, int code2) {
        handler.removeMessages(MSG_PROGRESS_UPDATE);
        if(null != videoView){
           videoView.onPlayingError();
        }
    }
}
