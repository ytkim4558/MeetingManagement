package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nagnek.android.debugLog.Dlog;

public class ListItemPopupMenuActivity extends PopupActivity {

    // MEMBER 편집 정보 관련 REQUEST_CODE 와 RESULT_CODE
    public final static int REQ_CODE_EDIT_MEMBER_INFO = 20;
    public final static int RESULT_CODE_EDIT_MEMBER_INFO = 30;
    public final static int RESULT_CODE_DELETE_MEMBER = 40;

    // 팝업에서 GROUP 편집 정보 관련 REQUEST_CODE 와 RESULT_CODE
    public final static int REQ_CODE_EDIT_GROUP_INFO = 50;
    public final static int RESULT_CODE_EDIT_GROUP_INFO = 60;
    public final static int RESULT_CODE_DELETE_GROUP_INFO = 70;

    // MEMBER INFO 편집 관련 변수
    public static final String EDIT_MEMBER_INFO = "com.nagnek.android.meetingmanagement.EDIT_MEMBER_INFO";
    public static final String WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY = "com.nagnek.android.meetingmanagement.WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY";

    // GROUP_INFO 편집 관련 변수
    public static final String EDIT_GROUP_INFO = "com.nagnek.android.meetingmanagement.EDIT_GROUP_INFO";
    public static final int POPUP_MENU_CALLED_BY_GROUP_LIST_VIEW_ITEM_LONG_CLICK = 1;
    public static final int POPUP_MENU_CALLED_BY_MEMBER_LIST_VIEW_ITEM_LONG_CLICK = 2;
    private static final String BACKUP_WHO_CALL_THIS_POPUP_MENU = "BACKUP_WHO_CALL_THIS_POPUP_MENU";
    private static int whoCallThisPopupMenu = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_long_click_popup_menu);
        ImageView editButton = (ImageView) findViewById(R.id.edit_button);
        ImageView deleteButton = (ImageView) findViewById(R.id.delete_button);
        ImageView cancelButton = (ImageView) findViewById(R.id.cancel_button);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivedIntent = getIntent();
                whoCallThisPopupMenu = receivedIntent.getIntExtra(WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY, -1);
                switch (whoCallThisPopupMenu) {
                    case POPUP_MENU_CALLED_BY_MEMBER_LIST_VIEW_ITEM_LONG_CLICK: {
                        Intent intent = new Intent(ListItemPopupMenuActivity.this, EditMemberInfoActivity.class);
                        int position = receivedIntent.getIntExtra(GroupInfoActivity.MEMBER_LIST_POSITION, 0);
                        Member member;
                        member = receivedIntent.getParcelableExtra(GroupInfoActivity.MEMBER_INFO);
                        intent.putExtra(EDIT_MEMBER_INFO, member);
                        intent.putExtra(GroupInfoActivity.MEMBER_LIST_POSITION, position);
                        startActivityForResult(intent, REQ_CODE_EDIT_MEMBER_INFO);
                        member = null;
                    }
                    break;
                    case POPUP_MENU_CALLED_BY_GROUP_LIST_VIEW_ITEM_LONG_CLICK: {
                        Intent intent = new Intent(ListItemPopupMenuActivity.this, EditGroupInfoActivity.class);
                        int position = receivedIntent.getIntExtra(MainActivity.GROUP_LIST_POSITION, 0);
                        Group groupInfo = receivedIntent.getParcelableExtra(MainActivity.GROUP_INFO);
                        intent.putExtra(EDIT_GROUP_INFO, groupInfo);
                        intent.putExtra(MainActivity.GROUP_LIST_POSITION, position);
                        startActivityForResult(intent, REQ_CODE_EDIT_GROUP_INFO);
                    }
                    break;
                }
                //TODO: finish후에 불리는 생명주기는? onDestroy? onStop? 메모리해제는 어디서?
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivedIntent = getIntent();
                whoCallThisPopupMenu = receivedIntent.getIntExtra(WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY, -1);
                switch (whoCallThisPopupMenu) {
                    case POPUP_MENU_CALLED_BY_MEMBER_LIST_VIEW_ITEM_LONG_CLICK:
                        setResult(RESULT_CODE_DELETE_MEMBER, receivedIntent);
                        break;
                    case POPUP_MENU_CALLED_BY_GROUP_LIST_VIEW_ITEM_LONG_CLICK:
                        Dlog.i("제거하라고");
                        setResult(RESULT_CODE_DELETE_GROUP_INFO, receivedIntent);
                        break;
                }

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
        if (requestCode == REQ_CODE_EDIT_MEMBER_INFO) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_CODE_EDIT_MEMBER_INFO, data);
                finish();
            }
        } else if (requestCode == REQ_CODE_EDIT_GROUP_INFO) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_CODE_EDIT_GROUP_INFO, data);
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BACKUP_WHO_CALL_THIS_POPUP_MENU, whoCallThisPopupMenu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        whoCallThisPopupMenu = savedInstanceState.getInt(BACKUP_WHO_CALL_THIS_POPUP_MENU);
    }
}
