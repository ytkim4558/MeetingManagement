package com.nagnek.android.meetingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yongtakpc on 2016. 7. 30..
 */
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView1 = (TextView) findViewById(R.id.textView1);
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        final TextView textView4 = (TextView) findViewById(R.id.textView4);
        final TextView textView5 = (TextView) findViewById(R.id.textView5);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, GroupListActivity.class));
                finish();
            }
        });

        textView1.animate().translationX(-30).translationY(-30).withEndAction(new Runnable() {
            @Override
            public void run() {
                textView1.animate().translationX(0).translationY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        textView2.animate().translationX(-20).translationY(-30).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                textView2.animate().translationX(0).translationY(-0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView3.animate().translationX(10).translationY(-30).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView3.animate().translationX(0).translationY(0).withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        textView4.animate().translationX(10).translationY(-30).withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                textView4.animate().translationX(0).translationY(0).withEndAction(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        textView5.animate().translationX(30).translationY(-30).withEndAction(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                textView5.animate().translationX(0).translationY(0).withEndAction(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        //Handler hd = new Handler();
                                                                                        //hd.postDelayed(new splashhandler(), 1000);
                                                                                        startActivity(new Intent(HomeActivity.this, GroupListActivity.class));
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }
//    private class splashhandler implements Runnable{
//        public void run() {
//            startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
//            HomeActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
//        }
//    }
}
