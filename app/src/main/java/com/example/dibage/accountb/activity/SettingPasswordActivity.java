package com.example.dibage.accountb.activity;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.EncryUtils;
import com.example.dibage.accountb.utils.SPUtils;
import com.example.dibage.accountb.utils.SimpleUtils;


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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("设置保护密码");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingPasswordActivity.this.finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassword()) {
                    String pwd_encrypt =  EncryUtils.getInstance().encryptString(et_pwd.getText().toString().trim(), SimpleUtils.DEFAULT_KEY);
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
