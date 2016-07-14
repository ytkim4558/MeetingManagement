package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;

public class EditGroupInfoActivity extends Activity {

    public static final String BACK_UP_GROUP_KEY = "BACK_UP_GROUP";  // 이전 액티비티의 그룹 정보 저장
    public static final String BACK_UP_GROUP_POSITION = "BACK_UP_POSITION"; // 최종적으로 GroupActivity에 넘겨주기 위해
    private static final int REQ_CODE_SELECT_IMAGE = 1;
    private static final String BACK_UP_TEMP_GROUP_KEY = "BACK_UP_TEMP_GROUP"; // 현재 수정중인 그룹 정보 저장, 취소버튼 누를시 저장안하고 사라짐
    // TODO: 에디트 박스 수정할때 바꿔줘야될려나...

    // 에디트 박스가 수정될때가 아니라 onSaveInstanceState나 onRestoreInstanceState 또는 ok버튼 누를시 갱신된다
    Group group;
    Group tempGroup;
    ImageView imageView = null;
    EditText groupNameEditText = null;
    ImageView okButton = null;
    ImageView cancelButton = null;
    private float userImageLength;
    private float pushIconLength;
    int position;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_info);
        Dlog.i("onCreate");
        // ====================================================================================

        // 호출된 인텐트에서 데이터를 추출한다
        // ====================================================================================

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if (receivedIntent != null) {
                group = receivedIntent.getParcelableExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO);
                tempGroup = new Group();
                tempGroup.copy(group);
                position = receivedIntent.getIntExtra(MainActivity.GROUP_LIST_POSITION, 0);
            }
        }
        userImageLength = R.dimen.image_view_showable_icon_length;
        pushIconLength = R.dimen.image_view_push_icon_length;
        // ====================================================================================

        // 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        imageView = (ImageView) findViewById(R.id.group_image);
        groupNameEditText = (EditText) findViewById(R.id.group_name);
        okButton = (ImageView) findViewById(R.id.ok_button);
        cancelButton = (ImageView) findViewById(R.id.cancel_button);
        // ====================================================================================

        // 뷰에 리스너 등록
        // ====================================================================================
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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

                // 확인 버튼 눌러 액티비티가 넘어갈 때 현재 editbox의 내용대로 group객체의 데이터 내용을 바꿔서 전달한다
                // ====================================================================================
                group.imageUri = tempGroup.imageUri;
                imageView.setImageBitmap(null);
                tempGroup.imageUri = null;
                tempGroup = null;
                group.name = groupNameEditText.getText().toString();

                Intent intent = new Intent();
                intent.putExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO, group);
                intent.putExtra(MainActivity.GROUP_LIST_POSITION, position);
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
        // ====================================================================================

        // 레이아웃 갱신한다.
        // ====================================================================================
        if (tempGroup != null) {
            if (tempGroup.imageUri != null) {
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, tempGroup.imageUri, userImageLength, userImageLength));
            }
            if (tempGroup.name != null) {
                groupNameEditText.setText(tempGroup.name);
            }
        }
        imageView.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.add_group, R.dimen.image_view_showable_icon_length, R.dimen.image_view_showable_icon_length));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                tempGroup.imageUri = data.getData();
                Bitmap image_bitmap = NagneCircleImage.getCircleBitmap(this, tempGroup.imageUri, userImageLength, userImageLength);
                //배치해놓은 ImageView에 set
                imageView.setImageBitmap(image_bitmap);
                image_bitmap = null;
            }
        }
    }

    // ====================================================================================

    // group 객체의 데이터들을 현재의 editText들의 데이터로 대체한 후 저장한다.
    // ====================================================================================
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i("onSaveInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        tempGroup.name = groupNameEditText.getText().toString();
        outState.putParcelable(BACK_UP_GROUP_KEY, group);
        outState.putParcelable(BACK_UP_TEMP_GROUP_KEY, tempGroup);
        outState.putInt(BACK_UP_GROUP_POSITION, position);
        Dlog.i("backup group");
        Dlog.i("backup tempGroupInfo");
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
            Dlog.i(savedInstanceState.getString("restore group"));
            group = savedInstanceState.getParcelable(BACK_UP_GROUP_KEY);
            tempGroup = savedInstanceState.getParcelable(BACK_UP_TEMP_GROUP_KEY);
            position = savedInstanceState.getInt(BACK_UP_GROUP_POSITION);
            if (tempGroup.imageUri != null) {
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, tempGroup.imageUri, userImageLength, userImageLength));
            }
            if (tempGroup.name != null) {
                groupNameEditText.setText(tempGroup.name);
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
