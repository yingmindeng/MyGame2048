package com.leo.mygame2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 2016/3/21.
 */

//将所有的ui布局和业务逻辑封装到GameView
public class GameView extends GridLayout {


    private static final String TAG = "GameView";
    private int mColumnNumber = 4;
    private int mRowNumber = 4;
    private int mTarget;
    private Home mHome;


    //用于计算上下左右滑动
    float startX;
    float startY;
    float endX;
    float endY; //不能在下面的方法中声明，因为下面声明的是局部变量，必须初始化，比如0，
    // 会导致每次的事件都会初始化为0,就实现不了效果


    //用于计算滑动之后每行或者每列合并后的数组
    List<Integer> caculatorList;

    private NumberItem[][] NumberItemMatrix;

    //记录上一步操作的矩阵
    private int[][] historyMatrix;

    //决定是否可以撤销的标志位
    boolean canRevert = false;

    //保存当前分数的一个成员变量
    private int currentScore;

    private int highestScore;

    SharedPreferences sp;

    private List<Point> blankList;
    private int width;
    private int height;


    public GameView(Context context) {
        super(context);
        init();
    }


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {



        setBackgroundColor(Color.GRAY);


        //设置GameView在RelativeLayout的CENTER_VERTICAL位置;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        setLayoutParams(params);


        //得到当前Activity的引用
        mHome = Home.getActivity();

        MyApplication application = (MyApplication) mHome.getApplication();
        mRowNumber = application.getLineNumber();
        mColumnNumber = mRowNumber;
        mTarget = application.getTarget();


        //restart也必须置为false，不然会出现bug
        canRevert = false;


        caculatorList = new ArrayList<>();

        blankList = new ArrayList<Point>();
        NumberItemMatrix = new NumberItem[mRowNumber][mColumnNumber];

        historyMatrix = new int[mRowNumber][mColumnNumber];

        currentScore = 0;


        sp = getContext().getSharedPreferences("config", getContext().MODE_PRIVATE);

        highestScore = sp.getInt("HighestRecord", 0);
        Log.i("Home", "最高纪录为："+highestScore);

        //获取屏幕的宽度
        WindowManager windowManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        width = metrics.widthPixels;

        height = metrics.heightPixels;

        setOrientation(HORIZONTAL);
        setRowCount(mRowNumber);
        setColumnCount(mColumnNumber);


/*        // 像xml一样设置MATCH_PARENT，WRAP_CONTENT
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);*/

        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {

                NumberItem numberItem = new NumberItem(getContext(), 0);

                //numberItem.setTextNumber(2);

                addView(numberItem, width / mColumnNumber, width / mColumnNumber);//可以指定增加的子控件宽高

                //把每次new出来的item引用保存在一个二维矩阵里面
                NumberItemMatrix[i][j] = numberItem;

                //初始化的时候记录当前空白的位置

                Point point = new Point();
                point.x = i;
                point.y = j;
                blankList.add(point);

            }

        }



        //继续初始化棋盘的view，一开始看到的时候，里面应有随机出现的两个数字不为0;
        //有一个东西来记录当前棋盘的空白位置

        addRandomNumber();

        addRandomNumber();


    }

    //在棋盘的空白位置上，随机找到一个位置，给它的item设置一个数

    private void addRandomNumber() {

        updateBlankList();
        int size = blankList.size();
        int location = (int) Math.floor(Math.random() * size);

        Point point = blankList.get(location);

        NumberItemMatrix[point.x][point.y].setTextNumber(Math.random() > 0.5d ? 2 : 4);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.myscale);
        NumberItemMatrix[point.x][point.y].startAnimation(animation);


    }

    private void updateBlankList() {

        blankList.clear();
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {

                NumberItem item = NumberItemMatrix[i][j];
                if (item.getNumber() == 0) {
                    blankList.add(new Point(i, j));
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:


                startX = event.getX();
                startY = event.getY();

                Log.i(TAG, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:

                Log.i(TAG, "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:

                endX = event.getX();
                endY = event.getY();
                Log.i(TAG, "ACTION_UP");



                float dx = Math.abs(endX - startX);
                float dy = Math.abs(endY - startY);

                if ((dx + dy) > width / 8) {

                    saveHistory();

                    judgeDirection(startX, startY, endX, endY);

                    //判断游戏是否结束 1.可以继续玩 2.成功了 3.gameover

                    updateCurrentScore();
                    handleResult(isOver());
                }





                break;
        }

        return true;//改为true
        //Down Move ...Move Up 当一个事件down给了某个控件后，后续的一序列时间都要给它处理
        //表示当前控件来处理 这个触摸事件的序列
    }

    //更新当前的分数
    public void updateCurrentScore() {


        mHome.updateCurrentScore(currentScore);

    }


    //恢复上一步的状态
    public void revert() {

        if (canRevert) {
            for (int i = 0; i < mRowNumber; i++) {
                for (int j = 0; j < mColumnNumber; j++) {
                    NumberItemMatrix[i][j].setTextNumber(historyMatrix[i][j]);
                }
            }
        }

    }

    //把当前的记录保存到historyMatrix
    private void saveHistory() {

        //方法1，遍历一遍history矩阵，如果里面全是0，就直接return。


        //方法2，添加一个flag，当且仅当histroy矩阵有过赋值之后，才置为1.
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {

                historyMatrix[i][j] = NumberItemMatrix[i][j].getNumber();
            }
        }

        canRevert = true;


    }

    public void handleResult(int result) {
        if (result == 2) { //完成游戏

            if (highestScore < currentScore) {

                highestScore = currentScore;
                // sp.edit().putInt("highestScore", highestScore);
                //更新home界面的Record
                MyApplication app = (MyApplication) mHome.getApplication();
                app.setHighestRecord(highestScore);
                mHome.updateHighestScore(highestScore);

            }

            new AlertDialog.Builder(getContext())
                    .setTitle("恭喜")
                    .setMessage("您已经完成游戏")
                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            restart();
                        }
                    })
                    .setNegativeButton("挑战更难", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHome.option();

                        }
                    }).show();

        } else if (result == 3) {
            new AlertDialog.Builder(getContext()).
                    setTitle("失败")
                    .setMessage("游戏结束")
                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restart();

                        }
                    })
                    .setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //为了取得Activity，在Home里声明一个Activity的引用，通过静态方法就可以拿到
                            mHome.finish();//把当前Activity干掉;
                        }
                    })
                    .show();
        } else {    //1 表示继续，则给出一个随机数
            addRandomNumber();
        }


    }

    public void restart() {


        removeAllViews();
        init();
        updateCurrentScore();
    }

    //1.可以继续玩 2.成功了 3.gameover
    public int isOver() {


        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {

                if (NumberItemMatrix[i][j].getNumber() == mTarget) {
                    return 2;
                }
            }
        }


        //上面循环走完，说明没有成功

        updateBlankList();
        if (blankList.size() == 0) {

            //这种情况下如果还有可以合并的，则返回1


            for (int i = 0; i < mRowNumber; i++) {
                for (int j = 0; j < mColumnNumber - 1; j++) {

                    int current = NumberItemMatrix[i][j].getNumber();
                    int next = NumberItemMatrix[i][j + 1].getNumber();

                    if (current == next) {
                        return 1;
                    }

                }
            }


            for (int i = 0; i < mRowNumber; i++) {
                for (int j = 0; j < mColumnNumber - 1; j++) {

                    int current = NumberItemMatrix[j][i].getNumber();
                    int next = NumberItemMatrix[j + 1][i].getNumber();

                    if (current == next) {
                        return 1;
                    }

                }
            }


            //如果没有可以合并的，则返回3

            return 3;
        }

        return 1;

    }




    private void judgeDirection(float startX, float startY, float endX, float endY) {

        float dx = Math.abs(endX - startX);
        float dy = Math.abs(endY - startY);

        boolean flag = dx > dy ? true : false;


        if (flag) {//水平方向滑动
            if (endX > startX) {

                slideRight();
                Log.i(TAG, "slide right");
            } else {

                slideLeft();
                Log.i(TAG, "slide left");
            }
        } else {
            if (endY > startY) {

                slideDown();
                Log.i(TAG, "slide down");
            } else {

                slideUp();
                Log.i(TAG, "slide up");
            }
        }
    }

    private void slideUp() {

        int preNumber = -1;
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {
                int number = NumberItemMatrix[j][i].getNumber();

                if (number != 0) {

                    if (number != preNumber && preNumber != -1) {

                        caculatorList.add(preNumber);

                    } else if (preNumber != -1) {  //  preNumber＝number

                        caculatorList.add(number * 2);
                        currentScore += number * 2;
                        preNumber = -1;
                        continue;
                    }

                    preNumber = number;
                }
            }

            //把最后一个preNumber加入集合中来,比如 2，2，4，4这样的，防止将preNumber为-1的加进去
            //还有就是一列全部为0的情况
            if (preNumber != -1) {
                caculatorList.add(preNumber);
            }


            //把通过计算后合并的数字放到矩阵中
            for (int p = 0; p < caculatorList.size(); p++) {

                NumberItemMatrix[p][i].setTextNumber(caculatorList.get(p));
            }
            //合并长度之后的部分以0来填充
            for (int q = caculatorList.size(); q < mColumnNumber; q++) {
                NumberItemMatrix[q][i].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculatorList.clear();
            preNumber = -1;
        }

    }

    private void slideDown() {

        int preNumber = -1;
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = mColumnNumber - 1; j >= 0; j--) {
                int number = NumberItemMatrix[j][i].getNumber();

                if (number != 0) {

                    if (number != preNumber && preNumber != -1) {

                        caculatorList.add(preNumber);

                    } else if (preNumber != -1) {  //  preNumber＝number

                        caculatorList.add(number * 2);
                        currentScore += number * 2;
                        preNumber = -1;
                        continue;
                    }

                    preNumber = number;
                }
            }

            if (preNumber != -1) {
                caculatorList.add(preNumber);
            }


            //把通过计算后合并的数字放到矩阵中
            for (int p = mColumnNumber - 1; p >= mColumnNumber - caculatorList.size(); p--) {

                NumberItemMatrix[p][i].setTextNumber(caculatorList.get(mColumnNumber - 1 - p));
            }
            //合并长度之后的部分以0来填充
            for (int q = 0; q <= mColumnNumber - 1 - caculatorList.size(); q++) {
                NumberItemMatrix[q][i].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculatorList.clear();
            preNumber = -1;
        }


    }

    private void slideLeft() {

        int preNumber = -1;
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = 0; j < mColumnNumber; j++) {
                int number = NumberItemMatrix[i][j].getNumber();

                if (number != 0) {

                    if (number != preNumber && preNumber != -1) {

                        caculatorList.add(preNumber);

                    } else if (preNumber != -1) {  //  preNumber＝number

                        caculatorList.add(number * 2);
                        currentScore += number * 2;
                        preNumber = -1;
                        continue;
                    }

                    preNumber = number;
                }
            }

            //把最后一个preNumber加入集合中来,比如 2，2，4，4这样的，防止将preNumber为-1的加进去
            //还有就是一列全部为0的情况
            if (preNumber != -1) {
                caculatorList.add(preNumber);
            }


            //把通过计算后合并的数字放到矩阵中
            for (int p = 0; p < caculatorList.size(); p++) {

                NumberItemMatrix[i][p].setTextNumber(caculatorList.get(p));
            }
            //合并长度之后的部分以0来填充
            for (int q = caculatorList.size(); q < mColumnNumber; q++) {
                NumberItemMatrix[i][q].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculatorList.clear();
            preNumber = -1;
        }


    }

    private void slideRight() {

        int preNumber = -1;
        for (int i = 0; i < mRowNumber; i++) {
            for (int j = mColumnNumber - 1; j >= 0; j--) {
                int number = NumberItemMatrix[i][j].getNumber();

                if (number != 0) {

                    if (number != preNumber && preNumber != -1) {

                        caculatorList.add(preNumber);

                    } else if (preNumber != -1) {  //  preNumber＝number

                        caculatorList.add(number * 2);
                        currentScore += number * 2;
                        preNumber = -1;
                        continue;
                    }

                    preNumber = number;
                }
            }

            //把最后一个preNumber加入集合中来,比如 2，2，4，4这样的，防止将preNumber为-1的加进去
            //还有就是一列全部为0的情况
            if (preNumber != -1) {
                caculatorList.add(preNumber);
            }


            //把通过计算后合并的数字放到矩阵中
            for (int p = mColumnNumber - 1; p >= mColumnNumber - caculatorList.size(); p--) {

                NumberItemMatrix[i][p].setTextNumber(caculatorList.get(mColumnNumber - 1 - p));
            }
            //合并长度之后的部分以0来填充
            for (int q = mColumnNumber - caculatorList.size() - 1; q >= 0; q--) {
                NumberItemMatrix[i][q].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculatorList.clear();
            preNumber = -1;
        }


    }


}
