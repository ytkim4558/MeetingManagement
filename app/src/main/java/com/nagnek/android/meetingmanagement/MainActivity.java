package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public static final String GROUP_LIST_POSITION = "com.nagnek.android.meetingmanagement.GROUP_LIST_POSITION";
    public static final String GROUP_NAME = "com.nagnek.android.meetingmanagement.GROUP_NAME";
    public static final String GROUP_IMAGE_URI = "com.nagnek.android.meetingmanagement.GROUP_IMAGE_URI";
    public static final String GROUP_INFO = "com.nagnek.android.meetingmanagement.GROUP_INFO";
    public static final int REQ_CODE_SELECT_GROUP_LIST_ITEM = 17;
    public static final String POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY = "com.nagnek.android.meetingmanagement.POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY";

    static final int NEW_GROUP_REQUEST = 1;
    static final int NEW_GROUP_GENERATE = 2;
    static final int NEW_GROUP_FALSE = 3;
    private final String GROUP_LIST_KEY = "GROUP_LIST_KEY";
    String groupName = null;
    ArrayList<Group> groupList = null;

    ArrayList<Member> memberList = null;
    private GroupListAdapter groupListAdatper = null;
    ListView groupListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dlog.i("onCreate()");

        if (groupList == null && savedInstanceState == null) {
            groupList = new ArrayList<Group>();
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
                intent.putParcelableArrayListExtra(GroupInfoActivity.MEMBER_LIST_KEY, group.memberList);
                startActivityForResult(intent, REQ_CODE_SELECT_GROUP_LIST_ITEM);
            }
        });

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


        Button addGroupButton = (Button) findViewById(R.id.add_group_button);
        addGroupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroupActivityIntent = new Intent(MainActivity.this, NewGroupPopupActivity.class);
                startActivityForResult(newGroupActivityIntent, NEW_GROUP_REQUEST);
            }
        });

        if (savedInstanceState != null) {
            groupList = savedInstanceState.getParcelableArrayList(GROUP_LIST_KEY);
        }
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
        }

        if (requestCode == REQ_CODE_SELECT_GROUP_LIST_ITEM) {
            if (resultCode == RESULT_OK) {
                Dlog.i("멤버리스트 반환");
                ArrayList<Member> memberList = data.getParcelableArrayListExtra(GroupInfoActivity.MEMBER_LIST_KEY);
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                Dlog.i("position("+position+")반환");
                Group group = (Group)groupListAdatper.getItem(position);
                group.imageUri = data.getParcelableExtra(GROUP_IMAGE_URI);
                group.memberList = memberList;
                groupListAdatper.set(position, group);
            }

            else if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_EDIT_GROUP_INFO) {
                Dlog.d("RESULT_CODE_EDIT_MEMBER_INFO");
                Group group = data.getParcelableExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO);
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                groupListAdatper.set(position, group);
            } else if (resultCode == ListItemPopupMenuActivity.RESULT_CODE_DELETE_GROUP_INFO) {
                int position = data.getIntExtra(GROUP_LIST_POSITION, 0);
                groupListAdatper.delete(position);
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
