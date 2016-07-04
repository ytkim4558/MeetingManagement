package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MemberItemPopupMenuActivity extends PopupActivity {
    public final static String EDIT_MEMBER_KEY = "com.nagnek.android.meetingmanagement.EDIT_MEMBER";
    public final static int REQ_EDIT_MEMBER_CODE = 20;
    public final static int RESULT_CODE_DELETE_MEMBER = 30;
    public final static int RESULT_CODE_EDIT_MEMBER = 50;
    Member member=null;


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
                int position = getIntent().getIntExtra(GroupActivity.MEMBER_LIST_POSITION_KEY, 0);
                intent.putExtra(GroupActivity.MEMBER_LIST_POSITION_KEY, position);
                intent.putExtra(GroupActivity.SELECT_MEMBER_LIST_ITEM, member);
                startActivityForResult(intent, REQ_EDIT_MEMBER_CODE);
                //TODO: finish후에 불리는 생명주기는? onDestroy? onStop? 메모리해제는 어디서?
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CODE_DELETE_MEMBER, intent);
                Dlog.i("delete");
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
