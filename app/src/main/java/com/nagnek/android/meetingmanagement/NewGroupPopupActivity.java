package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by yongtakpc on 2016. 6. 28..
 */
public class NewGroupPopupActivity extends PopupActivity {
    EditText newGroupName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 타이틀 제거
        setContentView(R.layout.activity_new_group);
        newGroupName = (EditText) findViewById(R.id.group_name);
        Button okButton = (Button) findViewById(R.id.ok_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("RESULT_NEW_GROUP_NAME", newGroupName.getText().toString());
                setResult(RESULT_OK, intent);
                Dlog.i("finish");
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                Dlog.i("finish");
                finish();
            }
        });
    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i( "onSaveInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if (newGroupName != null) {
            String backupName = newGroupName.getText().toString();
            outState.putString("BACKUP_NEW_GROUP_NAME", backupName);
            Dlog.i("backupNewGroupName");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Dlog.i( "onRestoreInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 사용자 이름 및 전화번호를 복원한다.
        if (savedInstanceState != null) {
            Dlog.i( savedInstanceState.getString("newGroupName"));
            newGroupName.setText(savedInstanceState.getString("BACKUP_NEW_GROUP_NAME"));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
