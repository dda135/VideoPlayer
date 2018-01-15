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
import android.widget.TextView;

import fanjh.mine.player.R;
import fanjh.mine.player.VideoPlayerClient;

/**
* @author fanjh
* @date 2018/1/12 10:47
* @description 正常模式下的播放器工具栏
* @note
**/
public class CommonToolbar extends BaseToolbarView{
    public static final int HIDDEN_DELAY_TIME = 3000;
    public static final int MSG_HIDDEN_TOOLBAR = 1;
    private Button playButton;
    private Button stopButton;
    private Button fullScreenButton;
    private VideoSeekBar progressBar;
    private TextView currentDurationText;
    private TextView sumDurationText;
    private Button pauseButton;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_HIDDEN_TOOLBAR:
                    if(canAutoHiddenToolbar) {
                        setVisibility(GONE);
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

    public CommonToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CommonToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_common_toolbar,this,true);
        playButton = findViewById(R.id.btn_play);
        stopButton = findViewById(R.id.btn_stop);
        progressBar = findViewById(R.id.sb_play_progress);
        currentDurationText = findViewById(R.id.tv_now_time);
        sumDurationText = findViewById(R.id.tv_all_time);
        pauseButton = findViewById(R.id.btn_pause);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    videoViewListener.onPlay();
                }
                setSeekCanTouch(true);
                canAutoHiddenToolbar = true;
                autoHiddenToolbar();
            }
        });
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    videoViewListener.onPause();
                }
                showToolbar();
                canAutoHiddenToolbar = false;
                cancelHiddenToolbar();
            }
        });
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    videoViewListener.onStop();
                }
                setSeekCanTouch(false);
                canAutoHiddenToolbar = false;
                showToolbar();
                cancelHiddenToolbar();
            }
        });
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
                cancelHiddenToolbar();
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
        setSeekCanTouch(false);
        fullScreenButton = findViewById(R.id.btn_full_screen);
        fullScreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != videoViewListener){
                    switch (currentShowState) {
                        case VideoPlayerClient.STATE_COMMON:
                            videoViewListener.fullScreen();
                            break;
                        case VideoPlayerClient.STATE_FULL_SCREEN:
                            videoViewListener.recoverNormal();
                            break;
                        case VideoPlayerClient.STATE_SMALL_WINDOW:
                            videoViewListener.fullScreen();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void setProgressBarMax(int maxDuration){
        progressBar.setMax(maxDuration);
        sumDurationText.setText(formatDuration(maxDuration));
    }

    @Override
    public void updateProgressBar(int nowDuration){
        progressBar.setProgress(nowDuration);
        currentDurationText.setText(formatDuration(nowDuration));
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
    }

    @Override
    public void onRecoverNormal(boolean isPlaying, int currentDuration, int maxDuration,int bufferedValue) {
        currentShowState = VideoPlayerClient.STATE_COMMON;
        setProgressBarMax(maxDuration);
        updateProgressBar(currentDuration);
        updateBufferBar(bufferedValue);
    }

    @Override
    public void onSmallWindow(boolean isPlaying, int currentDuration, int maxDuration,int bufferedValue) {
        currentShowState = VideoPlayerClient.STATE_SMALL_WINDOW;
        setProgressBarMax(maxDuration);
        updateProgressBar(currentDuration);
        updateBufferBar(bufferedValue);
    }

    @Override
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    private String formatDuration(int duration){
        StringBuilder stringBuilder = new StringBuilder();
        int second = duration / 1000;
        int minute = second / 60;
        int hour = minute / 60;
        stringBuilder.append(hour).
                append(":").
                append(minute % 60).
                append(":").
                append(second % 60);
        return stringBuilder.toString();
    }

    @Override
    public void showToolbar(){
        if(!isShowing()) {
            handler.removeMessages(MSG_HIDDEN_TOOLBAR);
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void autoHiddenToolbar(){
        if(isShowing() && canAutoHiddenToolbar) {
            handler.sendEmptyMessageDelayed(MSG_HIDDEN_TOOLBAR, HIDDEN_DELAY_TIME);
        }
    }

    public void cancelHiddenToolbar(){
        if(isShowing()) {
            handler.removeMessages(MSG_HIDDEN_TOOLBAR);
        }
    }

    private void setSeekCanTouch(boolean canTouch){
        progressBar.setSeekCanTouch(canTouch);
    }

}
