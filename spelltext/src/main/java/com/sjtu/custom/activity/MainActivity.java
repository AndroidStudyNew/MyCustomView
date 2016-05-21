package com.sjtu.custom.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sjtu.custom.R;
import com.sjtu.custom.view.CustomTextView;
import com.sjtu.custom.view.ScriptCardChineseView;


public class MainActivity extends AppCompatActivity {

    public static String str = "更新(gengxing)后(hou)可能(keneng)启动(qidong)不(bu)了(liao)，这时(zheshi)要在(yaozai)网上(wangshang)寻找(xuzhao)不(bu)了(liao)，这时(zheshi)要在(yaozai)网上(wangshang)寻找(xuzhao)";
    ScriptCardChineseView tv_name_chinese;
    CustomTextView mCTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        str = "更新(gengxing)后(hou)";
        tv_name_chinese = (ScriptCardChineseView) findViewById(R.id.tv_name_chinese);
        tv_name_chinese.setStr(str);
        tv_name_chinese.setTextSizePinyin(18);
        tv_name_chinese.setTextSizeHanzi(22);
        tv_name_chinese.setTextColor(ContextCompat.getColor(this, R.color.clr_09C0CE));
        tv_name_chinese.setVisibility(View.VISIBLE);

        mCTV = (CustomTextView) findViewById(R.id.ctv_2);
        mCTV.seText(str);
        mCTV.setVisibility(View.VISIBLE);
    }
}
