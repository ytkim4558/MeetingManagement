package com.nagnek.android.meetingmanagement;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.NagneImage;
import com.nagnek.android.sharedString.Storage;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NoticeDialogFragment.NoticeDialogListener{

    public static final String GROUP_LIST_POSITION = "com.nagnek.android.meetingmanagement.GROUP_LIST_POSITION";
    public static final String GROUP_NAME = "com.nagnek.android.meetingmanagement.GROUP_NAME";
    public static final String GROUP_IMAGE_URI = "com.nagnek.android.meetingmanagement.GROUP_IMAGE_URI";
    public static final String GROUP_INFO = "com.nagnek.android.meetingmanagement.GROUP_INFO";
    public final static String SHARE_VIEW_NAME = "SHARE_VIEW_NAME";
    public static final int REQ_CODE_SELECT_GROUP_LIST_ITEM = 17;
    public static final String POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY = "com.nagnek.android.meetingmanagement.POPUP_MENU_CALLED_BY_GROUP_LIST_ITEM_LONG_CLICK_KEY";
    public static float showable_small_icon_length;
    public static float push_icon_length;

    static final int NEW_GROUP_REQUEST = 1;
    static final int NEW_GROUP_GENERATE = 2;
    static final int NEW_GROUP_FALSE = 3;
    private final String GROUP_LIST_KEY = "GROUP_LIST_KEY";

    static final int MOVE_DURATION = 1000;
    String groupName = null;
    ArrayList<Group> groupList = null;
    private int groupNumber;

    ListView groupListView = null;
    private GroupListAdapter groupListAdatper = null;
    ImageView groupImageView;
    ImageView addGroupButton;
    boolean isShowWarningDialog = true;
    boolean mAdballonAnimate = false;

    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Dlog.i("onCreate()");
        // ====================================================================================
        //
        // 1. 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        groupImageView = (ImageView) findViewById(R.id.group_image);
        addGroupButton = (ImageView) findViewById(R.id.add_group_button);
        // ====================================================================================
        //
        // 2. 데이터 설정한다.
        // ====================================================================================
        showable_small_icon_length = getResources().getDimension(R.dimen.image_view_showable_small_icon_length);
        push_icon_length = getResources().getDimension(R.dimen.image_view_push_icon_length);
        Bitmap groupImageBitmap = NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.database, showable_small_icon_length, showable_small_icon_length);
        Bitmap addGroupImageButtonImage = NagneImage.decodeSampledBitmapFromResource(getResources(), R.drawable.add_group, push_icon_length, push_icon_length);
        String groupNumberString = NagneSharedPreferenceUtil.getValue(this, Storage.SAVE_MEMBER_INFO_FILE, Storage.GROUP_NUMBER);
        Dlog.i(groupNumberString);
        groupNumber = 0;
        if (groupNumberString == null) {
            groupNumber = 0;
            NagneSharedPreferenceUtil.setValue(this, Storage.SAVE_MEMBER_INFO_FILE, Storage.GROUP_NUMBER, 0);
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
        groupListAdatper = new GroupListAdapter(this, groupList, mDeleteButtonClickListener);

        // 리스트뷰에 어댑터 설정
        groupListView = (ListView) findViewById(R.id.group_list_view);
        groupListView.setAdapter(groupListAdatper);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, GroupInfoActivity.class);
                ImageView groupImageView = (ImageView)view.findViewById(R.id.group_image);
                Pair<View, String> pair = new Pair<>((View)groupImageView, SHARE_VIEW_NAME);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair);
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

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * OptionMenu 아이템이 선택될 때 호출 된다.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearDB:
                showNoticeDialog();
                break;
        }
        /*if(groupListAdatper != null) {
            groupListAdatper.clear();
        }*/
        return true;
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

    private Button.OnClickListener mDeleteButtonClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(final View v) {
            // 리스트뷰에서 아이템이 삭제되면 풍선 애니메이션이 동작한다.
            // 삭제되는 뷰는 점점 작아진다.
            if(mAdballonAnimate != true) {
                mAdballonAnimate = true;
                groupListView.setEnabled(false);
                // 풍선이 터지기 전까지 축소된다. (원래 크기의 0.7배)
                int firstVisiblePosition = groupListView.getFirstVisiblePosition();
                int position = groupListView.getPositionForView((LinearLayout) v.getParent());
                final View view = (View) groupListView.getChildAt(position - firstVisiblePosition);
                view.animate().setDuration(MOVE_DURATION).scaleX(0.7f).scaleY(0.7f).withEndAction(new Runnable() {
                    public void run() {
                        // 터진다. (뻥~) 사이즈 0
                        view.animate().scaleX(0.0f).scaleY(0.0f).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // 터진 애니메이션 끝난 후 삭제 후 삭제 아이템과 관련된 아이템들의 애니메이션이 동작한다.
                                // Delete the item from the adapter
                                animateRemoval(groupListView, view);
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
                long itemId = groupListAdatper.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = listview.getPositionForView(viewToRemove);
        groupListAdatper.delete(position);
        View replaceView = listview.getChildAt(position - listview.getFirstVisiblePosition());
        GroupListAdapter.ViewHolder vh = (GroupListAdapter.ViewHolder)replaceView.getTag();
        vh.needInflate = true;
        groupListAdatper.notifyDataSetChanged();
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
                    long itemId = groupListAdatper.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();

                    // startTop 이 null이 아니라는것은 화면상에 이미 보였던 아이템들이라는 것이다.
                    if(startTop != null) {
                        if(position < deletePosition) {   // 삭제한 아이템의 위에 있던 아이템들인 경우

                            // 현재 보이는 아이템
                            if(groupList.size() - 1 == lastVisiblePosition && firstVisiblePosition != 0) {
                                child.setTranslationY(-1 * child.getHeight());
                            }
                            moveUpDownBallonAnimation(child, i);
                        } else {    // 삭제한 아이템의 아래에 있던 아이템이나 삭제한 아이템의 위치에 생긴 아이템
                            // 화면창이 리스트뷰의 맨 아래에 있는 경우 아래로 이동하지 않는다.
                            // 단, 화면창에 리스트뷰의 첫번째 아이템이 보이는 경우(즉 리스트뷰가 충분히 크지 않아 스크롤바가 생기지 않은 경우나
                            // 스크롤바가 생겨도 리스트뷰의 마지막아이템과 첫번째 아이템이 한화면에 다 보이는 경우 (즉, 마지막 아이템이 일부만 보이는경우))
                            // 는 예외로 한다.
                            if(groupList.size() - 1 != lastVisiblePosition || (firstVisiblePosition == 0 && groupList.size() - 1 == lastVisiblePosition)) {
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
                            if(groupList.size() - 1 != lastVisiblePosition || (firstVisiblePosition == 0 && groupList.size() - 1 == lastVisiblePosition)) {
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
                        groupListView.setEnabled(true);
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
        int lastVisiblePosition = groupListView.getLastVisiblePosition();
        int firstVisiblePosition = groupListView.getFirstVisiblePosition();
        final float delta = halfViewSize + quadViewSize * (2 * (lastVisiblePosition - firstVisiblePosition - position + 1) - 1);

        // 아이템이 삭제되면 삭제된 뷰의 위에 있는 아이템들은 절반씩 찌끄러지면서 위로 밀린다. (뷰의 중심은 뷰의 절반만큼 이동되므로 translationY 가 뷰의 절반크기만큼 이동. scale은 절반만큼)
        child.animate().setDuration(MOVE_DURATION).translationY(delta).scaleY(0.5f).withEndAction(new Runnable() {
            @Override
            public void run() {
                // 그 후, 원래 상태로 복귀된다. (복귀될때 역시 scale의 절반만큼 중심이 이동되므로 translationY는 아래로)
                child.animate().setDuration(MOVE_DURATION).translationY(0).scaleY(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        groupListView.setEnabled(true);
//                        StudentAdapter.ViewHolder vh = (StudentAdapter.ViewHolder)child.getTag();
//                        vh.needInflate = true;
                        mAdballonAnimate = false;
                        //mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
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
        Dlog.i("onDestroy()");
        recycleImageView(groupImageView);
        recycleImageView(addGroupButton);
        super.onDestroy();
        if (Dlog.showToast) Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    void recycleImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if(d instanceof BitmapDrawable) {
            Dlog.d("recycle");
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            bitmap.recycle();
            bitmap = null;
        }
        d.setCallback(null);
    }

    // title : 다이얼로그 창 제목 : 예 ) 데이터가 지워집니다
    // NagneSMS : 다이얼로그 메시지 : 예 ) 정말로 데이터를 지우시겠습니까?
    // positiveMessageButtonText : "삭제"등을 사용한다. "ok", "확인"같은 애매한 메시지 말고 확실히 사용자가 인지가능한 메시지를 써야한다.
    // negativeMessageButtonText : "취소"와 같은 메시지
    // checkValue : 누른 값 확인하려고 두긴 했는데 사용 방법 아직 정하지 않음.
    public void showAlertDialog(String title, String message, String positiveMessageButtonText, String negativeMessageButtonText, boolean checkValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getApplicationContext());

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)    // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton(positiveMessageButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isShowWarningDialog = false;
                    }
                })
                .setNegativeButton(negativeMessageButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                        isShowWarningDialog = false;
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        isShowWarningDialog = true;
    }

    @Override
    public void onBackPressed() {
        // Alert 띄어서 종료시키기
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("종료 알림")
                .setMessage("정말로 종료하시겠습니까")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
/*
.setNeutralButton("취소", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
Toast.makeText(MainActivity.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
}
})
*/
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(groupListAdatper != null) {
            groupListAdatper.clear();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
