package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;

/**
 * Created by yongtakpc on 2016. 6. 28..
 */
public class NewGroupPopupActivity extends PopupActivity {
    public static final String NEW_GROUP_NAME = "com.nagnek.meetingmanagement.NEW_GROUP_NAME";
    public static final String NEW_GROUP_IMAGE = "com.nagnek.meetingmanagement.NEW_GROUP_IMAGE";
    private static final String BACKUP_GROUP_IMAGE_URI = "BACKUP_GROUP_IMAGE_URI";
    private Uri groupImageUri;  // 생명주기에서 살아남게 하기 위함
    private ImageView groupImageView;    // 리소스 아이디 계속 가져오는 것을 피하기 위해 전역변수로 둠
    private float groupImageLength;
    private float pushIconLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        groupImageLength = R.dimen.image_view_showable_icon_length;
        pushIconLength = R.dimen.image_view_push_icon_length;
        setContentView(R.layout.activity_new_group);
        ImageView okButton = (ImageView) findViewById(R.id.ok_button);
        ImageView cancelButton = (ImageView) findViewById(R.id.cancel_button);
        groupImageView = (ImageView) findViewById(R.id.group_image);
        groupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NagneImage.picImageFromGalleryStartActivityForResult(NewGroupPopupActivity.this, GroupInfoActivity.REQ_CODE_SELECT_IMAGE);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                EditText newGroupName = (EditText) findViewById(R.id.group_name);
                intent.putExtra(NEW_GROUP_NAME, newGroupName.getText().toString());
                intent.putExtra(NEW_GROUP_IMAGE, groupImageUri);
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
        groupImageView.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.add_group, groupImageLength, groupImageLength ));
        cancelButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.back, pushIconLength, pushIconLength));
        okButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.save, pushIconLength, pushIconLength));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GroupInfoActivity.REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                groupImageUri = data.getData();
                groupImageView.setImageBitmap(NagneCircleImage.getCircleBitmap(NewGroupPopupActivity.this, groupImageUri));
            }
        }
    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i("onSaveInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 그룹 객체를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if (groupImageUri != null) {
            outState.putParcelable(BACKUP_GROUP_IMAGE_URI, groupImageUri);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Dlog.i("onRestoreInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 사용자 이름 및 전화번호를 복원한다.
        if (savedInstanceState != null) {
            groupImageUri = savedInstanceState.getParcelable(BACKUP_GROUP_IMAGE_URI);
            if (groupImageUri != null) {
                groupImageView.setImageBitmap(NagneCircleImage.getCircleBitmap(NewGroupPopupActivity.this, groupImageUri, groupImageLength, groupImageLength));
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
