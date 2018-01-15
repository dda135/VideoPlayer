package fanjh.mine.player.view;

public interface VideoViewListener {
    void onPause();

    void onPlay();

    void onStop();

    void onSeekChanged(int progress, boolean fromUser);

    void onStartTrackingTouch();

    void onStopTrackingTouch();

    void fullScreen();

    void recoverNormal();

    void smallWindow(int width,int height);

}