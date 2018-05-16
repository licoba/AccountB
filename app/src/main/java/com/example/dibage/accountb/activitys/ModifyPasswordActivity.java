package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.base.BaseActivity;
import com.example.dibage.accountb.utils.SPUtils;
import com.example.dibage.accountb.utils.SimpleUtils;
import com.gcssloop.encrypt.symmetric.AESUtil;

import javax.crypto.Cipher;

import es.dmoral.toasty.Toasty;

public class ModifyPasswordActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText et_pwd_old;
    private EditText et_pwd_new;
    private EditText et_pwd_again;
    private Button btn_submit;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initFBI() {
        setContentView(R.layout.activity_modify_password);
        toolbar = findViewById(R.id.toolbar);
        et_pwd_old = findViewById(R.id.et_pwd_old);
        et_pwd_new = findViewById(R.id.et_pwd_new);
        et_pwd_again = findViewById(R.id.et_pwd_again);
        btn_submit = findViewById(R.id.btn_submit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("修改保护密码");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPasswordActivity.this.finish();
            }
        });
    }

    @Override
    protected void initEvent() {
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd1 = et_pwd_old.getText().toString().trim();
                    String pwd2 = et_pwd_new.getText().toString().trim();
                    String pwd3 = et_pwd_again.getText().toString().trim();
                    if(pwd1.isEmpty()){
                        Toasty.warning(context,"请输入当前密码").show();
                    }else if(pwd2.isEmpty()){
                        Toasty.warning(context,"请输入新的密码").show();
                    }else if(pwd3.isEmpty()){
                        Toasty.warning(context,"请输入二次确认密码").show();
                    }else{
                        if(!pwd2.equals(pwd3)){
                            Toasty.warning(context,"两个新密码不一致，请检查").show();
                        }else {
                            checkOldpwd(pwd1,pwd2);
                        }
                    }
                }
            });
    }

    //检查旧密码是否正确
    private void checkOldpwd(String password,String newPassword) {
        String pwd = (String) SPUtils.get(context, "pwd_encrypt", "");
        String pwd_from_storage = AESUtil.aes(pwd, SimpleUtils.DEFAULT_KEY, Cipher.DECRYPT_MODE);
        if (password.equals(pwd_from_storage)){
            String pwd_encrypt = AESUtil.aes(newPassword,SimpleUtils.DEFAULT_KEY, Cipher.ENCRYPT_MODE);
            SPUtils.put(context,"pwd_encrypt",pwd_encrypt);
            Toasty.success(context,"修改成功").show();
            this.finish();
        }else{
            Toasty.error(context,"当前密码错误，请重试").show();
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        context = this;
    }
}
