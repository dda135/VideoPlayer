package fanjh.mine.player;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.view.ViewGroup;

/**
* @author fanjh
* @date 2018/1/10 15:51
* @description 播放器客户端入口
* @note
**/
public interface VideoPlayerClient{
    int STATE_COMMON = 1;
    int STATE_FULL_SCREEN = 2;
    int STATE_SMALL_WINDOW = 3;
    @IntDef({STATE_COMMON,STATE_FULL_SCREEN,STATE_SMALL_WINDOW})
    @interface ShowState{}

    /**
     * 用于关联父布局
     * 将视频播放器之间添加到当前布局中
     * 默认会填充满整个父布局
     * @param viewGroup 当前播放器的父布局
     */
    void attachView(ViewGroup viewGroup);

    /**
     * 全屏展示播放器
     */
    void fullScreenShow();

    /**
     * 小窗口展示播放器
     * @param width 当前要制定的宽度
     * @param height 当前要制定的高度
     */
    void smallWindowShow(int width,int height);

    /**
     * 移除当前视图，前提上要先attachView、fullScreenShow或者smallWindowShow
     * 否则没什么意义
     */
    void detachView();

    /**
     * 同步Activity的生命周期，用于销毁数据
     */
    void onActivityDestroy();

    /**
     * 同步Activity的生命周期，做一些处理
     */
    void onActivityStop();

    /**
     * 设置视图是否可见，用于处理可见或不可见时的操作
     * @param visible 当前是否可见
     */
    void setViewVisible(boolean visible);

    /**
     * 设置视频数据源
     * @param uri 数据源地址
     * @throws DataResourceException 资源准备中出现异常
     */
    void setDataResource(Uri uri) throws DataResourceException;

}
