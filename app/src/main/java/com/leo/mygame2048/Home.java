package com.leo.mygame2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Home extends Activity implements View.OnClickListener {

    private  static Home mActivity;
    private GameView gameView;
    private Button bt_home_revert;
    private Button bt_home_restart;
    private Button bt_home_option;
    private TextView tv_home_score;
    private TextView tv_home_record;
    private TextView tv_home_target;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        mActivity = this;

        RelativeLayout rl_home_center = (RelativeLayout) findViewById(R.id.rl_home_center);

        gameView = new GameView(this);

        rl_home_center.addView(gameView);

        //初始化button
        bt_home_revert = (Button) findViewById(R.id.bt_home_revert);
        bt_home_restart = (Button) findViewById(R.id.bt_home_restart);
        bt_home_option = (Button) findViewById(R.id.bt_home_option);

        bt_home_revert.setOnClickListener(this);
        bt_home_restart.setOnClickListener(this);
        bt_home_option.setOnClickListener(this);

        //初始化显示当前分数的TextView
        tv_home_score = (TextView) findViewById(R.id.tv_home_score);
        tv_home_record = (TextView) findViewById(R.id.tv_home_record);
        tv_home_target = (TextView) findViewById(R.id.tv_home_target);

        application = (MyApplication) getApplication();

        Log.i("Home", "下面有坑，注意setText必须是字符串形式");
        //根据application里去获取sharedpreference取得之前用户保存的设置
        tv_home_target.setText(application.getTarget()+"");
        tv_home_record.setText(application.getHighestRecord()+"");


/*        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;

            }
        });*/

/*
        GridLayout gl_home_content = (GridLayout) findViewById(R.id.gl_home_content);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        //之前的
//        int width = display.getWidth();
//        int height = display.getHeight();
        //之后
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width =metrics.widthPixels;
       // Log.i("Home", width + ":" + height);
        Log.i("Home", width +"");

        //初始化中间的内容布局
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
               // TextView tv = new MyTextView(this);
               // tv.setText(i+","+j);

                NumberItem numberItem = new NumberItem(this,0);

                //这里的50 应该变成动态获取屏幕宽度，然后除以GridLayout的列数
                gl_home_content.addView(numberItem,width/4,width/4);//可以指定增加的子控件宽高

            }
        }*/

    }


    public static Home getActivity() {
        return mActivity;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_home_revert:
                revert();
                break;

            case R.id.bt_home_restart:
                restart();
                break;

            case R.id.bt_home_option:
                option();
                break;
        }
    }

    public void option() {

        startActivityForResult(new Intent(this, OptionActivity.class), 100);
    }

    private void restart() {

        new AlertDialog.Builder(this)
                .setTitle("确认")
                .setMessage("真的要重新开始吗？")
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.restart();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    private void revert() {

        gameView.revert();

    }



    public void updateCurrentScore(int score) {
        tv_home_score.setText(score + "");
    }

    public void updateHighestScore(int record) {
        tv_home_record.setText(record+"");
    }

    public void updateTargetScore(int score) {
        tv_home_target.setText(score+"");
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

     //   if (requestCode == 100 && resultCode == RESULT_OK) {

            updateTargetScore(application.getTarget());
            gameView.restart();

       // }
    }
}
