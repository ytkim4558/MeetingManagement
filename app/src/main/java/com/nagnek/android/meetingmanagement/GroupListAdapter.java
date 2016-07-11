package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;
import com.nagnek.android.nagneImage.NagneCircleImage;
import com.nagnek.android.sharedString.Storage;

import java.util.ArrayList;

/**
 * Created by yongtakpc on 2016. 7. 7..
 */
public class GroupListAdapter extends BaseAdapter {
    static Drawable blank;   // 그룹 이미지
    Context context = null;
    ArrayList<Group> groupList = null;
    LayoutInflater layoutInflater = null;

    GroupListAdapter(Context context, ArrayList<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
        this.layoutInflater = LayoutInflater.from(context);
        blank = context.getResources().getDrawable(R.drawable.blank);
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.group_image);
            itemLayout.setTag(viewHolder);
            // ------------------------------------------------------------------------------------
        } else {
            // 재사용 아이템에는 이전에 View Holder 객체를 설정해 두었다.
            // 그러므로 설정된 View Holder 객체를 이용해서 findViewById 함수를
            // 사용하지 않고 원하는 뷰를 참조할 수 있다.
            viewHolder = (ViewHolder) itemLayout.getTag();
        }
        // ====================================================================================

        // 4. 레이아웃 갱신한다.
        // ====================================================================================
        viewHolder.groupIdTextView.setText(String.valueOf(position + 1));
        viewHolder.groupNameTextView.setText(groupList.get(position).name);
        if (viewHolder.imageView != null) {
            Uri imageUri = groupList.get(position).imageUri;
            if (imageUri != null) {
                viewHolder.imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(context, imageUri));
            } else {
                viewHolder.imageView.setImageDrawable(blank);
            }
        }

        return itemLayout;
    }

    public void add(int index, Group group) {
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey((Activity) context, Storage.SAVE_MEMBER_INFO_FILE, group, this.getCount());
        groupList.add(index, group);
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey((Activity) context, Storage.SAVE_MEMBER_INFO_FILE, this.getCount(), Storage.GROUP_NUMBER);
        notifyDataSetChanged();
    }

    public void delete(int index) {
        groupList.remove(index);
        NagneSharedPreferenceUtil.removeKey((Activity) context, Storage.SAVE_MEMBER_INFO_FILE, index);
        NagneSharedPreferenceUtil.saveValueToSharedPreferenceUsingKey((Activity) context, Storage.SAVE_MEMBER_INFO_FILE, this.getCount(), Storage.GROUP_NUMBER);
        notifyDataSetChanged();
    }

    public void clear() {
        groupList.clear();
        notifyDataSetChanged();
    }

    public void set(int index, Group group) {
        groupList.set(index, group);
        NagneSharedPreferenceUtil.saveObjectToSharedPreferenceUsingKey((Activity) context, Storage.SAVE_MEMBER_INFO_FILE, group, index);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView groupNameTextView;
        TextView groupIdTextView;
        ImageView imageView;
    }
}
