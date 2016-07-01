package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        Dlog.i( "onCreate()");
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
                if(group1ButtonName != null) {
                    Intent groupActivityIntent = new Intent(MainActivity.this, GroupActivity.class);

                    groupActivityIntent.putExtra("GROUP1_NAME", group1ButtonName);
                    startActivity(groupActivityIntent);
                }
            }
        });
        if(savedInstanceState != null) {
            group1Button.setText(savedInstanceState.getString("GROUP1_NAME"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("GROUP1_NAME", group1Button.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dlog.i( "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dlog.i( "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Dlog.i( "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dlog.i( "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Dlog.i( "onStop()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_GROUP_REQUEST) {
            if(resultCode == RESULT_OK) {
                String name = data.getStringExtra("RESULT_NEW_GROUP_NAME");
                group1Button.setText(name);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.i( "onDestroy()");
    }
}
