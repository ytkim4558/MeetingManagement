package com.nagnek.android.meetingmanagement;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagnek.android.externalIntent.Message;
import com.nagnek.android.externalIntent.Phone;
import com.nagnek.android.nagneImage.NagneCircleImage;

import java.util.ArrayList;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class MemberListAdapter extends BaseAdapter {
    public final static String SHOW_MEMBER_KEY = "com.nagnek.android.meetingmanagement.SHOW_MEMBER";
    static Drawable face;   // 얼굴 이미지
    Context context = null;
    ArrayList<Member> memberList = null;
    LayoutInflater layoutInflater = null;
    private static final String MESSAGE_BODY = "안녕하세요 김용탁입니다.";

    public MemberListAdapter(Context context, ArrayList<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
        this.layoutInflater = LayoutInflater.from(context);
        face = context.getResources().getDrawable(R.drawable.face);
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
            viewHolder.callButton = (Button) itemLayout.findViewById(R.id.call_button);
            viewHolder.messageButton = (Button) itemLayout.findViewById(R.id.message_button);
            viewHolder.deleteButton = (Button) itemLayout.findViewById(R.id.delete_button);
            viewHolder.phoneNumberTextView = (TextView) itemLayout.findViewById(R.id.phone_number);
            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.member_image);
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
                phone.call(context, memberList.get(pos).phone_number);
            }
        });
        viewHolder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.sendSMS(context, memberList.get(position).phone_number, MESSAGE_BODY);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
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
        if (viewHolder.imageView != null) {
            Uri imageUri = memberList.get(position).imageUri;
            if (imageUri != null) {
                viewHolder.imageView.setImageBitmap(NagneCircleImage.getCircleBitmap(context, imageUri));
            } else {
                viewHolder.imageView.setImageDrawable(face);
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

    class ViewHolder {
        TextView memberNameTextView;
        TextView memberIdTextView;
        Button callButton;
        Button messageButton;
        Button deleteButton;
        TextView phoneNumberTextView;
        ImageView imageView;
    }
}


