package com.nagnek.android.meetingmanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class MemberListAdapter extends BaseAdapter {
    Context context = null;
    ArrayList<Member> memberList = null;
    LayoutInflater layoutInflater = null;
    public final static String SHOW_MEMBER_KEY = "com.nagnek.android.meetingmanagement.SHOW_MEMBER";

    public MemberListAdapter(Context context, ArrayList<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
        this.layoutInflater = LayoutInflater.from(context);

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
        final int pos = position;
        // 1. 리스트의 한 항목에 해당하는 레이아웃을 생성한다
        // ====================================================================================
        View itemLayout = layoutInflater.inflate(R.layout.member_list_view_item_layout, null);
        // ====================================================================================

        // 2. 현재 아이템의 내용을 변경할 뷰를 찾는다.
        // ====================================================================================
        TextView memberNameTextView = (TextView) itemLayout.findViewById(R.id.member_name);
        TextView memberIdTextView = (TextView) itemLayout.findViewById(R.id.member_id);
        final Button callButton = (Button) itemLayout.findViewById(R.id.call_button);
        Button messageButton = (Button) itemLayout.findViewById(R.id.message_button);
        Button deleteButton = (Button) itemLayout.findViewById(R.id.delete_button);
        TextView phoneNumberTextView = (TextView) itemLayout.findViewById(R.id.phone_number);
        ImageView imageView = (ImageView) itemLayout.findViewById(R.id.member_image);
        // ====================================================================================

        // 3. 리스너 등록한다
        // ====================================================================================
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phone phone = new Phone();
                phone.call(context, memberList.get(pos).phone_number);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });


        // ====================================================================================

        // 4. 레이아웃 갱신한다.
        // ====================================================================================
        memberIdTextView.setText(String.valueOf(position + 1));
        memberNameTextView.setText(memberList.get(position).name);
        if (phoneNumberTextView != null) {
            phoneNumberTextView.setText(memberList.get(position).phone_number);
        }
        if (imageView != null) {
            Uri imageUri = memberList.get(position).imageUri;
            if (imageUri != null) {
                imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(context, imageUri));
            }
        }

        return itemLayout;
    }

    public void add(int index, Member member) {
        memberList.add(index, member);
        notifyDataSetChanged();
    }

    public void delete(int index) {
        memberList.remove(index);
        notifyDataSetChanged();
    }

    public void clear() {
        memberList.clear();
        notifyDataSetChanged();
    }

    public void set(int index, Member member) {
        memberList.set(index, member);
        notifyDataSetChanged();
    }
}


