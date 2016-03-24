package com.leo.mygame2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Leo on 2016/3/21.
 */
public class NumberItem extends FrameLayout {

    private TextView mTv;
    private int number;


    //一般在代码中new出该控件的时候，会使用这个构造方法初始化
    public NumberItem(Context context) {
        super(context);
        initView(0);
    }

    //这个构造方法一般是给系统调用，系统通过该方法实例化控件的时候，会把xml里定义的属性一并传进来，供控件使用
    public NumberItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(0);
    }

    public NumberItem(Context context, int number) {
        super(context);

        initView(number);
    }

    private void initView(int number) {

        //给NumberItem设置一个背景色
        //setBackgroundColor(Color.GRAY);
        setBackgroundColor(0xB7ADA3);

        this.number = number;


        mTv = new TextView(getContext());

        mTv.setGravity(Gravity.CENTER);

        //给控件mTv设置一些属性参数,注意LayoutParams是android.widget.FrameLayout里面的
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);//这里的5表示5个像素(在代码里给控件设置的所有数字，如无单位，默认单位是px)
/*        mTv.setText(number + "");
        mTv.setBackgroundColor(Color.WHITE);*/

        setTextNumber(number);
        addView(mTv, params);

    }

    public void setTextNumber(int number) {

        //更改控件显示数字的同时，应该把里面保存数字值的number同时更改
        this.number = number;
        if (number == 0) {
            mTv.setText("");
        } else {
            mTv.setText(number+"");
        }

        switch (number) {
            case 0:
               // mTv.setBackgroundColor(0x00000000); //ARGB
                mTv.setBackgroundResource(R.drawable.num_0);
                break;
            case 2:
                mTv.setBackgroundResource(R.drawable.num_2);
                break;
            case 4:
                mTv.setBackgroundResource(R.drawable.num_4);

                break;
            case 8:
                mTv.setBackgroundResource(R.drawable.num_8);
                break;
            case 16:
                mTv.setBackgroundResource(R.drawable.num_16);
                break;
            case 32:
                mTv.setBackgroundResource(R.drawable.num_32);
                break;
            case 64:
                mTv.setBackgroundResource(R.drawable.num_64);
                break;
            case 128:
                mTv.setBackgroundResource(R.drawable.num_128);
                break;
            case 256:
                mTv.setBackgroundResource(R.drawable.num_256);
                break;
            case 512:
                mTv.setBackgroundResource(R.drawable.num_512);
                break;
            case 1024:
                mTv.setBackgroundResource(R.drawable.num_1024);
                break;
            case 2048:
                mTv.setBackgroundColor(0xFFFF1493);
                break;
            case 4096:
                mTv.setBackgroundColor(0xFFFF3030);
                break;
        }
    }

    //向外界返回当前控件内保存的数字
    public int getNumber() {
        return this.number;
    }
}
