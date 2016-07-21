package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.AsyncDrawable;
import com.nagnek.android.nagneImage.BitmapShape;
import com.nagnek.android.nagneImage.BitmapWorkerOptions;
import com.nagnek.android.nagneImage.BitmapWorkerTask;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.nagneImage.NagneImage;
import com.nagnek.android.sharedString.Storage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.nagnek.android.nagneImage.BitmapWorkerTask.cancelPotentialWork;

/**
 * Created by yongtakpc on 2016. 7. 7..
 */
public class GroupListAdapter extends BaseAdapter {
    private int groupImageId;   // 그룹 이미지
    private int editGroupImageId; // 그룹 수정 이미지
    private int deleteGroupImageid; // 그룹 삭제 이미지
    private float groupImageLength;
    private float pushIconLength;

    Activity activity = null;
    ArrayList<Group> groupList = null;
    LayoutInflater layoutInflater = null;
    Bitmap mPlaceHolderBitmap;
    Resources mResources;

    class PhotoTask {
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<Bitmap> bitmapWeakReference;
        PhotoTask(ImageView imageView, Bitmap bitmap) {
            imageViewWeakReference  = new WeakReference<ImageView>(imageView);
            bitmapWeakReference = new WeakReference<Bitmap>(bitmap);
        }
    }

    class BitmapLoadingOptionTask {
        int position = -1;
        Uri imageUri;
        int imageId;
        boolean isCancel;
        BitmapLoadingOptionTask(Uri imageUri, int imageId, int position) {
            this.imageId = imageId;
            this.imageUri = imageUri;
            this.position = position;
        }
    }

    static class AsyncImage extends BitmapDrawable {
        private final WeakReference<BitmapLoadingOptionTask> bitmapLoadingOptionTaskWeakReference;

        public AsyncImage(Resources res, Bitmap bitmap,
                             BitmapLoadingOptionTask bitmapLoadingOptionTask) {
            super(res, bitmap);
            bitmapLoadingOptionTaskWeakReference =
                    new WeakReference<BitmapLoadingOptionTask>(bitmapLoadingOptionTask);
        }

        public BitmapLoadingOptionTask getBitmapLoadingOptionTask() {
            return bitmapLoadingOptionTaskWeakReference.get();
        }


    }

    static final private int MESSAGE_DRAW_CURRENT_IMAGE_TO_CURRENT_IMAGE_VIEW = 1;

    // 메시지 큐에 메시지를 추가하기 위한 핸들러를 생성한다.
    // ====================================================================================
    Handler mHandler = new Handler() {
        // 메시지 큐는 핸들러에 존재하는 handleMessage 함수를 호출해준다.
        // ====================================================================================
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_DRAW_CURRENT_IMAGE_TO_CURRENT_IMAGE_VIEW: {
                    PhotoTask photoTask = (PhotoTask)msg.obj;
                    ImageView imageView = photoTask.imageViewWeakReference.get();
                    Bitmap bitmap = photoTask.bitmapWeakReference.get();
                    if(imageView != null && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                    bitmap = null;
                    break;
                }
            }
        }
    };

    GroupListAdapter(Activity activity, ArrayList<Group> groupList) {
        this.activity = activity;
        this.groupList = groupList;
        this.layoutInflater = LayoutInflater.from(this.activity);
        groupImageId = R.drawable.group;
        editGroupImageId = R.drawable.edit_group;
        deleteGroupImageid = R.drawable.delete_group;
        groupImageLength = MainActivity.showable_small_icon_length;
        pushIconLength = MainActivity.push_icon_length;
        mResources = activity.getResources();
        setLoadingImage(groupImageId);
        Dlog.i("GroupListAdapter()");
    }

    public void setLoadingImage(int resId) {
        mPlaceHolderBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 1. 리스트의 한 항목에 해당하는 레이아웃을 생성한다
        // 어댑터뷰가 재사용할 뷰를 넘겨주지 않은 경우에만 새로운 뷰를 생성한다.
        // ====================================================================================
        View itemLayout = convertView;
        ViewHolder viewHolder = null;
        // ====================================================================================

        // 2. 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        if (itemLayout == null) {
            itemLayout = layoutInflater.inflate(R.layout.group_list_view_item_layout, null);
            // View Holder를 생성하고 사용할 자식 뷰를 찾아 View Holder에 참조시킨다.
            // 생성된 View Holder는 아이템에 설정해 두고 다음에 아이템 재사용시 참조한다.
            // ------------------------------------------------------------------------------------
            viewHolder = new ViewHolder();
            viewHolder.groupNameTextView = (TextView) itemLayout.findViewById(R.id.group_name);
            viewHolder.groupIdTextView = (TextView) itemLayout.findViewById(R.id.group_id);
            viewHolder.groupImageView = (ImageView) itemLayout.findViewById(R.id.group_image);
            viewHolder.editGroupImageButton = (ImageView) itemLayout.findViewById(R.id.edit_button);
            viewHolder.deleteGroupImageButton = (ImageView) itemLayout.findViewById(R.id.delete_button);

            // 레이아웃 갱신 (position값 상관없는 것)
            viewHolder.editGroupImageButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(activity.getResources(), editGroupImageId, pushIconLength, pushIconLength));
            viewHolder.deleteGroupImageButton.setImageBitmap(NagneImage.decodeSampledBitmapFromResource(activity.getResources(), deleteGroupImageid, pushIconLength, pushIconLength));
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
        viewHolder.editGroupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditGroupInfoActivity.class);
                intent.putExtra(ListItemPopupMenuActivity.EDIT_GROUP_INFO, groupList.get(position));
                intent.putExtra(MainActivity.GROUP_LIST_POSITION, position);
                activity.startActivityForResult(intent, ListItemPopupMenuActivity.REQ_CODE_EDIT_GROUP_INFO);
            }
        });
        viewHolder.deleteGroupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });
        // ====================================================================================

        // 4. 레이아웃 갱신한다.
        // ====================================================================================
        viewHolder.groupIdTextView.setText(String.valueOf(position + 1));
        viewHolder.groupNameTextView.setText(groupList.get(position).name);

        BitmapLoadingOptionTask bitmapLoadingOptionTask = new BitmapLoadingOptionTask(groupList.get(position).imageUri, groupImageId, position);

        loadBitmapByHandlerViaThread(bitmapLoadingOptionTask, viewHolder.groupImageView);

        return itemLayout;
    }

    public void add(int index, Group group) {
        groupList.add(index, group);
        syncSharedPreferenceToGroupListItemAdd(index, group);
        notifyDataSetChanged();
    }

    // 추가된 아이템 인덱스 이후 SharedPreference에서 저장된 데이터들을 인덱스 하나씩 뒤로 밀고 아이템 넣는 함수
    public void syncSharedPreferenceToGroupListItemAdd(int addGroupIndex, Group group) {
        // ====================================================================================
        //
        // 데이터는 인덱스 0부터 들어가 있다
        // 추가한 인덱스보다 큰 인덱스들을 가진 저장되어 있던 데이터들을 하나 뒤의 인덱스로 옮긴다.
        // groupList.size - 2를 한 이유는 비록 추가된 인덱스는 -1
        // ====================================================================================
        Dlog.i("groupListSize : " + NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, Storage.GROUP_NUMBER));
        for (int i = groupList.size() - 2; i >= addGroupIndex; --i) {
            // 1. 그룹 정보 갱신 (key는 그룹리스트의 position)
            String value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, i);
            NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, i + 1);
            Dlog.i("current value : " + value);
            // ====================================================================================
            //
            // 2. 멤버 정보 갱신 key는 아래와 같다
            // 그룹position + "|" + 멤버position
            // ====================================================================================
            String memberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, i + "|" + Storage.MEMBER_NUMBER);
            if(memberNumberString != null) {
                for (int j = 0; j < Integer.valueOf(memberNumberString) ; ++j) {
                    value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, i + "|" + j);
                    NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, (i + 1) + "|" + j);
                }
            }
        }

        // 추가된 인덱스의 데이터 갱신
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, group, addGroupIndex);

        // 그룹 수 갱신
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, this.getCount(), Storage.GROUP_NUMBER);
    }

    public void delete(int index) {
        groupList.remove(index);
        syncSharedPreferenceToGroupListItemDelete(index);
        notifyDataSetChanged();
    }

    public void clear() {
        groupList.clear();
        syncSharedPreferenceToGroupListDelete();
        notifyDataSetChanged();
    }

    void syncSharedPreferenceToGroupListDelete() {
        String savedGroupNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, Storage.GROUP_NUMBER);
        int savedGroupNumber = Integer.parseInt(savedGroupNumberString);
        for (int i = 0; i < savedGroupNumber; ++i) {
            // 1. 그룹 정보 삭제 (key는 그룹리스트의 position)
            NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, i);
            // ====================================================================================
            //
            // 2. 멤버 정보 삭제 key는 아래와 같다
            // 그룹position + "|" + 멤버position
            // ====================================================================================
            String memberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, i + "|" + Storage.MEMBER_NUMBER);
            if(memberNumberString != null) {
                for (int j = 0; j < Integer.valueOf(memberNumberString); ++j) {
                    NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, i + j);
                }
            }
        }
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, 0, Storage.GROUP_NUMBER);
    }

    public void set(int index, Group group) {
        groupList.set(index, group);
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, group, index);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView groupNameTextView;
        TextView groupIdTextView;
        ImageView groupImageView;
        ImageView editGroupImageButton;
        ImageView deleteGroupImageButton;
    }

    // 삭제된 아이템 이후 SharedPreference에서 저장된 데이터들을 인덱스 하나씩 당기는 함수.
    public void syncSharedPreferenceToGroupListItemDelete(int deleteGroupIndex) {
        // 리스트뷰에서 데이터 삭제 후 저장되어 있던 데이터들을 갱신한다
        NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, deleteGroupIndex);
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, this.getCount(), Storage.GROUP_NUMBER);
        String memberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, deleteGroupIndex + "|" + Storage.MEMBER_NUMBER); // key는 (그룹position + "|" + Storage.MEMBER_NUMBER) 이다.
        int memberNumber = 0;
        if (memberNumberString == null) {
            memberNumber = 0;
        } else {
            memberNumber = Integer.parseInt(memberNumberString);
        }
        for (int i = 0; i < memberNumber; ++i) {
            NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, deleteGroupIndex + "|" + i);
        }
        // ====================================================================================
        //
        // 데이터는 인덱스 0부터 들어가 있다
        // 삭제한 인덱스보다 큰 인덱스들을 가진 저장되어 있던 데이터들을 하나 앞의 인덱스로 옮겨 삭제되어 생긴 빈공간을 없앤다.
        // 반복문 조건에 groupList.size에 +1을 더한 것은 리스트뷰에서 아이템 삭제 했으나 SharedPrefence에서는 삭제된게 아니므로 +1 인덱스의 데이터가 있으므로 그것도 하나 앞으로 옮겨야 하기 때문이다.
        // ====================================================================================
        for (int i = deleteGroupIndex; i < groupList.size(); ++i) {
            // 1. 그룹 정보 갱신 (key는 그룹리스트의 position)
            String value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, i + 1);
            NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, i);
            // ====================================================================================
            //
            // 2. 멤버 정보 갱신
            // key는 아래와 같다
            // 그룹position + "|" + 멤버position
            // ====================================================================================
            memberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, (i + 1) + "|" + Storage.MEMBER_NUMBER);
            NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, memberNumberString, i + "|" + Storage.MEMBER_NUMBER);
            Dlog.i("옮겨질 데이터 수 : " + memberNumberString);
            if(memberNumberString != null) {
                for (int j = 0; j < Integer.valueOf(memberNumberString); ++j) {
                    value = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, (i + 1) + "|" + j);
                    NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey(activity, Storage.SAVE_MEMBER_INFO_FILE, value, i + "|" + j);
                }
            }
        }

        // ====================================================================================
        //
        // 맨 마지막 그룹 정보와 그 그룹에 해당되는 멤버들 정보 제거
        // ====================================================================================
        // 그룹 정보 제거
        if(deleteGroupIndex != groupList.size()) {  // 위에서 deleteGroupIndex는 이미 삭제했으므로 생략
            NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, groupList.size());

            // 해당 그룹의 멤버 정보 제거
            memberNumberString = NagneSharedPreferenceUtil.getValue(activity, Storage.SAVE_MEMBER_INFO_FILE, groupList.size() + "|" + Storage.MEMBER_NUMBER);
            if (memberNumberString != null) {
                for (int i = 0; i < Integer.valueOf(memberNumberString); ++i) {
                    NagneSharedPreferenceUtil.removeKey(activity, Storage.SAVE_MEMBER_INFO_FILE, groupList.size() + "|" + i);
                }
            }
        }
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


    public void loadBitmapThroughThread(BitmapWorkerOptions bitmapWorkerOptions, ImageView imageView) {
        if (cancelPotentialWork(bitmapWorkerOptions, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(activity.getApplicationContext(), imageView, groupImageLength, groupImageLength);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(activity.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(bitmapWorkerOptions);
        }
    }

    public void loadBitmapByHandlerViaThread(final BitmapLoadingOptionTask bitmapLoadingOptionTask, final ImageView imageView) {
        if (cancelPrevWork(bitmapLoadingOptionTask.position, imageView)) {
            Thread loadingImageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    if (bitmapLoadingOptionTask.imageUri != null) {
                        bitmap = NagneCircleImage.getCircleBitmap(activity.getApplicationContext(), bitmapLoadingOptionTask.imageUri, groupImageLength, groupImageLength);
                    } else {
                        bitmap = NagneImage.decodeSampledBitmapFromResource(activity.getApplication().getResources(), groupImageId, groupImageLength, groupImageLength);
                    }
                    PhotoTask photoTask = new PhotoTask(imageView, bitmap);
                    //메시지 큐에 담을 메시지를 하나 생성한다
                    // ------------------------------------------------------------------
                    Message message = Message.obtain(mHandler);
                    // ------------------------------------------------------------------

                    // 핸들러의 handleMessage로 전달할 값들을 설정한다.
                    // ------------------------------------------------------------------
                    // 무엇을 실행하는 메시지인지 구분하기 위해 구분자 설정
                    message.what = MESSAGE_DRAW_CURRENT_IMAGE_TO_CURRENT_IMAGE_VIEW;
                    // 메시지가 실행될 때 참조하는 int형 데이터 설정
                    message.arg1 = bitmapLoadingOptionTask.position;
                    // 메시지가 실행될 때 참조하는 Object형 데이터 설정
                    message.obj = photoTask;

                    if(bitmapLoadingOptionTask.isCancel != true) {
                        // 핸들러를 통해 메시지를 메시지 큐로 보낸다.
                        // ------------------------------------------------------------------
                        mHandler.sendMessage(message);
                        // ------------------------------------------------------------------
                    }
                }
            });
            loadingImageThread.start();
        }
    }

    boolean cancelPrevWork(int newListPosition, ImageView imageView) {
        final BitmapLoadingOptionTask prevBitmapLoadingOptionTask = getBitmapLoadingOptionTask(imageView);

        if (prevBitmapLoadingOptionTask != null) {
            final int prevListPosition = prevBitmapLoadingOptionTask.position;
            // If bitmapData is not yet set or it differs from the new data
            if (prevListPosition == -1 || prevListPosition != newListPosition) {
                // Cancel previous task
                prevBitmapLoadingOptionTask.isCancel = true;
                //mHandler.removeMessages(MESSAGE_DRAW_CURRENT_IMAGE_TO_CURRENT_IMAGE_VIEW);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public BitmapLoadingOptionTask getBitmapLoadingOptionTask(ImageView imageView) {
        if(imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncImage) {
                final AsyncImage asyncImage = (AsyncImage) drawable;
                asyncImage.getBitmapLoadingOptionTask();
            }
        }
        return null;
    }
}
