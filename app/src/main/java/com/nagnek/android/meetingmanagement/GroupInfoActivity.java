package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.externalIntent.Phone;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;

import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.sharedString.Storage;

import java.util.ArrayList;

public class GroupInfoActivity extends Activity {

    public static final String MEMBER_LIST_POSITION = "com.nagnek.android.meetingmanagement.MEMBER_LIST_POSITION";
    public static final String MEMBER_INFO = "com.nagenk.android.meetingmanagement.MEMBER_INFO";
    public static final int REQ_CODE_SELECT_MEMBER_LIST_ITEM = 25;
    public static final int REQ_CODE_SELECT_IMAGE = 100;
    public static final String MEMBER_LIST_KEY = "com.nagnek.android.meetingmanagement.MEMBER_LIST_KEY";
    public static final String BACKUP_GROUP_POSITION_KEY = "BACKUP_GROUP_POSITION_KEY";
    public static final String POPUP_MENU_CALLED_BY_MENU_LIST_ITEM_LONG_CLICK_KEY = "com.nagnek.android.meetingmanagement.POPUP_MENU_CALLED_BY_MENU_LIST_ITEM_LONG_CLICK_KEY";
    static final int PICK_CONTACT_REQUEST = 1;
    private static final String KEY_CROPPED_RECT = "cropped-rect";
    private static final String BACKUP_MEMBER_LIST_KEY = "BACKUP_MEMBER_LIST_KEY";
    private static final String BACKUP_GROUP_IMAGE_URI = "BACKUP_GROUP_IMAGE_URI";
    String imagePath = null;
    ImageView imageView;
    Uri groupImageUri;
    ArrayList<Member> memberList = null;
    MemberListAdapter memberListAdapter = null;
    ListView memberListView = null;
    int group_position;
    //private TextView memberNameView = null;
    private String number = null; // 멤버 전화번호
    private String groupName = null;
    private TextView groupNameText = null;
    private String strPhotoName = null;

    static void saveMemberInfoAndPostionsOfMemberAndGroup() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Dlog.i("onCreate()");
        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getExtras().getString(MainActivity.GROUP_NAME);
            groupImageUri = intent.getParcelableExtra(MainActivity.GROUP_IMAGE_URI);
            group_position = intent.getIntExtra(MainActivity.GROUP_LIST_POSITION, 0);
            memberList = intent.getParcelableArrayListExtra(MEMBER_LIST_KEY);
        }

        // 어댑터를 생성하고 데이터 설정
        memberListAdapter = new MemberListAdapter(this, memberList);

        // 리스트뷰에 어댑터 설정
        memberListView = (ListView) findViewById(R.id.member_list_view);
        memberListView.setAdapter(memberListAdapter);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupInfoActivity.this, MemberInfoActivity.class);
                Member member = memberList.get(position);
                intent.putExtra(MEMBER_INFO, member);
                intent.putExtra(MEMBER_LIST_POSITION, position);
                startActivityForResult(intent, REQ_CODE_SELECT_MEMBER_LIST_ITEM);
            }
        });

        memberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupInfoActivity.this, ListItemPopupMenuActivity.class);
                Member member = memberList.get(position);
                intent.putExtra(ListItemPopupMenuActivity.WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY, ListItemPopupMenuActivity.POPUP_MENU_CALLED_BY_MEMBER_LIST_VIEW_ITEM_LONG_CLICK);
                intent.putExtra(POPUP_MENU_CALLED_BY_MENU_LIST_ITEM_LONG_CLICK_KEY, ListItemPopupMenuActivity.POPUP_MENU_CALLED_BY_MEMBER_LIST_VIEW_ITEM_LONG_CLICK);
                intent.putExtra(MEMBER_INFO, member);
                intent.putExtra(MEMBER_LIST_POSITION, position);
                startActivityForResult(intent, REQ_CODE_SELECT_MEMBER_LIST_ITEM);
                return true;
            }
        });

        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        Button addMemberButton = (Button) findViewById(R.id.add_member_button);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        groupNameText = (TextView) findViewById(R.id.group_name_text_view);
        groupNameText.setText(groupName);

        imageView = (ImageView) findViewById(R.id.groupImageView);
        if (groupImageUri != null) {
            imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, groupImageUri));
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NagneImage.picImageFromGalleryStartActivityForResult(GroupInfoActivity.this, REQ_CODE_SELECT_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult에서 넘긴 requestCode를 체크한다
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 선택한 결과는 Uri 리턴되며 해당 Uri를 쿼리하여 얻어오게 된다
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, //연락처
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // 연락처 이

                // 주의 : UI의 블락킹 때문에라도(화면 버벅거림 쿼리 실행은 별도의 스레드에서 처리하는게 좋다
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // getColumnIndex로 꼭 가져와야하나 바로 가져올수없나 특히 아래 이름의 경우 getString 같은걸로
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                Member member = new Member();
                member.phone_number = cursor.getString(column);
                member.phone_number = Phone.getFormatPhoneNumberFormat(member.phone_number);

                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                member.name = cursor.getString(nameIndex);

                memberListAdapter.add(memberListAdapter.getCount(), member);

                NagneSharedPreferenceUtil.saveOjbectToSharedPreference(this, Storage.SAVE_MEMBER_INFO_FILE, member, "phone_number");
                String[] resultList = NagneSharedPreferenceUtil.loadObjectFromSharedPreference(this, Storage.SAVE_MEMBER_INFO_FILE, member, "phone_number");

                Dlog.i("result : ");
                String result = null;
                for (int i = 0; i < resultList.length; ++i) {
                    result += resultList[i];
                }
                Dlog.i(result);
            }

        } else if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                Bitmap image_bitmap = null;
                groupImageUri = data.getData();
                image_bitmap = NagneCircleImage.getCircleBitmap(this, groupImageUri);
                //배치해놓은 ImageView에 set
                imageView.setImageBitmap(image_bitmap);
                image_bitmap = null;
            }
        } else if (requestCode == REQ_CODE_SELECT_MEMBER_LIST_ITEM) {
            if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_EDIT_MEMBER_INFO) {
                Dlog.d("RESULT_CODE_EDIT_MEMBER_INFO");

                Member member = data.getParcelableExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO);
                int position = data.getIntExtra(MEMBER_LIST_POSITION, 0);
                memberListAdapter.set(position, member);
            } else if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_DELETE_MEMBER) {
                int position = data.getIntExtra(MEMBER_LIST_POSITION, 0);
                memberListAdapter.delete(position);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dlog.i("onStart()");

        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
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
            Dlog.i("RestoreImage");
            if (groupImageUri != null) {
                Bitmap bitmap = null;
                bitmap = NagneCircleImage.getCircleBitmap(this, groupImageUri);
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                bitmap = null;
            }
            group_position = savedInstanceState.getInt(BACKUP_GROUP_POSITION_KEY);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dlog.i("onResume()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Dlog.i("onRestart()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Dlog.i("onPause()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Dlog.i("onStop()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.i("onDestroy()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        groupImageUri = null;
        imageView = null;
        number = null;
        groupName = null;
    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i("onSaveInstanceState()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 멤버 리스트를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if (memberList != null) {
            outState.putParcelableArrayList(BACKUP_MEMBER_LIST_KEY, memberList);
        }
        // 이미지를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        if (groupImageUri != null) {
            outState.putParcelable(BACKUP_GROUP_IMAGE_URI, groupImageUri);
            Dlog.i("저장");
        }

        outState.putInt(BACKUP_GROUP_POSITION_KEY, group_position);

        super.onSaveInstanceState(outState);
    }

    // 연락처 선택
    private void pickContact() {
        // TODO: Uri 공부할것
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        // setType은 뭐야.

        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        // PICK_CONTACT_REQUEST, requestCode가 결과를 얻는데 사용된다
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    // 전화걸기
    private void call(String number) {
        if (number != null) {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + number)));
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: 이후에 이전 액티비티에 넘기지 말고 저장하고 불러오는 식으로 할것
        Intent intent = new Intent();
        intent.putExtra(MEMBER_LIST_KEY, memberList);
        intent.putExtra(MainActivity.GROUP_LIST_POSITION, group_position);
        intent.putExtra(MainActivity.GROUP_IMAGE_URI, groupImageUri);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
