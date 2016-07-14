package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;

public class EditMemberInfoActivity extends Activity {
    public static final String BACK_UP_MEMBER_KEY = "BACK_UP_MEMBER";  // 이전 액티비티의 멤버 정보 저장
    public static final String BACK_UP_MEMBER_POSITION = "BACK_UP_POSITION"; // 최종적으로 GroupActivity에 넘겨주기 위해
    private static final int REQ_CODE_SELECT_IMAGE = 1;
    private static final String BACK_UP_TEMP_MEMBER_KEY = "BACK_UP_TEMP_MEMBER"; // 현재 수정중인 멤버 정보 저장, 취소버튼 누를시 저장안하고 사라짐
    // TODO: 에디트 박스 수정할때 바꿔줘야될려나...
    Member member = null;   // 에디트 박스가 수정될때가 아니라 onSaveInstanceState나 onRestoreInstanceState 또는 ok버튼 누를시 갱신된다
    Member tempMember = null;
    ImageView imageView = null;
    EditText memberName = null;
    EditText phoneNumber = null;
    ImageView okButton = null;
    ImageView cancelButton = null;
    private int addUserImageButtonId;
    private int backImageButtonId;
    private int saveImageButtonId;
    private float userImageLength;
    private float pushIconLength;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_info);
        addUserImageButtonId = R.drawable.add_user;
        backImageButtonId = R.drawable.back;
        saveImageButtonId = R.drawable.save;
        userImageLength = R.dimen.image_view_showable_icon_length;
        pushIconLength = R.dimen.image_view_push_icon_length;

        Dlog.i("onCreate");
        // ====================================================================================

        // 호출된 인텐트에서 데이터를 추출한다
        // ====================================================================================

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if (receivedIntent != null) {
                member = receivedIntent.getParcelableExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO);
                tempMember = new Member();
                tempMember.copy(member);
                position = receivedIntent.getIntExtra(GroupInfoActivity.MEMBER_LIST_POSITION, 0);
            }
        }
        // ====================================================================================

        // 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        imageView = (ImageView) findViewById(R.id.member_image);
        memberName = (EditText) findViewById(R.id.member_name);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        okButton = (ImageView) findViewById(R.id.ok_button);
        cancelButton = (ImageView) findViewById(R.id.cancel_button);
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
                //intent.putExtra(GroupInfoActivity.SELECT_MEMBER_LIST_ITEM, member);
                intent.putExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO, member);
                intent.putExtra(GroupInfoActivity.MEMBER_LIST_POSITION, position);
                setResult(RESULT_OK, intent);
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
        //에디트 박스 내용이 전화번호로 표시되게끔 설정
        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // ====================================================================================

        // 레이아웃 갱신한다.
        // ====================================================================================
        if (tempMember != null) {
            if (tempMember.imageUri != null) {
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, member.imageUri, userImageLength, userImageLength));
            }
            if (tempMember.name != null) {
                memberName.setText(tempMember.name);
            }
            if (tempMember.phone_number != null) {
                phoneNumber.setText(tempMember.phone_number);
            }
        }
        imageView.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), addUserImageButtonId, userImageLength, userImageLength));
        cancelButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), backImageButtonId, pushIconLength, pushIconLength));
        okButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), saveImageButtonId, pushIconLength, pushIconLength));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                tempMember.imageUri = data.getData();
                Bitmap image_bitmap = NagneCircleImage.getCircleBitmap(this, tempMember.imageUri, userImageLength, userImageLength);
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
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, tempMember.imageUri, userImageLength, userImageLength));
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
