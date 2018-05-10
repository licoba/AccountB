package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.SPUtils;
import com.gcssloop.encrypt.symmetric.AESUtil;

import javax.crypto.Cipher;

import es.dmoral.toasty.Toasty;

public class SettingPasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText et_pwd;
    private EditText et_pwd_again;
    private Button btn_submit;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        context = getApplicationContext();
        toolbar = findViewById(R.id.toolbar);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwd_again = findViewById(R.id.et_pwd_again);
        btn_submit = findViewById(R.id.btn_submit);

        //替代ActionBar
        setSupportActionBar(toolbar);
        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键为可点击状态
        getSupportActionBar().setHomeButtonEnabled(true);
        //隐藏自带AppTitle
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("设置保护密码");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v.getId() == android.R.id.home)
                //Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
                SettingPasswordActivity.this.finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassword()) {

                    String key = "dibage";//密钥，进行解密和加密
                    String pwd_encrypt = AESUtil.aes(et_pwd.getText().toString().trim(),key, Cipher.ENCRYPT_MODE);
                    SPUtils.put(context,"pwd_encrypt",pwd_encrypt);
                    SPUtils.put(context,"is_setting_pwd",true);
                    Toasty.success(context, "设置成功").show();
                    setResult(RESULT_OK);
                    SettingPasswordActivity.this.finish();

                }
            }

        });



    }//end Oncreate

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
