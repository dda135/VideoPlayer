package fanjh.mine.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import fanjh.mine.player.R;

/**
* @author fanjh
* @date 2018/1/12 11:31
* @description
* @note
**/
public class VideoSeekBar extends SeekBar{
    private boolean canTouch;

    public VideoSeekBar(Context context) {
        this(context,null);
    }

    public VideoSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setProgressDrawable(getResources().getDrawable(R.drawable.bg_seekbar));
        setThumb(getResources().getDrawable(R.drawable.thumb_seekbar));
    }

    public void setSeekCanTouch(boolean canTouch){
        this.canTouch = canTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return canTouch && super.onTouchEvent(event);
    }
}
