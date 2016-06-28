package com.nagnek.android.mettingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupActivity extends Activity {

    static final int PICK_CONTACT_REQUEST = 1;
    private TextView memberNameView = null;
    private String number = null;

    // 연락처 선택
    private void pickContact() {
        // TODO: Uri 공부할것
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        // setType은 뭐야.

        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        // PICK_CONTACT_REQUEST, requestCode가 결과를 얻는데 사용된다
        startActivityForResult( pickContactIntent, PICK_CONTACT_REQUEST);
    }

    // 전화걸기
    private void call() {
        if(number != null) {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+number)));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Log.e("GroupActivity", "onCreate()");
        Button addMemberButton = (Button)findViewById(R.id.add_member_button);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult에서 넘긴 requestCode를 체크한다
        if(requestCode == PICK_CONTACT_REQUEST) {
            if( resultCode == RESULT_OK) {
                // 선택한 결과는 Uri 리턴되며 해당 Uri를 쿼리하여 얻어오게 된다
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, //연락처
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // 연락처 이

                // 주의 : UI의 블락킹 때문에라도(화면 버벅거림 쿼리 실행은 별도의 스레드에서 처리하는게 좋다
                Cursor cursor = getContentResolver().query( contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // getColumnIndex로 꼭 가져와야하나 바로 가져올수없나 특히 아래 이름의 경우 getString 같은걸로
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                number = cursor.getString(column);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameIndex);

                // 선택한 연락처의 이름을 TextView에 보여준다.
                memberNameView.setText(name);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("GroupActivity", "onStart()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e("GroupActivity", "onRestoreInstanceState()");
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 사용자 이름 및 전화번호를 복원한다.
        if (savedInstanceState != null) {
            Log.e("GroupActivity", "RestoreName");
            Log.e("GroupActivity", savedInstanceState.getString("BACKUP_NAME"));
            Log.e("GroupActivity", savedInstanceState.getString("BACKUP_NUMBER"));
            memberNameView.setText(savedInstanceState.getString("BACKUP_NAME"));
            number = savedInstanceState.getString("BACKUP_NUMBER");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("GroupActivity", "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("GroupActivity", "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("GroupActivity", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("GroupActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("GroupActivity", "onDestroy()");
    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e("GroupActivity", "onSaveInstanceState()");
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if(memberNameView != null) {
            String backupName = memberNameView.getText().toString();
            outState.putString("BACKUP_NAME", backupName);
            Log.e("GroupActivity", "backupName");
        }
        if(number != null) {
            outState.putString("BACKUP_NUMBER", number);
            Log.e("GroupActivity", "backupNumber");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("GroupActivity", "onConfigurationChanged()");
    }
}
