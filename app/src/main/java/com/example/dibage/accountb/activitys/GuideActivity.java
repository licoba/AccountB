package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.EncryUtils;
import com.example.dibage.accountb.utils.SPUtils;
import com.example.dibage.accountb.utils.SimpleUtils;


import es.dmoral.toasty.Toasty;

/**
 * 用来引导用户设置保护密码
 */
public class GuideActivity extends AppCompatActivity {
    private Button btn_ignore;
    private Button btn_submit;
    private Context context;
    private EditText et_pwd;
    private EditText et_pwd_again;
    private Toolbar toolbar;
    private Boolean already_guide = false;
    private Boolean is_setting_pwd = false;
    private LinearLayout ll_guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initFBI();
        initData();
        initView();
        initEvent();



    }//end oncreate

    private void initEvent() {
        btn_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.put(context,"already_guide",true);
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
            }

        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassword()) {
                    Toasty.success(context, "设置成功").show();
//                    String pwd_encrypt = AESUtil.aes(et_pwd.getText().toString().trim(),key, Cipher.ENCRYPT_MODE);
                    String pwd_encrypt = EncryUtils.getInstance().encryptString(et_pwd.getText().toString().trim(), SimpleUtils.DEFAULT_KEY);
                    SPUtils.put(context,"already_guide",true);
                    SPUtils.put(context,"pwd_encrypt",pwd_encrypt);
                    SPUtils.put(context,"is_setting_pwd",true);
                    Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                    startActivity(intent);
                    GuideActivity.this.finish();
                }
            }

        });
    }

    private void initView() {
        if (!already_guide) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("设置保护密码");
        }else if(already_guide&&(!is_setting_pwd)){
           // ll_guide.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Intent intent = new Intent(GuideActivity.this,MainActivity.class);
            startActivity(intent);
            GuideActivity.this.finish();
        }else if(already_guide&&(is_setting_pwd)){//如果设置过安全密码
            Intent intent = new Intent(GuideActivity.this,ValidateActivity.class);
            startActivity(intent);
            GuideActivity.this.finish();
        }
    }

    private void initData() {
        already_guide = (Boolean) SPUtils.get(context,"already_guide",false);
        is_setting_pwd = (Boolean) SPUtils.get(context,"is_setting_pwd",false);
    }

    private void initFBI() {
        ll_guide = findViewById(R.id.ll_guide);
        context = getApplicationContext();
        toolbar = findViewById(R.id.toolbar);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwd_again = findViewById(R.id.et_pwd_again);
        btn_ignore = findViewById(R.id.btn_ignore);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private boolean checkPassword() {
        String et1 = et_pwd.getText().toString().trim();
        String et2 = et_pwd_again.getText().toString().trim();
        if (et1.isEmpty()) {
            Toasty.warning(context, "请输入新的密码").show();
            return false;
        } else if (et2.isEmpty()) {
            Toasty.warning(context, "请输入二次确认密码").show();
            return false;
        } else if (et1.length() < 4 || et1.length() > 8) {
            Toasty.warning(context, "新密码长度不对，请重新输入").show();
            return false;
        } else if (!et1.equals(et2)) {
            Toasty.warning(context, "密码不一致，请重新输入").show();
            return false;
        } else {
            //Toasty.success(context, "验证成功").show();
            return true;
        }

    }
}
