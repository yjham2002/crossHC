package kr.co.picklecode.crossmedia.hiddencatch.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by HP on 2018-07-20.
 */

public class TouchableImageView extends AppCompatImageView {

    private OnTouchBack onTouchBack;

    public void setOnTouchBack(OnTouchBack onTouchBack){
        this.onTouchBack = onTouchBack;
    }

    public TouchableImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public TouchableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TouchableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(onTouchBack != null){
            onTouchBack.onTouch(this, event.getAction(), event.getX(), event.getY());
        }
        return true;
    }

}
