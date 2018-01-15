package fanjh.mine.player;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.view.Surface;

/**
* @author fanjh
* @date 2018/1/12 14:04
* @description 播放控制器抽象
* @note
**/
public interface VideoPlayer {
    /**
     * 当前播放器还未初始化
     */
    int STATE_NO_INIT = 0;
    /**
     * 当前播放器已经初始化
     */
    int STATE_INITIAL = 1;
    /**
     * 当前播放器读取资源中
     * 也称准备中
     */
    int STATE_PREPARING = 2;
    /**
     * 当前播放器读取资源完成
     * 也称准备完成
     */
    int STATE_PREPARED = 3;
    /**
     * 当前播放器播放中
     */
    int STATE_PLAYING = 4;
    /**
     * 当前播放器处于播放暂停中
     */
    int STATE_PAUSE = 5;
    /**
     * 当前播放器处于播放停止中
     */
    int STATE_STOP = 6;
    /**
     * 当前播放器缓冲中
     */
    int STATE_BUFFERING = 8;
    /**
     * 当前播放器播放完成
     */
    int STATE_PLAY_COMPLETED = 100;
    /**
     * 当前播放器被释放，不可再用
     */
    int STATE_RELEASE = 101;

    @IntDef({STATE_NO_INIT,STATE_INITIAL,STATE_PREPARING,STATE_PREPARED,
            STATE_PLAYING,STATE_PAUSE,STATE_STOP,STATE_PLAY_COMPLETED,
            STATE_BUFFERING,STATE_RELEASE})
    @interface VideoState{}

    /**
     * 设置播放源
     * @param uri 播放源路径
     * @throws DataResourceException 资源加载出现异常
     */
    void setDataResource(Uri uri) throws DataResourceException;

    /**
     * 继续播放视频
     * 可能从之前暂停或者从头开始
     * @return true表示成功
     */
    boolean start();

    /**
     * 暂停播放
     * @return true表示成功
     */
    boolean pause();

    /**
     * 停止播放
     * @return true表示成功
     */
    boolean stop();

    /**
     * 释放播放器资源，后期不可再用
     * @return true表示成功
     */
    boolean release();

    /**
     * 用于获取当前播放进度
     * @return 当前已经播放的毫秒数
     */
    int getCurrentMillis();

    /**
     * 定位到指定毫秒数位置
     * @param newMillis 毫秒
     * @return true表示定位成功
     */
    boolean seekTo(int newMillis);

    /**
     * 播放器如果要正常播放，要关联对应的视图
     * @param object 类型请自己转换
     */
    void attachSurface(Object object);

    /**
     * 用于判断当前是否播放中
     * @return 当前是否播放中
     */
    boolean isPlaying();

    /**
     * 用于判断当前资源是否准备完成
     * @return true表示完成
     */
    boolean alreadyPrepared();

    interface OnPreparedListener{
        /**
         * 资源准备完成回调，资源准备是异步进行中的
         * @param autoStart 当前准备完成后是否应该开始播放
         * @param totalMillis 当前视频的总长度
         */
        void onPrepared(boolean autoStart,int totalMillis);
    }

    /**
     * 用于设置资源准备监听
     * @param listener 资源准备监听
     */
    void setOnPreparedListener(OnPreparedListener listener);

    interface OnBufferingListener{
        /**
         * 资源缓冲中回调
         * @param value 当前缓冲的数量，0-100
         */
        void onBuffered(int value);
    }

    /**
     * 用于监听缓冲结果
     * @param listener 监听
     */
    void setOnBufferingListener(OnBufferingListener listener);

    interface OnErrorListener{
        /**
         * 播放中出现异常
         * @param code1 错误码，参考MediaPlayer实现
         * @param code2 错误码，参考MediaPlayer实现
         */
        void onError(int code1,int code2);
    }

    /**
     * 用于监听播放失败
     * @param listener 监听
     */
    void setOnErrorListener(OnErrorListener listener);

    void onActivityStop();

}
