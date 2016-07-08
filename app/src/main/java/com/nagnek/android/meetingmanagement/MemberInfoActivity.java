package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneImage.NagneCircleImage;

public class MemberInfoActivity extends Activity {

    public final static String BACKUP_INSTANCE = "BACKUP_INSTANCE";
    private final static String BACKUP_IS_EDIT_MEMBER_INFO = "BACKUP_IS_EDIT_MEMBER_INFO";

    Member member;
    int position;
    Intent resultIntent;
    boolean isEditMemberInfo = false; // 멤버 info를 edit 했었는지 확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        Dlog.d("onCreate()");
        // ====================================================================================
        //
        // 호출한 인텐트에서 정보를 추출한다
        // ====================================================================================
        member = new Member();
        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            member.name = receivedIntent.getStringExtra(GroupActivity.MEMBER_NAME);
            member.imageUri = receivedIntent.getParcelableExtra(GroupActivity.MEMBER_IMAGE_URI);
            member.phone_number = receivedIntent.getStringExtra(GroupActivity.MEMBER_PHONE);
            position = receivedIntent.getIntExtra(GroupActivity.MEMBER_LIST_POSITION, 0);
        } else {
            member = savedInstanceState.getParcelable(EditMemberActivity.BACK_UP_MEMBER_KEY);
            position = savedInstanceState.getInt(GroupActivity.MEMBER_LIST_POSITION, 0);
            resultIntent = savedInstanceState.getParcelable(BACKUP_INSTANCE);
            isEditMemberInfo = savedInstanceState.getBoolean(BACKUP_IS_EDIT_MEMBER_INFO);
        }
        // ====================================================================================
        //
        // 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        TextView memberName = (TextView) findViewById(R.id.member_name);
        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        ImageView memberImage = (ImageView) findViewById(R.id.member_image);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        Button editButton = (Button) findViewById(R.id.edit_button);
        // ====================================================================================

        // 리스너 등록
        // ====================================================================================
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMemberInfo) {
                    setResult(MemberItemPopupMenuActivity.RESULT_CODE_EDIT_MEMBER, resultIntent);
                }
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberInfoActivity.this, EditMemberActivity.class);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_NAME, member.name);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_IMAGE_URI, member.imageUri);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_PHONE, member.phone_number);
                intent.putExtra(GroupActivity.MEMBER_LIST_POSITION, position);
                startActivityForResult(intent, MemberItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER);
            }
        });

        // ====================================================================================

        // 뷰 내용 변경
        // ====================================================================================
        if (member.name != null) {
            memberName.setText(member.name);
        }
        if (member.imageUri != null) {
            memberImage.setImageBitmap(NagneCircleImage.getCircleBitmap(this, member.imageUri));
        }
        if (member.phone_number != null) {
            phoneNumber.setText(member.phone_number);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MemberItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER) {
            if (resultCode == RESULT_OK) {
                Dlog.d("편집됨");
                Intent receivedIntent = data;
                isEditMemberInfo = true;
                resultIntent = data;
                member.name = receivedIntent.getStringExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_NAME);
                member.imageUri = receivedIntent.getParcelableExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_IMAGE_URI);
                member.phone_number = receivedIntent.getStringExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_PHONE);
                position = receivedIntent.getIntExtra(GroupActivity.MEMBER_LIST_POSITION, 0);

                // ====================================================================================
                //
                // 현재 아이템의 내용을 변경할 뷰를 찾는다.
                // ====================================================================================
                TextView memberName = (TextView) findViewById(R.id.member_name);
                TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
                ImageView memberImage = (ImageView) findViewById(R.id.member_image);
                Button cancelButton = (Button) findViewById(R.id.cancel_button);
                Button editButton = (Button) findViewById(R.id.edit_button);
                // ====================================================================================
                //
                // 뷰 내용 변경
                // ====================================================================================
                if (member.name != null) {
                    memberName.setText(member.name);
                }
                if (member.imageUri != null) {
                    memberImage.setImageBitmap(NagneCircleImage.getCircleBitmap(this, member.imageUri));
                }
                if (member.phone_number != null) {
                    phoneNumber.setText(member.phone_number);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EditMemberActivity.BACK_UP_MEMBER_KEY, member);
        outState.putInt(EditMemberActivity.BACK_UP_MEMBER_POSITION, position);
        outState.putParcelable(BACKUP_INSTANCE, resultIntent);
        outState.putBoolean(BACKUP_IS_EDIT_MEMBER_INFO, isEditMemberInfo);
    }

    @Override
    public void onBackPressed() {
        Dlog.i("onBackPressed()");
        if(isEditMemberInfo) {
            setResult(MemberItemPopupMenuActivity.RESULT_CODE_EDIT_MEMBER, resultIntent);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Dlog.i("onDestroy()");
        super.onDestroy();
    }
}
