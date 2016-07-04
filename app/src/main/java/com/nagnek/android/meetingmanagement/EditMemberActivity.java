package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditMemberActivity extends Activity {
    private static final int REQ_CODE_SELECT_IMAGE = 1;
    public static final String MEMBER_KEY = "MEMBER_KEY";
    private static final String BACK_UP_MEMBER_KEY = "BACK_UP_MEMBER";
    Member member = null;
    ImageView imageView = null;
    EditText memberName = null;
    EditText phoneNumber = null;
    Button okButton = null;
    Button cancelButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        if(Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // ====================================================================================

        // 호출된 인텐트에서 데이터를 추출한다
        // ====================================================================================
        Intent intent = getIntent();
        if (intent != null && savedInstanceState == null) {
            member = intent.getParcelableExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_KEY);
        }
        // ====================================================================================

        // 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        imageView = (ImageView) findViewById(R.id.member_image);
        memberName = (EditText) findViewById(R.id.member_name);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        okButton = (Button) findViewById(R.id.ok_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        // ====================================================================================

        // 뷰에 리스너 등록
        // ====================================================================================
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                try {
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MEMBER_KEY, member);
                intent.putExtra(GroupActivity.MEMBER_LIST_POSITION_KEY, getIntent().getIntExtra(GroupActivity.MEMBER_LIST_POSITION_KEY, 0));
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
        // ====================================================================================

        // 레이아웃 갱신한다.
        // ====================================================================================

    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i( "onSaveInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if (member != null) {
            outState.putParcelable(BACK_UP_MEMBER_KEY, member);
            Dlog.i("backup member");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Dlog.i( "onRestoreInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 뷰들의 내용을 복원한다.
        if (savedInstanceState != null) {
            Dlog.i( savedInstanceState.getString("restore member"));
            member = savedInstanceState.getParcelable(BACK_UP_MEMBER_KEY);
            if(member.imageUri != null) {
                imageView.setImageBitmap(NagneRoundedImage.getRoundedBitmap(this, member.imageUri));
            }
            if(member.name != null) {
                memberName.setText(member.name);
            }
            if(member.phone_number != null) {
                phoneNumber.setText(member.phone_number);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        imageView.setImageBitmap(null);
        super.onDestroy();
    }
}
