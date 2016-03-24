package com.leo.mygame2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends Activity implements View.OnClickListener{

    private Button bt_option_back;
    private Button bt_option_done;
    private Button bt_option_setline;
    private Button bt_option_settarget;
    private int target;
    private int line;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_option);

        bt_option_back = (Button) findViewById(R.id.bt_option_back);
        bt_option_done = (Button) findViewById(R.id.bt_option_done);
        bt_option_setline = (Button) findViewById(R.id.bt_option_setline);
        bt_option_settarget = (Button) findViewById(R.id.bt_option_settarget);


        bt_option_back.setOnClickListener(this);
        bt_option_done.setOnClickListener(this);
        bt_option_setline.setOnClickListener(this);
        bt_option_settarget.setOnClickListener(this);

        application = (MyApplication) getApplication();

        line = application.getLineNumber();
        target = application.getTarget();

        bt_option_setline.setText(line + "");
        bt_option_settarget.setText(target+"");

    }

    public void contactMe(View view) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://15875512636"));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_option_back:


                finish();

                break;

            case R.id.bt_option_done:

                done();
                break;

            case R.id.bt_option_setline:

                setLine();
                break;

            case R.id.bt_option_settarget:

                setTarget();
                break;

        }
    }

    private void done() {


        application.setLineNumber(line);
        application.setTarget(target);
        finish();
    }

    private void setTarget() {

        final String [] items = {"1024", "2048", "4096"};

        new AlertDialog.Builder(this)
                .setTitle("Change Target")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        target = Integer.parseInt(items[which]);
                        bt_option_settarget.setText(target+"");


                    }
                }).show();

    }

    private void setLine() {

        final String [] items = {"4", "5", "6"};
        new AlertDialog.Builder(this)
                .setTitle("Change Line")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        line = Integer.parseInt(items[which]);
                        bt_option_setline.setText(line+"");

                    }
                }).show();

    }

}
