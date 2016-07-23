package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.externalIntent.NagneSMS;
import com.nagnek.android.externalIntent.Phone;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.AsyncDrawable;
import com.nagnek.android.nagneImage.BitmapShape;
import com.nagnek.android.nagneImage.BitmapWorkerOptions;
import com.nagnek.android.nagneImage.BitmapWorkerTask;
import com.nagnek.android.sharedString.Storage;
import com.nagnek.nagneJavaUtil.NagneString;

import java.util.ArrayList;

import static com.nagnek.android.nagneImage.BitmapWorkerTask.cancelPotentialWork;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class MemberListAdapter extends BaseAdapter {
    public final static String SHOW_MEMBER_KEY = "com.nagnek.android.meetingmanagement.SHOW_MEMBER";
    private static final String MESSAGE_BODY = "안녕하세요 김용탁입니다.";
    static int memberImageId;   // 멤버 이미지 id
    Activity activity = null;
    ArrayList<Member> memberList = null;
    LayoutInflater layoutInflater = null;
    private float memberImageLength;
    private float pushIconLength;
    private static Member dialog_member;    // dialog 인자값 멤버
    private static boolean isShowDialog = false; // dialog 보이고 있니? 다이얼로그 떠있으면 true, 아니면 false
    private static int dialogListPosition = 0; // dialog 인자의 리스트의 position
    Bitmap mPlaceHolderBitmap;
    Resources mResources;


    public MemberListAdapter(Activity activity, ArrayList<Member> memberList) {
        this.activity = activity;
        this.memberList = memberList;
        this.layoutInflater = LayoutInflater.from(activity);
        memberImageId = R.drawable.user;
        memberImageLength = MainActivity.showable_small_icon_length;
        pushIconLength = MainActivity.push_icon_length;
        if(isShowDialog) {
            showWarningDialog(dialogListPosition, dialog_member);
        }
        mResources = activity.getResources();
        setLoadingImage(memberImageId);
    }

    public void setLoadingImage(int resId) {
        mPlaceHolderBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;   //phone.call 할때 final 변수로 필요해서 position값을 final로 변형
        // 1. 리스트의 한 항목에 해당하는 레이아웃을 생성한다
        // 어댑터뷰가 재사용할 뷰를 넘겨주지 않은 경우에만 새로운 뷰를 생성한다.
        // ====================================================================================
        View itemLayout = convertView;
        ViewHolder viewHolder = null;
        // ====================================================================================

        // 2. 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        if (itemLayout == null) {
            itemLayout = layoutInflater.inflate(R.layout.member_list_view_item_layout, null);
            // View Holder를 생성하고 사용할 자식 뷰를 찾아 View Holder에 참조시킨다.
            // 생성된 View Holder는 아이템에 설정해 두고 다음에 아이템 재사용시 참조한다.
            // ------------------------------------------------------------------------------------
            viewHolder = new ViewHolder();
            viewHolder.memberNameTextView = (TextView) itemLayout.findViewById(R.id.member_name);
            viewHolder.memberIdTextView = (TextView) itemLayout.findViewById(R.id.member_id);
            viewHolder.callButton = (ImageView) itemLayout.findViewById(R.id.call_button);
            viewHolder.messageButton = (ImageView) itemLayout.findViewById(R.id.message_button);
            viewHolder.phoneNumberTextView = (TextView) itemLayout.findViewById(R.id.phone_number);
            viewHolder.memberImageView = (ImageView) itemLayout.findViewById(R.id.member_image);
            viewHolder.editMemberImageView = (ImageView) itemLayout.findViewById(R.id.edit_button);
            viewHolder.deleteMemberImageView = (ImageView) itemLayout.findViewById(R.id.delete_button);
            itemLayout.setTag(viewHolder);
            // ------------------------------------------------------------------------------------
        } else {
            // 재사용 아이템에는 이전에 View Holder 객체를 설정해 두었다.
            // 그러므로 설정된 View Holder 객체를 이용해서 findViewById 함수를
            // 사용하지 않고 원하는 뷰를 참조할 수 있다.
            viewHolder = (ViewHolder) itemLayout.getTag();
        }
        // ====================================================================================
        //
        // 3. 리스너 등록한다 (position마다 다른)
        // ====================================================================================
        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phone phone = new Phone();
                phone.call(activity, memberList.get(pos).phone_number);
            }
        });
        viewHolder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NagneSMS nagneSMS = new NagneSMS();
                nagneSMS.sendSMS(activity, memberList.get(position).phone_number, MESSAGE_BODY);
            }
        });

        viewHolder.editMemberImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditMemberInfoActivity.class);
                intent.putExtra(ListItemPopupMenuActivity.EDIT_MEMBER_INFO, memberList.get(position));
                intent.putExtra(GroupInfoActivity.MEMBER_LIST_POSITION, position);
                activity.startActivityForResult(intent, ListItemPopupMenuActivity.REQ_CODE_EDIT_MEMBER_INFO);
            }
        });

        viewHolder.deleteMemberImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });
        // ====================================================================================

        // 4. 레이아웃 갱신한다.
        // ====================================================================================
        viewHolder.memberIdTextView.setText(String.valueOf(position + 1));
        viewHolder.memberNameTextView.setText(memberList.get(position).name);
        if (viewHolder.phoneNumberTextView != null) {
            viewHolder.phoneNumberTextView.setText(memberList.get(position).phone_number);
        }

        if (viewHolder.memberImageView != null) {
            Uri imageUri = memberList.get(position).imageUri;
            if (imageUri != null) {
                loadCircleBitmap(imageUri, viewHolder.memberImageView);
            } else {
                loadBitmap(memberImageId, viewHolder.memberImageView);
            }
        }

        return itemLayout;
    }

    public void add(int index, Member member) {
        if (GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.containsKey(member.phone_number)) {
            showWarningDialog(index, member);
        } else {
            addData(index, member);
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(member.phone_number, GroupInfoActivity.group_position + "|" + index);
        }
    }

    public void addData(int index, Member member) {
        // 리스트 갱신
        memberList.add(index, member);
        syncSharedPreferenceToMemberListAfterAddMemberListItem(index, member);
        notifyDataSetChanged();
    }

    // 추가된 아이템 인덱스 이후 SharedPreference에서 저장된 데이터들을 인덱스 하나씩 미는 함수.
    public void syncSharedPreferenceToMemberListAfterAddMemberListItem(int addMemberIndex, Member member) {
        int currentGroupIndex = GroupInfoActivity.group_position;
        int currentMemberNumber = this.getCount();
        // ====================================================================================
        // 데이터는 인덱스 0부터 들어가 있다
        // 추가한 인덱스보다 큰 인덱스들을 가진 저장되어 있던 데이터들을 하나 뒤의 인덱스로 옮긴다.
        // 멤버 정보 key는 아래와 같다
        // 그룹position + "|" + 멤버position
        // ====================================================================================
        for (int i = currentMemberNumber - 2; i >= addMemberIndex; --i) {
            String value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + i); // i, 즉 추가된 인덱스부터 가져온다
            NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, currentGroupIndex + "|" + (i + 1));  // 가져온 데이터를 이전 인덱스에 차례대로 삽입
        }

        // 추가된 인덱스의 데이터 갱신
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, member, currentGroupIndex + "|" + addMemberIndex);

        // 멤버수 갱신
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, currentMemberNumber, currentGroupIndex + "|" + Storage.MEMBER_NUMBER);
    }

    public void delete(int index) {
        // 1. 해쉬 맵 갱신
        // 전화번호 바뀐 경우 인덱스에서 이전 저장 번호 제거 후 새로운 번호 추가
        // ====================================================================================
        Member m = (Member)getItem(index);
        if (GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.containsKey(m.phone_number)) {
            String valueList = findAndGenerateNewSpecificDataInArray(m.phone_number, index);
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.remove(m.phone_number);
            if(valueList != null) {
                GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(m.phone_number, valueList);
            }
        }


        // ====================================================================================
        //
        // 2. 데이터 갱신
        // ====================================================================================
        memberList.remove(index);
        syncSharedPreferenceToMemberListAfterDeleteListItem(index);
        notifyDataSetChanged();
    }

    private String findAndGenerateNewSpecificDataInArray(String key, int index) { // 해시맵에서 key에 해당 되는 value list중 해당 position 을 삭제한 valuelist를 반환한다
        String gotValue = GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.get(key);
        String[] valueList = NagneString.convertStringToArray(gotValue);
        int resultStringLength = valueList.length - 1;

        String tempList = null;
        if(resultStringLength > 0) {

            for(int i=0, j = 0; i<valueList.length; ++i) {
                if(valueList[i].equals(GroupInfoActivity.group_position + "|" + index) != true) {
                    tempList = tempList + valueList[i];
                }
            }
        }
        return tempList;
    }

    // 삭제된 아이템 이후 SharedPreference에서 저장된 데이터들을 인덱스 하나씩 당기는 함수.
    public void syncSharedPreferenceToMemberListAfterDeleteListItem(int deleteMemberIndex) {
        int currentGroupIndex = GroupInfoActivity.group_position;
        int currentMemberNumber = this.getCount();  //this.getCount()는 아이템을 삭제한 이후의 리스트 개수이다
        // ====================================================================================
        //
        // 멤버 리스트 데이터 삭제 후 저장되어 있던 데이터들을 갱신한다
        // ====================================================================================
        NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + deleteMemberIndex);
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, currentMemberNumber, Storage.MEMBER_NUMBER);
        // ====================================================================================
        //
        // 삭제 이후 인덱스들의 데이터들을 하나씩 앞으로 당긴다. (빈 인덱스를 채우기 위해)
        // 데이터는 인덱스 0부터 들어가 있다
        // 삭제한 인덱스보다 큰 인덱스들을 가진 저장되어 있던 데이터들을 하나 앞의 인덱스로 옮겨 삭제되어 생긴 빈공간을 없앤다.
        // 반복문 조건에 groupList.size에 +1을 더한 것은 리스트뷰에서 아이템 삭제 했으나 SharedPrefence에서는 삭제된게 아니므로 +1 인덱스의 데이터가 있으므로 그것도 하나 앞으로 옮겨야 하기 때문이다.
        // 멤버 정보 key는 아래와 같다
        // 그룹position + "|" + 멤버position
        // ====================================================================================
        for (int i = deleteMemberIndex; i < currentMemberNumber; ++i) {
            String value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + (i + 1)); // i+1, 즉 삭제 이후 인덱스 부터 가져온다
            NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, currentGroupIndex + "|" + i);  // 가져온 데이터를 이전 인덱스에 차례대로 삽입
        }

        // ====================================================================================
        //
        // 맨 마지막 멤버 정보 제거 (이미 옮겨진 정보이므로 삭제한다)
        // ====================================================================================
        if (deleteMemberIndex != currentMemberNumber) {  // 위쪽에서 이미 삭제한 경우는 제외한다
            NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + currentMemberNumber);
        }
    }

    public void clear() {
        memberList.clear();
        deleteAllMemberListDataFromSharedPreference();
        notifyDataSetChanged();
    }

    public void deleteAllMemberListDataFromSharedPreference() {
        int currentGroupIndex = GroupInfoActivity.group_position;
        String savedMemberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + Storage.MEMBER_NUMBER);
        int savedMemberNumber = Integer.parseInt(savedMemberNumberString);
        for (int i = 0; i < savedMemberNumber; ++i) {
            NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, currentGroupIndex + "|" + i);
        }
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, 0, currentGroupIndex + "|" + Storage.MEMBER_NUMBER);
    }

    public void set(int index, Member member) {
        // 1. 해쉬 맵 갱신
        // 전화번호 바뀐 경우 인덱스에서 이전 저장 번호 제거 후 새로운 번호 추가
        // ====================================================================================
        if (GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.containsKey(member.phone_number)) {
            Member m = (Member) getItem(index);
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.remove(m.phone_number);
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(member.phone_number, GroupInfoActivity.group_position + "|" + index);
        } else {
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(member.phone_number, GroupInfoActivity.group_position + "|" + index);
        }
        // ====================================================================================
        //
        // 2. 데이터 갱신
        // ====================================================================================
        memberList.set(index, member);
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, member, GroupInfoActivity.group_position + "|" + index);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView memberNameTextView;
        TextView memberIdTextView;
        ImageView callButton;
        ImageView messageButton;
        TextView phoneNumberTextView;
        ImageView memberImageView;
        ImageView editMemberImageView;
        ImageView deleteMemberImageView;
    }

    private void appendValue(String key, String value) {
        if (GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.containsKey(key)) {
            String gotValue = GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.get(key);
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(key, gotValue + "," + value);
        } else {
            GroupInfoActivity.phoneNumberKeyToMatchGroupPositionAndMemberPosition.put(key, value);
        }
    }

    public void showWarningDialog(final int index, final Member member) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("기존에 같은 전화번호가 있습니다.")
                .setMessage("그래도 추가하시겠습니까?")
                .setCancelable(true)    // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_member = null;
                        Dlog.i("멤버 값 추가");
                        // 1. 해쉬값 갱신
                        appendValue(member.phone_number, GroupInfoActivity.group_position + "|" + index);
                        // 2. 데이터 갱신
                        addData(index, member);
                        isShowDialog = false;
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_member = null;
                        isShowDialog = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        isShowDialog = true;
        GroupInfoActivity.dialog_list_position = index;
        dialog_member = member;
        dialogListPosition = index;
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerOptions bitmapWorkerOptions = new BitmapWorkerOptions.Builder(activity).resource(resId).build();
        loadBitmapThroughThread(bitmapWorkerOptions, imageView);
    }

    public void loadBitmap(Uri imageUri, ImageView imageView) {
        BitmapWorkerOptions bitmapWorkerOptions = new BitmapWorkerOptions.Builder(activity).resource(imageUri).build();
        loadBitmapThroughThread(bitmapWorkerOptions, imageView);
    }

    public void loadCircleBitmap(Uri imageUri, ImageView imageView) {
        BitmapWorkerOptions bitmapWorkerOptions = new BitmapWorkerOptions.Builder(activity).shape(BitmapShape.Circle).resource(imageUri).build();
        loadBitmapThroughThread(bitmapWorkerOptions, imageView);
    }

    // 비트맵 로딩을 BitmapWorkerTask스레드를 통해 비트맵 로딩함.
    public void loadBitmapThroughThread(BitmapWorkerOptions bitmapWorkerOptions, ImageView imageView) {
        if (cancelPotentialWork(bitmapWorkerOptions, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(activity.getApplicationContext(), imageView, memberImageLength, memberImageLength);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(activity.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(bitmapWorkerOptions);
        }
    }
}

