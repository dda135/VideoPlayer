package fanjh.mine.player.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import fanjh.mine.player.R;
import fanjh.mine.player.VideoPlayerClient;

/**
* @author fanjh
* @date 2018/1/12 11:22
* @description 简易的播放工具栏
* @note
**/
public class EasyToolbar extends BaseToolbarView{
    public static final int HIDDEN_DELAY_TIME = 1000;
    public static final int MSG_HIDDEN_TOOLBAR = 1;
    private Button playButton;
    private Button pauseButton;
    private VideoSeekBar progressBar;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_HIDDEN_TOOLBAR:
                    if(canAutoHiddenToolbar) {
                        hide();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private boolean canAutoHiddenToolbar = true;
    @VideoPlayerClient.ShowState
    private int currentShowState;
    private boolean isPlaying;

    public EasyToolbar(@NonNull Context context) {
        this(context,null);
    }

    public EasyToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_easy_toolbar,this,true);
        playButton = findViewById(R.id.btn_play);
        pauseButton = findViewById(R.id.btn_pause);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    videoViewListener.onPlay();
                }
                autoHiddenToolbar();
                canAutoHiddenToolbar = true;
                changeButtonState(true);
            }
        });
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    videoViewListener.onPause();
                }
                canAutoHiddenToolbar = false;
                changeButtonState(false);
            }
        });
        progressBar = findViewById(R.id.sb_progress);
        progressBar.setSeekCanTouch(false);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(null != videoViewListener){
                    videoViewListener.onSeekChanged(progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(null != videoViewListener){
                    videoViewListener.onStartTrackingTouch();
                }
                canAutoHiddenToolbar = false;
                showToolbar();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(null != videoViewListener){
                    videoViewListener.onStopTrackingTouch();
                }
                canAutoHiddenToolbar = true;
                autoHiddenToolbar();
            }
        });
    }

    @Override
    public void showToolbar() {
        show();
    }

    @Override
    public void autoHiddenToolbar() {
        handler.sendEmptyMessageDelayed(MSG_HIDDEN_TOOLBAR,HIDDEN_DELAY_TIME);
    }

    @Override
    public void setProgressBarMax(int maxDuration) {
        progressBar.setMax(maxDuration);
    }

    @Override
    public void updateProgressBar(int nowDuration) {
        progressBar.setProgress(nowDuration);
    }

    @Override
    public void updateBufferBar(int value) {
        progressBar.setSecondaryProgress((int) (progressBar.getMax() * (value * 1.0 / 100)));
    }

    @Override
    public void onFullScreen(boolean isPlaying, int currentDuration, int maxDuration,int bufferedValue) {
        currentShowState = VideoPlayerClient.STATE_FULL_SCREEN;
        setProgressBarMax(maxDuration);
        updateProgressBar(currentDuration);
        updateBufferBar(bufferedValue);
        changeButtonState(isPlaying);
    }

    @Override
    public void onRecoverNormal(boolean isPlaying, int currentDuration, int maxDuration,int bufferedValue) {
        currentShowState = VideoPlayerClient.STATE_COMMON;
        setProgressBarMax(maxDuration);
        updateProgressBar(currentDuration);
        updateBufferBar(bufferedValue);
        changeButtonState(isPlaying);
    }

    @Override
    public void onSmallWindow(boolean isPlaying, int currentDuration, int maxDuration,int bufferedValue) {
        currentShowState = VideoPlayerClient.STATE_SMALL_WINDOW;
        setProgressBarMax(maxDuration);
        updateProgressBar(currentDuration);
        updateBufferBar(bufferedValue);
        changeButtonState(isPlaying);
    }

    private void changeButtonState(boolean isPlaying){
        this.isPlaying = isPlaying;
        playButton.setVisibility(isPlaying?GONE:VISIBLE);
        pauseButton.setVisibility(isPlaying?VISIBLE:GONE);
        progressBar.setSeekCanTouch(isPlaying);
    }

    @Override
    public boolean isShowing() {
        return playButton.getVisibility() == VISIBLE || pauseButton.getVisibility() == VISIBLE;
    }

    private void hide(){
        playButton.setVisibility(GONE);
        pauseButton.setVisibility(GONE);
    }

    private void show(){
        if(isPlaying) {
            pauseButton.setVisibility(VISIBLE);
        }else {
            playButton.setVisibility(VISIBLE);
        }
    }

}
