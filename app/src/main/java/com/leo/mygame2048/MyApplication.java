package com.leo.mygame2048;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Leo on 2016/3/22.
 */


//一般用该类来保存app的全局信息(全局共享数据)

//还可以用来保存app挂掉的信息（留遗言）

//当当前application 当前应用创建的时候创建。每次app启动的时候都会创建。

public class MyApplication extends Application {

    private int LineNumber;
    private int HighestRecord;
    private int Target;

    public int getTarget() {
        return Target;
    }

    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences("config", MODE_PRIVATE);

        LineNumber = sp.getInt("LineNumber", 4);
        HighestRecord = sp.getInt("HighestRecord", 0);
        Target = sp.getInt("Target", 2048);

        Log.i("MyApplication", LineNumber + "--" + HighestRecord + "--" + Target);

    }


    public int getLineNumber() {
        return LineNumber;
    }

    public int getHighestRecord() {
        return HighestRecord;
    }

    public void setLineNumber(int lineNumber) {
        LineNumber = lineNumber;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("LineNumber", LineNumber);
        editor.commit();
    }

    public void setHighestRecord(int highestRecord) {
        HighestRecord = highestRecord;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("HighestRecord", HighestRecord);
        editor.commit();
    }

    public void setTarget(int target) {
        Target = target;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Target", target);
        editor.commit();
    }
}
