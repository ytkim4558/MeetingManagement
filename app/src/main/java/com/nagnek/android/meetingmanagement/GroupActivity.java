package com.nagnek.android.meetingmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class GroupActivity extends Activity {

    static final int PICK_CONTACT_REQUEST = 1;
    private TextView memberNameView = null;
    private String number = null;
    private String groupName = null;
    private String groupNameKey = "GROUP1_NAME";
    private TextView groupNameText = null;
    private int REQ_CODE_SELECT_IMAGE = 100;
    private String KEY_CROPPED_RECT = "cropped-rect";
    String imagePath = null;
    ImageView imageView;
    Bitmap bitmap;
    Uri imageUri;
    ArrayList<Member> memberList = null;
    MemberListAdapter memberListAdapter = null;
    ListView memberListView = null;
    private String strPhotoName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        memberList = new ArrayList<Member>();
        // 어댑터에서 사용할 데이터 설정
        for(int i=0; i<4; ++i) {
            Member member = new Member();
            member.name = "홍길동" + "(" + i + ")";
            member.phone_number = "010-7416-2566";
            memberList.add(member);
        }

        // 어댑터를 생성하고 데이터 설정
        memberListAdapter = new MemberListAdapter(this, memberList);

        // 리스트뷰에 어댑터 설정
        memberListView = (ListView) findViewById(R.id.member_list_view);
        memberListView.setAdapter(memberListAdapter);




        Dlog.i("onCreate()");

        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        Button addMemberButton = (Button) findViewById(R.id.add_member_button);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });
        memberNameView = (TextView) findViewById(R.id.member_name);
        Button callButton = (Button) findViewById(R.id.call_button);
/*        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });*/
        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getExtras().getString(groupNameKey);
        }
      /*  groupNameText = (TextView) findViewById(R.id.group_name_text_view);
        groupNameText.setText(groupName);*/

        imageView = (ImageView) findViewById(R.id.groupImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                try {
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
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
                number = cursor.getString(column);

                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameIndex);

                // 선택한 연락처의 이름을 TextView에 보여준다.
                //memberNameView.setText(name);
            }

        } else if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                // result


                Bitmap image_bitmap = null;
                try {
                    imageUri = data.getData();
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    image_bitmap = NagneRoundedImage.getCircleBitmap(image_bitmap);
                    imageView.setImageBitmap(image_bitmap);
                    //image_bitmap = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //배치해놓은 ImageView에 set


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Dlog.i("onStart()");

        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Dlog.i("onRestoreInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 만일 onRestoreInstanceState 함수의 번들 매개 변수가 널이 아니면
        // 해당 액티비티에서 백업된 데이터가 존재하는 것을 의미한다
        // 따라서 번들에 백업된 데이터를 불러서 사용자 이름 및 전화번호를 복원한다.
        if (savedInstanceState != null) {
            /*Dlog.i("RestoreName");
            Dlog.i(savedInstanceState.getString("BACKUP_NAME"));
            Dlog.i(savedInstanceState.getString("BACKUP_NUMBER"));
            memberNameView.setText(savedInstanceState.getString("BACKUP_NAME"));
            number = savedInstanceState.getString("BACKUP_NUMBER");
            groupName = savedInstanceState.getString(groupNameKey);
            imageUri = savedInstanceState.getParcelable("BACKUP_IMAGE");

           if(imageUri != null) {
               Bitmap bitmap = null;
               try {
                   bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                   bitmap = NagneRoundedImage.getCircleBitmap(bitmap);
                   imageView.setImageBitmap(bitmap);
                   bitmap = null;
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }*/

        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dlog.i("onResume()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Dlog.i("onRestart()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Dlog.i("onPause()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Dlog.i("onStop()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.i("onDestroy()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        /*if(bitmap != null) {
            bitmap.recycle();
        }*/
        imageUri = null;
        imageView = null;
        number = null;
        groupName = null;
    }

    // 액티비티 데이터를 백업할 수 있는 함수
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Dlog.i("onSaveInstanceState()");
        if(Dlog.showToast)Toast.makeText(this, Dlog.s(""), Toast.LENGTH_SHORT).show();
        // 이름과 전화번호를 onSavedInstanceState 매개 변수인 번들에 저장한다.
        /*if (memberNameView != null) {
            String backupName = memberNameView.getText().toString();
            outState.putString("BACKUP_NAME", backupName);
            outState.putString(groupNameKey, groupName);
            Dlog.i("backupName");
        }
        if (number != null) {
            outState.putString("BACKUP_NUMBER", number);
            Dlog.i("backupNumber");
        }
        if(imageUri!=null) {
            outState.putParcelable("BACKUP_IMAGE", imageUri);
            Dlog.i("저장");
        }*/

        super.onSaveInstanceState(outState);
    }

    public class MemberListAdapter extends BaseAdapter {
        Context context = null;
        ArrayList<Member> memberList = null;
        LayoutInflater layoutInflater = null;

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
        public View getView(int position, View convertView, ViewGroup parent) {
            // 1. 리스트의 한 항목에 해당하는 레이아웃을 생성한다
            // ====================================================================================
            View itemLayout = layoutInflater.inflate(R.layout.member_list_view_item_layout, null);
            TextView memberNameTextView = (TextView) itemLayout.findViewById(R.id.member_name);
            TextView memberIdTextView = (TextView) itemLayout.findViewById(R.id.member_id);
            // ====================================================================================

            // 2. 이름 참조하여 레이아웃을 갱신한다
            // ====================================================================================
            memberNameTextView.setText(memberList.get(position).name);

            // ====================================================================================

            System.out.println("hi");
            return itemLayout;
        }
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
    private void call() {
        if (number != null) {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + number)));
        }
    }
}
