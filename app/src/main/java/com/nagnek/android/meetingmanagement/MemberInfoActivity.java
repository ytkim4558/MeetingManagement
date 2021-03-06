package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;

public class MemberInfoActivity extends Activity {

    public final static String BACKUP_INSTANCE = "BACKUP_INSTANCE";
    private final static String BACKUP_IS_EDIT_MEMBER_INFO = "BACKUP_IS_EDIT_MEMBER_INFO";

    Member member;
    int position;
    Intent resultIntent;
    boolean isEditMemberInfo = false; // 멤버 info를 edit 했었는지 확인
    private float memberImageLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        memberImageLength = R.dimen.image_view_showable_big_icon_length;
        Dlog.d("onCreate()");
        // ====================================================================================
        //
        // 호출한 인텐트에서 정보를 추출한다
        // ====================================================================================
        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            member = receivedIntent.getParcelableExtra(MemberListActivity.MEMBER_INFO);
            position = receivedIntent.getIntExtra(MemberListActivity.MEMBER_LIST_POSITION, 0);
        } else {
            member = savedInstanceState.getParcelable(EditMemberInfoActivity.BACK_UP_MEMBER_KEY);
            position = savedInstanceState.getInt(MemberListActivity.MEMBER_LIST_POSITION, 0);
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
        ImageView cancelButton = (ImageView) findViewById(R.id.cancel_button);
        ImageView editButton = (ImageView) findViewById(R.id.edit_button);
        // ====================================================================================

        // 리스너 등록
        // ====================================================================================
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMemberInfo) {
                    setResult(ListItemPopupMenuActivity.RESULT_CODE_EDIT_MEMBER_INFO, resultIntent);
                }
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberInfoActivity.this, EditMemberInfoActivity.class);
                intent.putExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO, member);
                intent.putExtra(MemberListActivity.MEMBER_LIST_POSITION, position);
                startActivityForResult(intent, ListItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER_INFO);
            }
        });

        // ====================================================================================

        // 뷰 내용 변경
        // ====================================================================================
        if (member.name != null) {
            memberName.setText(member.name);
        }
        if (member.imageUri != null) {
            memberImage.setImageBitmap(NagneCircleImage.getCircleBitmap(this, member.imageUri, memberImageLength, memberImageLength));
        } else {
            memberImage.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.user, memberImageLength, memberImageLength));
        }
        if (member.phone_number != null) {
            phoneNumber.setText(member.phone_number);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ListItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER_INFO) {
            if (resultCode == RESULT_OK) {
                Dlog.d("편집됨");
                Intent receivedIntent = data;
                isEditMemberInfo = true;
                resultIntent = data;
                member = receivedIntent.getParcelableExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO);
                position = receivedIntent.getIntExtra(MemberListActivity.MEMBER_LIST_POSITION, 0);
                // ====================================================================================
                //
                // 현재 아이템의 내용을 변경할 뷰를 찾는다.
                // ====================================================================================
                TextView memberName = (TextView) findViewById(R.id.member_name);
                TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
                ImageView memberImage = (ImageView) findViewById(R.id.member_image);
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
        outState.putParcelable(EditMemberInfoActivity.BACK_UP_MEMBER_KEY, member);
        outState.putInt(EditMemberInfoActivity.BACK_UP_MEMBER_POSITION, position);
        outState.putParcelable(BACKUP_INSTANCE, resultIntent);
        outState.putBoolean(BACKUP_IS_EDIT_MEMBER_INFO, isEditMemberInfo);
    }

    @Override
    public void onBackPressed() {
        Dlog.i("onBackPressed()");
        if (isEditMemberInfo) {
            setResult(ListItemPopupMenuActivity.RESULT_CODE_EDIT_MEMBER_INFO, resultIntent);
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