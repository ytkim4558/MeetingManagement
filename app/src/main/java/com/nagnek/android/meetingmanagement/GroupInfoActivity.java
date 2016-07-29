package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.externalIntent.Phone;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;
import com.nagnek.android.sharedString.Storage;
import com.nagnek.nagneJavaUtil.NagneString;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfoActivity extends AppCompatActivity {
    public static final String MEMBER_LIST_POSITION = "com.nagnek.android.meetingmanagement.MEMBER_LIST_POSITION";
    public static final String MEMBER_INFO = "com.nagenk.android.meetingmanagement.MEMBER_INFO";
    public static final int REQ_CODE_SELECT_MEMBER_LIST_ITEM = 25;
    public static final int REQ_CODE_SELECT_IMAGE = 100;
    public static final String POPUP_MENU_CALLED_BY_MENU_LIST_ITEM_LONG_CLICK_KEY = "com.nagnek.android.meetingmanagement.POPUP_MENU_CALLED_BY_MENU_LIST_ITEM_LONG_CLICK_KEY";
    static final int PICK_CONTACT_REQUEST = 1;
    private static final String KEY_CROPPED_RECT = "cropped-rect";
    private static final String BACKUP_MEMBER_LIST_KEY = "BACKUP_MEMBER_LIST_KEY";
    private static final String BACKUP_GROUP_IMAGE_URI = "BACKUP_GROUP_IMAGE_URI";
    private static final String BACKUP_GROUP_POSITION_KEY = "BACKUP_GROUP_POSITION_KEY";
    String imagePath = null;
    ImageView imageView;
    Uri groupImageUri;
    ArrayList<Member> memberList = null;
    MemberListAdapter memberListAdapter = null;
    ListView memberListView = null;
    //private TextView memberNameView = null;
    private String number = null; // 멤버 전화번호
    private String groupName = null;
    private TextView groupNameText = null;
    public static int group_position = 0;
    public static int dialog_list_position = 0; // 어떤 리스트 인덱스가 팝업 띄웠는지. (생명주기에서 살아남게 하기 위해)
    static final int MOVE_DURATION = 300;
    boolean mAdballonAnimate = false;

    private int userImageLength;
    static HashMap<String, String> phoneNumberKeyToMatchGroupPositionAndMemberPosition; // 키 : 폰넘버, 값 : 그룹위치 | 멤버 위치 arraylist (값) . 멤버위치 arraylist는 한값에 멤버 위치가 여러개 있을수 있기 때문
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar memberToolBar = (Toolbar) findViewById(R.id.member_toolbar);
        setSupportActionBar(memberToolBar);
        Dlog.i("onCreate()");

        userImageLength = R.dimen.image_view_showable_big_icon_length;
        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getExtras().getString(GroupListActivity.GROUP_NAME);
            groupImageUri = intent.getParcelableExtra(GroupListActivity.GROUP_IMAGE_URI);
            group_position = intent.getIntExtra(GroupListActivity.GROUP_LIST_POSITION, 0);
        }
        memberList = new ArrayList<Member>();
        int memberNumber = 0;

        String memberNumberString = NagneSharedPreferenceUtil.getValue(this, Storage.SAVE_MEMBER_INFO_FILE, group_position + "|" + Storage.MEMBER_NUMBER); // 멤버리스트 수 = 그룹위치숫자그룹숫자
        if (memberNumberString == null) {
            memberNumber = 0;
        } else {
            memberNumber = Integer.parseInt(memberNumberString);
        }
        Dlog.i("멤버 수 : " + memberNumber);
        if (memberNumber != 0) {
            for (int i = 0; i < memberNumber; ++i) {
                String[] resultMemberInfo = NagneSharedPreferenceUtil.getValueList(this, Storage.SAVE_MEMBER_INFO_FILE, group_position + "|" + i);
                if (resultMemberInfo != null) {
                    if (resultMemberInfo.length == 3) {
                        Member member = new Member();
                        if (resultMemberInfo[0].equals("null")) {
                            member.imageUri = null;
                        } else {
                            member.imageUri = Uri.parse(resultMemberInfo[0]);
                        }
                        if (resultMemberInfo[2].equals("null")) {
                            resultMemberInfo[2] = null;
                        } else {
                            member.phone_number = resultMemberInfo[2];
                        }
                        if (resultMemberInfo[1].equals("null")) {
                            resultMemberInfo[1] = null;
                        } else {
                            member.name = resultMemberInfo[1];
                        }
                        memberList.add(member);
                    }
                }
            }
        }

        // 어댑터를 생성하고 데이터 설정
        memberListAdapter = new MemberListAdapter(this, memberList, mDeleteButtonClickListener);

        // 리스트뷰에 어댑터 설정
        memberListView = (ListView) findViewById(R.id.member_list_view);
        memberListView.setAdapter(memberListAdapter);

        // 해시맵 생성
        phoneNumberKeyToMatchGroupPositionAndMemberPosition = new HashMap<String, String>();
        for (int i = 0; i < memberList.size(); ++i) {
            String key = memberList.get(i).phone_number;
            String value = GroupInfoActivity.group_position + "|" + i;
            if (phoneNumberKeyToMatchGroupPositionAndMemberPosition.containsKey(key)) {
                String gotValue = phoneNumberKeyToMatchGroupPositionAndMemberPosition.get(key);
                String[] valueList = NagneString.convertStringToArray(gotValue);
                phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(memberList.get(i).phone_number, gotValue + "," + GroupInfoActivity.group_position + "|" + i);
            } else {
                phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(memberList.get(i).phone_number, GroupInfoActivity.group_position + "|" + i);
            }
        }

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
        ImageView addMemberButton = (ImageView) findViewById(R.id.add_member_button);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        groupNameText = (TextView) findViewById(R.id.group_name_text_view);
        groupNameText.setText(groupName);
        groupNameText.setTransitionName(GroupListActivity.SHARE_TEXT_VIEW_NAME);

        imageView = (ImageView) findViewById(R.id.groupImageView);
        imageView.setTransitionName(GroupListActivity.SHARE_IMAGE_VIEW_NAME);
        if (groupImageUri != null) {
            imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, groupImageUri, userImageLength, userImageLength));
        } else {
            imageView.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.group, userImageLength, userImageLength ));
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
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // 연락처 이름

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
                cursor.close();
                memberListAdapter.add(memberListAdapter.getCount(), member);
            }
        } else if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                groupImageUri = data.getData();
                //배치해놓은 ImageView에 set
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(this, groupImageUri, userImageLength, userImageLength));
                Group group = new Group(groupName, groupImageUri);
                NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey(this, Storage.SAVE_MEMBER_INFO_FILE, group, group_position);
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
        } else if (requestCode == ListItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER_INFO) {
            if (resultCode == RESULT_OK) {
                Member member = data.getParcelableExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO);
                int position = data.getIntExtra(MEMBER_LIST_POSITION, 0);
                memberListAdapter.set(position, member);
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
                bitmap = NagneCircleImage.getCircleBitmap(this, groupImageUri, userImageLength, userImageLength);
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
        Intent intent = new Intent();
        intent.putExtra(GroupListActivity.GROUP_LIST_POSITION, group_position);
        intent.putExtra(GroupListActivity.GROUP_IMAGE_URI, groupImageUri);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }


    private Button.OnClickListener mDeleteButtonClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(final View v) {
            // 리스트뷰에서 아이템이 삭제되면 풍선 애니메이션이 동작한다.
            // 삭제되는 뷰는 점점 작아진다.
            if(mAdballonAnimate != true) {
                mAdballonAnimate = true;
                memberListView.setEnabled(false);
                // 풍선이 터지기 전까지 축소된다. (원래 크기의 0.7배)
                int firstVisiblePosition = memberListView.getFirstVisiblePosition();
                int position = memberListView.getPositionForView((LinearLayout) v.getParent());
                final View view = (View) memberListView.getChildAt(position - firstVisiblePosition);
                view.animate().setDuration(MOVE_DURATION).scaleX(0.7f).scaleY(0.7f).withEndAction(new Runnable() {
                    public void run() {
                        // 터진다. (뻥~) 사이즈 0
                        view.animate().scaleX(0.0f).scaleY(0.0f).rotation(360).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // 터진 애니메이션 끝난 후 삭제 후 삭제 아이템과 관련된 아이템들의 애니메이션이 동작한다.
                                // Delete the item from the adapter
                                animateRemoval(memberListView, view);
                            }
                        });
                    }
                });
            }
        }
    };

    /**
     * 리스트뷰 삭제 애니메이션 진행 후 해당 아이템을 리스트에서 삭제하고
     * 해당 삭제된 아이템의 주변 아이템들을 움직이는 애니메이션을 실행한다.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();

        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = memberListAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = listview.getPositionForView(viewToRemove);
        memberListAdapter.delete(position);
        View replaceView = listview.getChildAt(position - listview.getFirstVisiblePosition());
        MemberListAdapter.ViewHolder vh = (MemberListAdapter.ViewHolder)replaceView.getTag();
        vh.needInflate = true;
        memberListAdapter.notifyDataSetChanged();
        final int deletePosition = position;
        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                int lastVisiblePosition = listview.getLastVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {    // getChildCount는 현재 화면상에 보이는 아이템의 개수를 보여준다.
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = memberListAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();

                    // startTop 이 null이 아니라는것은 화면상에 이미 보였던 아이템들이라는 것이다.
                    if(startTop != null) {
                        if(position < deletePosition) {   // 삭제한 아이템의 위에 있던 아이템들인 경우

                            // 현재 보이는 아이템
                            if(memberList.size() - 1 == lastVisiblePosition && firstVisiblePosition != 0) {
                                child.setTranslationY(-1 * child.getHeight());
                            }
                            moveUpDownBallonAnimation(child, i);
                        } else {    // 삭제한 아이템의 아래에 있던 아이템이나 삭제한 아이템의 위치에 생긴 아이템
                            // 화면창이 리스트뷰의 맨 아래에 있는 경우 아래로 이동하지 않는다.
                            // 단, 화면창에 리스트뷰의 첫번째 아이템이 보이는 경우(즉 리스트뷰가 충분히 크지 않아 스크롤바가 생기지 않은 경우나
                            // 스크롤바가 생겨도 리스트뷰의 마지막아이템과 첫번째 아이템이 한화면에 다 보이는 경우 (즉, 마지막 아이템이 일부만 보이는경우))
                            // 는 예외로 한다.
                            if(memberList.size() - 1 != lastVisiblePosition || (firstVisiblePosition == 0 && memberList.size() - 1 == lastVisiblePosition)) {
                                child.setTranslationY(child.getHeight());
                            }
                            moveDownUpBallonAnimation(child, i);
                        }
                    } else {
                        // 리스트뷰 아래쪽에 있어서 화면에 보여지지 않았던 아이템
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        if(position < deletePosition) {   // 삭제한 아이템의 위에 있던 아이템들인 경우
                            if(firstVisiblePosition != 0) {
                                child.setTranslationY(-1 * child.getHeight());
                            }
                            moveUpDownBallonAnimation(child, i);
                        } else {    // 삭제한 아이템의 아래에 있던 아이템이나 삭제한 아이템의 위치에 생긴 아이템

                            // 화면창이 리스트뷰의 맨 아래에 있는 경우 아래로 이동하지 않는다.
                            // 단, 화면창에 리스트뷰의 첫번째 아이템이 보이는 경우(즉 리스트뷰가 충분히 크지 않아 스크롤바가 생기지 않은 경우나
                            // 스크롤바가 생겨도 리스트뷰의 마지막아이템과 첫번째 아이템이 한화면에 다 보이는 경우 (즉, 마지막 아이템이 일부만 보이는경우))
                            // 는 예외로 한다.
                            if(memberList.size() - 1 != lastVisiblePosition || (firstVisiblePosition == 0 && memberList.size() - 1 == lastVisiblePosition)) {
                                child.setTranslationY(child.getHeight());
                            }
                            moveDownUpBallonAnimation(child, i);
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    // 리스트뷰에서 아이템이 삭제되면 풍선 애니메이션이 동작한다.
    // 아이템이 삭제되면 삭제된 뷰의 위에 있는 아이템들은 절반씩 찌끄러지면서 위로 밀린다. (뷰의 중심은 뷰의 절반만큼 이동되므로 translationY 가 뷰의 절반크기만큼 이동. scale은 절반만큼)
    // 그후 원래 상태로 복귀된다. (복귀될때 역시 scale의 절반만큼 중심이 이동되므로 translationY는 아래로)
    private void moveUpDownBallonAnimation(final View child, final int position ) {
        final float quadViewSize = child.getHeight() / 4;
        final float delta = -1 * quadViewSize * (2 * (position + 1) - 1);



        // 아이템이 삭제되면 삭제된 뷰의 위에 있는 아이템들은 절반씩 찌끄러지면서 위로 밀린다. (뷰의 중심은 뷰의 절반만큼 이동되므로 translationY 가 뷰의 절반크기만큼 이동. scale은 절반만큼)
        child.animate().setDuration(MOVE_DURATION).translationY(delta).scaleY(0.5f).withEndAction(new Runnable() {
            @Override
            public void run() {
                // 그 후, 원래 상태로 복귀된다. (복귀될때 역시 scale의 절반만큼 중심이 이동되므로 translationY는 아래로)
                child.animate().setDuration(MOVE_DURATION).translationY(0).scaleY(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        memberListView.setEnabled(true);
//                        StudentAdapter.ViewHolder vh = (StudentAdapter.ViewHolder)child.getTag();
//                        vh.needInflate = true;
                        mAdballonAnimate = false;
                        // mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    // 리스트뷰에서 아이템이 삭제되면 풍선 애니메이션이 동작한다.
    // 아이템이 삭제되면 삭제된 뷰의 아래에 있는 아이템들은 아래로 밀린다.
    // 그후 원래 위치로 복귀된다.
    private void moveDownUpBallonAnimation(final View child, final int position ) {
        // 풍선이
        final float quadViewSize = child.getHeight() / 4;
        final float halfViewSize = child.getHeight() / 2;
        int lastVisiblePosition = memberListView.getLastVisiblePosition();
        int firstVisiblePosition = memberListView.getFirstVisiblePosition();
        final float delta = halfViewSize + quadViewSize * (2 * (lastVisiblePosition - firstVisiblePosition - position + 1) - 1);

        // 아이템이 삭제되면 삭제된 뷰의 위에 있는 아이템들은 절반씩 찌끄러지면서 위로 밀린다. (뷰의 중심은 뷰의 절반만큼 이동되므로 translationY 가 뷰의 절반크기만큼 이동. scale은 절반만큼)
        child.animate().setDuration(MOVE_DURATION).translationY(delta).scaleY(0.5f).withEndAction(new Runnable() {
            @Override
            public void run() {
                // 그 후, 원래 상태로 복귀된다. (복귀될때 역시 scale의 절반만큼 중심이 이동되므로 translationY는 아래로)
                child.animate().setDuration(MOVE_DURATION).translationY(0).scaleY(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        memberListView.setEnabled(true);
//                        StudentAdapter.ViewHolder vh = (StudentAdapter.ViewHolder)child.getTag();
//                        vh.needInflate = true;
                        mAdballonAnimate = false;
                        //mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}
