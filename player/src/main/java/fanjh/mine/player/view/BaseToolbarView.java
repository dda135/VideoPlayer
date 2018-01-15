package fanjh.mine.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
* @author fanjh
* @date 2018/1/12 11:08
* @description
* @note
**/
public abstract class BaseToolbarView extends FrameLayout {
    protected VideoViewListener videoViewListener;

    public void setVideoViewListener(VideoViewListener videoViewListener) {
        this.videoViewListener = videoViewListener;
    }

    public BaseToolbarView(@NonNull Context context) {
        this(context, null);
    }

    public BaseToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseToolbarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void showToolbar();
    public abstract void autoHiddenToolbar();
    public abstract void setProgressBarMax(int maxDuration);
    public abstract void updateProgressBar(int nowDuration);
    public abstract void updateBufferBar(int value);
    public abstract void onFullScreen(boolean isPlaying,int currentDuration,int maxDuration,int bufferedValue);
    public abstract void onRecoverNormal(boolean isPlaying,int currentDuration,int maxDuration,int bufferedValue);
    public abstract void onSmallWindow(boolean isPlaying,int currentDuration,int maxDuration,int bufferedValue);
    public abstract boolean isShowing();

}
