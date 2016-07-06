package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;

public class MainActivity extends Activity {
    static final int NEW_GROUP_REQUEST = 1;
    static final int NEW_GROUP_GENERATE = 2;
    static final int NEW_GROUP_FALSE = 3;
    String groupName = null;
    private Button group1Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dlog.i("onCreate()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        group1Button = (Button) findViewById(R.id.group1_button);
        Button addGroupButton = (Button) findViewById(R.id.add_group_button);
        addGroupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroupActivityIntent = new Intent(MainActivity.this, NewGroupPopupActivity.class);
                startActivityForResult(newGroupActivityIntent, NEW_GROUP_REQUEST);
            }
        });
        group1Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group1ButtonName = group1Button.getText().toString();
                if (group1ButtonName != null) {
                    Intent groupActivityIntent = new Intent(MainActivity.this, GroupActivity.class);
                    groupActivityIntent.putExtra("GROUP1_NAME", group1ButtonName);
                    startActivity(groupActivityIntent);
                }
            }
        });
        if (savedInstanceState != null) {
            group1Button.setText(savedInstanceState.getString("GROUP1_NAME"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Dlog.showToast) Toast.makeText(this, Dlog.s("그룹네임 저장"), Toast.LENGTH_SHORT).show();
        outState.putString("GROUP1_NAME", group1Button.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dlog.i("onStart()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dlog.i("onResume()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Dlog.i("onRestart()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dlog.i("onPause()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Dlog.i("onStop()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Dlog.i("onActivityResult()");
        if (requestCode == NEW_GROUP_REQUEST) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("RESULT_NEW_GROUP_NAME");
                group1Button.setText(name);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.i("onDestroy()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }
}
