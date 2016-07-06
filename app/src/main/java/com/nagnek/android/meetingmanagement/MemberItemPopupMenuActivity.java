package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MemberItemPopupMenuActivity extends PopupActivity {
    public final static int REQ_EDIT_MEMBER_CODE = 20;
    public final static int RESULT_CODE_DELETE_MEMBER = 30;
    public final static int RESULT_CODE_EDIT_MEMBER = 50;
    public static final String EDIT_MEMBER_NAME = "com.nagnek.android.meetingmanagement.EDIT_MEMBER_NAME";
    public static final String EDIT_MEMBER_PHONE = "com.nagnek.android.meetingmanagement.EDIT_MEMBER_PHONE";
    public static final String EDIT_MEMBER_IMAGE_URI = "com.nagnek.android.meetingmanagement.EDIT_MEMBER_IMAGE_URI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_item_popup_menu);
        Button editButton = (Button) findViewById(R.id.edit_button);
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberItemPopupMenuActivity.this, EditMemberActivity.class);
                Intent receivedIntent = getIntent();
                int position = receivedIntent.getIntExtra(GroupActivity.MEMBER_LIST_POSITION, 0);
                Member member = new Member();
                member.name = receivedIntent.getStringExtra(GroupActivity.MEMBER_NAME);
                member.phone_number = receivedIntent.getStringExtra(GroupActivity.MEMBER_PHONE);
                member.imageUri = receivedIntent.getParcelableExtra(GroupActivity.MEMBER_IMAGE_URI);
                Dlog.i("member" + member.name);
                intent.putExtra(EDIT_MEMBER_NAME, member.name);
                intent.putExtra(EDIT_MEMBER_IMAGE_URI, member.imageUri);
                intent.putExtra(EDIT_MEMBER_PHONE, member.phone_number);
                intent.putExtra(GroupActivity.MEMBER_LIST_POSITION, position);
                startActivityForResult(intent, REQ_EDIT_MEMBER_CODE);
                member = null;
                //TODO: finish후에 불리는 생명주기는? onDestroy? onStop? 메모리해제는 어디서?
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CODE_DELETE_MEMBER, intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_EDIT_MEMBER_CODE) {
            if(resultCode == RESULT_OK) {
                setResult(RESULT_CODE_EDIT_MEMBER, data);
                finish();
            }
        }
    }
}
