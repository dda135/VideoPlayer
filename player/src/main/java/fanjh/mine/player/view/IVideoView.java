package fanjh.mine.player.view;

import android.view.View;

/**
* @author fanjh
* @date 2018/1/12 14:15
* @description 普通的视频视图抽象
* @note
**/
public interface IVideoView {

    /**
     * 当前变为全屏的时候回调
     * @param isPlaying true表示当前播放中
     */
    void onFullScreen(boolean isPlaying);

    /**
     * 当从全屏回复为正常的时候回调
     * @param isPlaying true表示当前播放中
     */
    void onRecoverNormal(boolean isPlaying);

    /**
     * 当前变为小窗口模式的时候回调
     * @param isPlaying true表示当前播放中
     */
    void onSmallWindow(boolean isPlaying);

    /**
     * 获得当前视频播放所需要展示的视图
     * @return 视频播放视图
     */
    View getView();

    /**
     * 播放进度变化的时候回调
     * @param millis 当前已经播放的毫秒数
     */
    void onPlayingMillisUpdate(int millis);

    /**
     * 缓冲进度变化的时候回调
     * @param value 当前已经缓冲的比例，0-100
     */
    void onBufferedValueUpdate(int value);

    /**
     * 当播放资源准备完成时回调
     * @param totalDuration 当前资源的总时长
     */
    void onDataResourcePrepared(int totalDuration);

    /**
     * 设置一系列视图可能触发的操作监听
     * @param videoViewListener 操作监听
     */
    void setVideoViewListener(VideoViewListener videoViewListener);

    /**
     * 用于展示工具栏
     * @param shouldAutoHidden true表示展示之后过一段时间应该自动隐藏
     */
    void showToolbar(boolean shouldAutoHidden);

    /**
     * 播放中出现异常后回调
     */
    void onPlayingError();

}
