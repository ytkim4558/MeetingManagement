package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;
import com.nagnek.android.sharedString.Storage;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public static final String GROUP_LIST_POSITION = "com.nagnek.android.meetingmanagement.GROUP_LIST_POSITION";
    public static final String GROUP_NAME = "com.nagnek.android.meetingmanagement.GROUP_NAME";
    public static final String GROUP_IMAGE_URI = "com.nagnek.android.meetingmanagement.GROUP_IMAGE_URI";
    public static final String GROUP_INFO = "com.nagnek.android.meetingmanagement.GROUP_INFO";
    public static final int REQ_CODE_SELECT_GROUP_LIST_ITEM = 17;
    public static final String POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY = "com.nagnek.android.meetingmanagement.POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY";
    public static float showable_icon_length;
    public static float push_icon_length;

    static final int NEW_GROUP_REQUEST = 1;
    static final int NEW_GROUP_GENERATE = 2;
    static final int NEW_GROUP_FALSE = 3;
    private final String GROUP_LIST_KEY = "GROUP_LIST_KEY";
    String groupName = null;
    ArrayList<Group> groupList = null;
    private int groupNumber;

    ListView groupListView = null;
    private GroupListAdapter groupListAdatper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dlog.i("onCreate()");
        // ====================================================================================
        //
        // 1. 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        ImageView groupImageView = (ImageView) findViewById(R.id.group_image);
        ImageView addGroupButton = (ImageView) findViewById(R.id.add_group_button);
        // ====================================================================================
        //
        // 2. 데이터 설정한다.
        // ====================================================================================
        showable_icon_length = getResources().getDimension(R.dimen.image_view_showable_icon_length);
        push_icon_length = getResources().getDimension(R.dimen.image_view_push_icon_length);
        Bitmap groupImageBitmap = NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.group, showable_icon_length, showable_icon_length);
        Bitmap addGroupImageButtonImage = NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.add_group, push_icon_length, push_icon_length);
        String groupNumberString = NagneSharedPreferenceUtil.getValue(this, Storage.SAVE_MEMBER_INFO_FILE, Storage.GROUP_NUMBER);
        Dlog.i(groupNumberString);
        groupNumber = 0;
        if (groupNumberString == null) {
            groupNumber = 0;
        } else {
            groupNumber = Integer.parseInt(groupNumberString);
        }

        if (groupList == null && savedInstanceState == null && groupNumber == 0) {
            groupList = new ArrayList<Group>();
        } else if (savedInstanceState == null && groupNumber != 0) {
            groupList = new ArrayList<Group>();
            for (int i = 0; i < groupNumber; ++i) {
                String[] resultGroupInfo = NagneSharedPreferenceUtil.getValueList(this, Storage.SAVE_MEMBER_INFO_FILE, i);
                Group group;
                if (resultGroupInfo != null) {
                    if (resultGroupInfo.length == 2) {
                        Uri imageUri = null;
                        if (resultGroupInfo[0].equals("null")) {
                            imageUri = null;
                        } else {
                            imageUri = Uri.parse(resultGroupInfo[0]);
                        }
                        if (resultGroupInfo[1].equals("null")) {
                            resultGroupInfo[1] = null;
                        }
                        group = new Group(resultGroupInfo[1], imageUri);
                        groupList.add(group);
                    }
                }
            }
        } else {
            groupList = savedInstanceState.getParcelableArrayList(GROUP_LIST_KEY);
        }

        // 어댑터를 생성하고 데이터 설정
        groupListAdatper = new GroupListAdapter(this, groupList);

        // 리스트뷰에 어댑터 설정
        groupListView = (ListView) findViewById(R.id.group_list_view);
        groupListView.setAdapter(groupListAdatper);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, GroupInfoActivity.class);
                Group group = groupList.get(position);
                intent.putExtra(GROUP_NAME, group.name);
                intent.putExtra(GROUP_IMAGE_URI, group.imageUri);
                intent.putExtra(GROUP_LIST_POSITION, position);
                startActivityForResult(intent, REQ_CODE_SELECT_GROUP_LIST_ITEM);
            }
        });

        if (savedInstanceState != null) {
            groupList = savedInstanceState.getParcelableArrayList(GROUP_LIST_KEY);
        }
        // ====================================================================================
        //
        // 3. 리스너 등록한다
        // ====================================================================================
        groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ListItemPopupMenuActivity.class);
                Group group = groupList.get(position);
                intent.putExtra(ListItemPopupMenuActivity.WHO_CALL_LIST_ITEM_POPUP_MENU_ACTIVITY, ListItemPopupMenuActivity.POPUP_MENU_CALLED_BY_GROUP_LIST_VIEW_ITEM_LONG_CLICK);
                intent.putExtra(GROUP_INFO, group);
                intent.putExtra(GROUP_LIST_POSITION, position);
                startActivityForResult(intent, REQ_CODE_SELECT_GROUP_LIST_ITEM);
                return true;
            }
        });

        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();


        addGroupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroupActivityIntent = new Intent(MainActivity.this, NewGroupPopupActivity.class);
                startActivityForResult(newGroupActivityIntent, NEW_GROUP_REQUEST);
            }
        });
        // ====================================================================================

        // 4. 레이아웃 갱신한다.
        // ====================================================================================
        groupImageView.setImageBitmap(groupImageBitmap);
        addGroupButton.setImageBitmap(addGroupImageButtonImage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Dlog.showToast) Toast.makeText(this, Dlog.s("그룹네임 저장"), Toast.LENGTH_SHORT).show();
        outState.putParcelableArrayList(GROUP_LIST_KEY, groupList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dlog.i("onStart()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Dlog.i("onActivityResult()");
        if (requestCode == NEW_GROUP_REQUEST) {
            if (resultCode == RESULT_OK) {
                Group group = new Group();
                group.name = data.getStringExtra(NewGroupPopupActivity.NEW_GROUP_NAME);
                group.imageUri = data.getParcelableExtra(NewGroupPopupActivity.NEW_GROUP_IMAGE);
                groupListAdatper.add(groupListAdatper.getCount(), group);
            }
        } else if (requestCode == REQ_CODE_SELECT_GROUP_LIST_ITEM) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                Dlog.i("position(" + position + ")반환");
                Group group = (Group) groupListAdatper.getItem(position);
                group.imageUri = data.getParcelableExtra(GROUP_IMAGE_URI);
                groupListAdatper.set(position, group);
            } else if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_EDIT_GROUP_INFO) {
                Dlog.d("RESULT_CODE_EDIT_MEMBER_INFO");
                Group group = data.getParcelableExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO);
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                groupListAdatper.set(position, group);
            } else if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_DELETE_GROUP_INFO) {
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                groupListAdatper.delete(position);
            }
        } else if (requestCode == ListItemPopupMenuActivity.REQ_CODE_EDIT_GROUP_INFO) {
            if (resultCode == RESULT_OK) {
                Group group = data.getParcelableExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO);
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                groupListAdatper.set(position, group);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.i("onDestroy()");
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }
}
