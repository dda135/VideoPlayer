package fanjh.mine.player.view;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
* @author fanjh
* @date 2018/1/12 14:43
* @description
* @note
**/
public interface ITextureView extends IVideoView{
    void setTextureViewListener(TextureView.SurfaceTextureListener listener);
    void setSurfaceTexture(SurfaceTexture surfaceTexture);
}
