package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditMemberActivity extends Activity {
    private static final int REQ_CODE_SELECT_IMAGE = 1;
    private static final String BACK_UP_MEMBER_KEY = "BACK_UP_MEMBER";  // 이전 액티비티의 멤버 정보 저장
    private static final String BACK_UP_TEMP_MEMBER_KEY = "BACK_UP_TEMP_MEMBER"; // 현재 수정중인 멤버 정보 저장, 취소버튼 누를시 저장안하고 사라짐
    private static final String BACK_UP_MEMBER_POSITION = "BACK_UP_POSITION"; // 최종적으로 GroupActivity에 넘겨주기 위해
    // TODO: 에디트 박스 수정할때 바꿔줘야될려나...
    Member member = null;   // 에디트 박스가 수정될때가 아니라 onSaveInstanceState나 onRestoreInstanceState 또는 ok버튼 누를시 갱신된다
    Member tempMember = null;
    ImageView imageView = null;
    EditText memberName = null;
    EditText phoneNumber = null;
    Button okButton = null;
    Button cancelButton = null;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_info);
        Dlog.i("onCreate");
        // ====================================================================================

        // 호출된 인텐트에서 데이터를 추출한다
        // ====================================================================================

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if(receivedIntent!=null) {
                //member = receivedIntent.getParcelableExtra(GroupActivity.SELECT_MEMBER_LIST_ITEM);
                member = new Member();
                member.name = receivedIntent.getStringExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_NAME);
                member.imageUri = receivedIntent.getParcelableExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_IMAGE_URI);
                member.phone_number = receivedIntent.getStringExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_PHONE);
                tempMember = new Member();
                tempMember.copy(member);
                Dlog.i("I"+tempMember.name);
                position = receivedIntent.getIntExtra(GroupActivity.MEMBER_LIST_POSITION, 0);
            }
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
                // ====================================================================================

                // 확인 버튼 눌러 액티비티가 넘어갈 때 현재 editbox의 내용대로 member객체의 데이터 내용을 바꿔서 전달한다
                // ====================================================================================
                member.imageUri = tempMember.imageUri;
                imageView.setImageBitmap(null);
                tempMember.imageUri = null;
                tempMember = null;
                member.name = memberName.getText().toString();
                member.phone_number = phoneNumber.getText().toString();

                Intent intent = new Intent();
                //intent.putExtra(GroupActivity.SELECT_MEMBER_LIST_ITEM, member);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_PHONE, member.phone_number);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_IMAGE_URI, member.imageUri);
                intent.putExtra(MemberItemPopupMenuActivity.EDIT_MEMBER_NAME, member.name);
                intent.putExtra(GroupActivity.MEMBER_LIST_POSITION, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                Dlog.i("finish with Cancel Button");
                finish();
            }
        });
        // ====================================================================================

        // 레이아웃 갱신한다.
        // ====================================================================================
        if (tempMember != null) {
            if (tempMember.imageUri != null) {
                imageView.setImageBitmap(NagneImage.getBitmap(this, tempMember.imageUri));
            }
            if (tempMember.name != null) {
                memberName.setText(tempMember.name);
            }
            if (tempMember.phone_number != null) {
                phoneNumber.setText(tempMember.phone_number);
            }
        } else {
            Dlog.i("tempMember가 null입니다 ");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                tempMember.imageUri = data.getData();
                Bitmap image_bitmap = NagneCircleImage.getCircleBitmap(this, tempMember.imageUri);
                //배치해놓은 ImageView에 set
                imageView.setImageBitmap(image_bitmap);
                image_bitmap = null;
            }
        }
    }

    // ====================================================================================

    // member 객체의 데이터들을 현재의 editText들의 데이터로 대체한 후 저장한다.
    // ====================================================================================
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i("onSaveInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        tempMember.name = memberName.getText().toString();
        tempMember.phone_number = phoneNumber.getText().toString();
        outState.putParcelable(BACK_UP_MEMBER_KEY, member);
        outState.putParcelable(BACK_UP_TEMP_MEMBER_KEY, tempMember);
        outState.putInt(BACK_UP_MEMBER_POSITION, position);
        Dlog.i("backup member");
        Dlog.i("backup tempMember");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Dlog.i("onRestoreInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 뷰들의 내용을 복원한다.
        if (savedInstanceState != null) {
            Dlog.i(savedInstanceState.getString("restore member"));
            member = savedInstanceState.getParcelable(BACK_UP_MEMBER_KEY);
            tempMember = savedInstanceState.getParcelable(BACK_UP_TEMP_MEMBER_KEY);
            position = savedInstanceState.getInt(BACK_UP_MEMBER_POSITION);
            if (tempMember.imageUri != null) {
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, tempMember.imageUri));
            }
            if (tempMember.name != null) {
                memberName.setText(tempMember.name);
            }
            if (tempMember.phone_number != null) {
                phoneNumber.setText(tempMember.phone_number);
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
