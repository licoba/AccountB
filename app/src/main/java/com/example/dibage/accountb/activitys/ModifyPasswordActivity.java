package com.example.dibage.accountb.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dibage.accountb.R;

public class ModifyPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        toolbar = findViewById(R.id.toolbar);
        //替代ActionBar
        setSupportActionBar(toolbar);
        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键为可点击状态
        getSupportActionBar().setHomeButtonEnabled(true);
        //隐藏自带AppTitle
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("修改保护密码");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v.getId() == android.R.id.home)
                //Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
                ModifyPasswordActivity.this.finish();
            }
        });
    }
}
