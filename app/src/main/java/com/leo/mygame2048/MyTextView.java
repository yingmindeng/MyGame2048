package com.leo.mygame2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Leo on 2016/3/21.
 */

//自定义TextView，使它变成一个带边框的TextView
public class MyTextView extends TextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //先在画布上画上一个我们自定义的外框
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        //getMeasuredWidth()方法必须是控件已经调用过onMeasure方法测量自己的宽高之后才可以得到正确的值
        //为什么设置为1，1，-1，-1是因为这样设置比如容易看出效果
        canvas.drawRect(1, 1, getMeasuredWidth()-1, getMeasuredHeight()-1, paint);

        //然后让它自己画出TextView的内容和背景
        super.onDraw(canvas);

    }
}
