package fanjh.mine.player.ijkplayer;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import java.io.IOException;

import fanjh.mine.player.DataResourceException;
import fanjh.mine.player.R;
import fanjh.mine.player.VideoPlayer;
import tv.danmaku.ijk.media.player.AbstractMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
* @author fanjh
* @date 2018/1/15 11:35
* @description 基于Bilibili的IjkMediaPlayer实现
* @note
**/
public class IJkPlayers implements VideoPlayer{
    public static final String WIFI_LOCK_NAME = "video_lock";
    private IjkMediaPlayer mediaPlayer;
    private Context context;
    @VideoState
    private int currentState;
    private WifiManager.WifiLock wifiLock;
    private boolean shouldStartWhenPrepared;
    private OnPreparedListener onPreparedListener;
    private OnBufferingListener onBufferingListener;
    private OnErrorListener onErrorListener;
    private Uri uri;
    private Surface surface;

    public IJkPlayers(Context context) {
        this.context = context;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    private void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new IjkMediaPlayer();
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(new AbstractMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer mp) {
                    currentState = STATE_PREPARED;
                    releaseWifiLock();
                    if(null != onPreparedListener){
                        onPreparedListener.onPrepared(shouldStartWhenPrepared, (int) mp.getDuration());
                    }
                    shouldStartWhenPrepared = false;
                }
            });
            mediaPlayer.setOnCompletionListener(new AbstractMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer mp) {
                    currentState = STATE_PLAY_COMPLETED;
                }
            });
            mediaPlayer.setOnInfoListener(new AbstractMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new AbstractMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    switch (currentState){
                        case STATE_PLAYING:
                            if(null != onBufferingListener){
                                onBufferingListener.onBuffered(percent);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    Toast.makeText(context, context.getString(R.string.playing_error), Toast.LENGTH_LONG).show();
                    if(null != mediaPlayer){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        currentState = STATE_INITIAL;
                        try {
                            setDataResource(uri);
                            if(null != surface){
                                mediaPlayer.setSurface(surface);
                            }
                        } catch (DataResourceException e) {
                            e.printStackTrace();
                        }
                    }
                    if(null != onErrorListener){
                        onErrorListener.onError(i,i1);
                    }
                    return false;
                }
            });
            currentState = STATE_INITIAL;
        }
    }

    private void releaseWifiLock() {
        if (null != wifiLock && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    private void prepareAsync(boolean shouldStartWhenPrepared) {
        this.shouldStartWhenPrepared = shouldStartWhenPrepared;
        releaseWifiLock();
        WifiManager wifiManager = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        if (null != wifiManager) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_NAME);
            wifiLock.acquire();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void setDataResource(Uri uri) throws DataResourceException {
        this.uri = uri;
        switch (currentState){
            case STATE_NO_INIT:
            case STATE_INITIAL:
                initMediaPlayer();
                try {
                    mediaPlayer.setDataSource(context, uri);
                    prepareAsync(false);
                    currentState = STATE_PREPARING;
                } catch (IOException e) {
                    throw new DataResourceException(e);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean start() {
        switch (currentState) {
            case STATE_PREPARED:
            case STATE_PAUSE:
            case STATE_BUFFERING:
            case STATE_PLAY_COMPLETED:
                mediaPlayer.start();
                currentState = STATE_PLAYING;
                return true;
            case STATE_STOP:
                prepareAsync(true);
                return false;
            default:
                Toast.makeText(context,context.getString(R.string.start_when_no_prepared),Toast.LENGTH_LONG).show();
                return false;
        }
    }

    @Override
    public boolean pause() {
        switch (currentState) {
            case STATE_BUFFERING:
            case STATE_PLAYING:
                mediaPlayer.pause();
                currentState = STATE_PAUSE;
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean stop() {
        switch (currentState) {
            case STATE_PLAY_COMPLETED:
            case STATE_PREPARED:
            case STATE_BUFFERING:
            case STATE_PLAYING:
            case STATE_PAUSE:
                mediaPlayer.stop();
                currentState = STATE_STOP;
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean release() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
            currentState = STATE_RELEASE;
            mediaPlayer = null;
            releaseWifiLock();
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentMillis() {
        return null != mediaPlayer? (int) mediaPlayer.getCurrentPosition() :-1;
    }

    @Override
    public boolean seekTo(int newMillis) {
        switch (currentState) {
            case STATE_NO_INIT:
            case STATE_INITIAL:
            case STATE_PREPARING:
            case STATE_RELEASE:
            case STATE_STOP:
                return false;
            default:
                mediaPlayer.seekTo(newMillis);
                return true;
        }
    }

    @Override
    public void attachSurface(Object object) {
        this.surface = (Surface) object;
        if(null != mediaPlayer){
            mediaPlayer.setSurface((Surface) object);
        }
    }

    @Override
    public boolean isPlaying() {
        return currentState == STATE_PLAYING;
    }

    @Override
    public boolean alreadyPrepared() {
        return currentState >= STATE_PREPARED && currentState <= STATE_PLAY_COMPLETED;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    @Override
    public void setOnBufferingListener(OnBufferingListener listener) {
        this.onBufferingListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        this.onErrorListener = listener;
    }

    @Override
    public void onActivityStop() {
        IjkMediaPlayer.native_profileEnd();
    }
}
