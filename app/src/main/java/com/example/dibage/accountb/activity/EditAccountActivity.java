package com.example.dibage.accountb.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.utils.AccountUtils;
import com.example.dibage.accountb.utils.SimpleUtils;

import es.dmoral.toasty.Toasty;

public class EditAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btn_clear1;
    private ImageButton btn_clear2;
    private ImageButton btn_clear3;
    private ImageButton btn_clear4;

    EditText et_description;
    EditText et_username;
    EditText et_password;
    EditText et_remarks;
    Button btn_Submit;
    Button btn_getRandom;
    ListView listView;

    Toolbar toolbar;
    DaoSession daoSession ;
    AccountDao mAccountDao;
    private PopupWindow mPopupWindow;

    int length = 12;
    boolean big = true;
    boolean small = true;
    boolean special = false;
    private TextView tv_pwd_random;
    private Button btn_refresh;
    private TextView tv_length;
    private SeekBar seekBar;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private Button btn_cancel;
    private Button btn_copy;
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        initData();
        initView();
        initEvent();

    }

    private void initData() {
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
        account = (Account) getIntent().getSerializableExtra("account_data");
    }

    private void initEvent() {
        btn_clear1.setOnClickListener(this);
        btn_clear2.setOnClickListener(this);
        btn_clear3.setOnClickListener(this);
        btn_clear4.setOnClickListener(this);
        btn_Submit.setOnClickListener(clickListener);
        btn_getRandom.setOnClickListener(this);


    }

    private void initView() {
        et_description = findViewById(R.id.etDescription);
        et_password = findViewById(R.id.etPassword);
        et_username = findViewById(R.id.etUsername);
        et_remarks = findViewById(R.id.etRemark);
        btn_Submit = findViewById(R.id.btnSubmit);
        btn_clear1 = findViewById(R.id.btn_clear1);
        btn_clear2 = findViewById(R.id.btn_clear2);
        btn_clear3 = findViewById(R.id.btn_clear3);
        btn_clear4 = findViewById(R.id.btn_clear4);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listview);
        btn_getRandom = findViewById(R.id.btn_getRandom);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("编辑账号");

        et_description.setText(account.getDescription());
        et_username.setText(account.getUsername());
        et_password.setText(account.getPassword());
        et_remarks.setText(account.getRemark());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAccountActivity.this.finish();
            }
        });
        ;

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        String msg;
        boolean VertifyState = false ;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSubmit:
                    if (SimpleUtils.isNotNull(et_description)){
                        if (SimpleUtils.isNotNull(et_username)){
                            if(SimpleUtils.isNotNull(et_password)) {
                                msg = "保存成功";
                                VertifyState = true;

                                ModifyRecord(et_description,et_username,et_password,et_remarks);
                                EditAccountActivity.this.finish();
                            }
                            else
                                msg="密码不能为空";
                        }else
                            msg = "账号不能为空";
                    }
                    else
                        msg="名称不能为空";
                    if (VertifyState)
                        Toasty.success(EditAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                    else
                        Toasty.warning(EditAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                    break;
                default:
                    Toasty.warning(EditAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();

            }
        }
    };

    //修改记录
    private void ModifyRecord(EditText et_description, EditText et_username, EditText et_password, EditText et_remarks) {

        String description = SimpleUtils.getStrings(et_description);
        String username = SimpleUtils.getStrings(et_username);
        String password = SimpleUtils.getStrings(et_password);
        String remarks = SimpleUtils.getStrings(et_remarks);
        String firstChar = AccountUtils.getFirstString(description);
        Account account1 = new Account(account.getId(),description,username,password,remarks,firstChar);
        mAccountDao.update(account1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_clear1:
                et_description.setText("");
                break;
            case R.id.btn_clear2:
                et_username.setText("");
                break;
            case R.id.btn_clear3:
                et_password.setText("");
                break;
            case R.id.btn_clear4:
                et_remarks.setText("");
                break;
            case R.id.btn_getRandom:
                showPopRandom();
                break;
        }
    }

    private void showPopRandom() {
        mPopupWindow = new PopupWindow();
        LayoutInflater inflater = getLayoutInflater();
        View contentView = LayoutInflater.from(EditAccountActivity.this).inflate(R.layout.pop_random, null);
        View rootview = LayoutInflater.from(EditAccountActivity.this). inflate(R.layout.activity_add_account, null);
        mPopupWindow = new PopupWindow(contentView,
                getWindowManager().getDefaultDisplay().getWidth() - 200, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);

        tv_pwd_random = contentView.findViewById(R.id.tv_pwd_random);
        tv_length = contentView.findViewById(R.id.tv_length);
        seekBar = contentView.findViewById(R.id.seekBar);
        checkBox1 = contentView.findViewById(R.id.checkBox1);
        checkBox2 = contentView.findViewById(R.id.checkBox2);
        checkBox3 = contentView.findViewById(R.id.checkBox3);
        btn_cancel = contentView.findViewById(R.id.btn_cancel);
        btn_refresh =contentView.findViewById(R.id.btn_refresh);
        btn_copy = contentView.findViewById(R.id.btn_copy);

        initPopEvent();

    }

    private void initPopEvent() {
        tv_length.setText("["+length+"]");
        seekBar.setProgress(length-4);
        checkBox1.setChecked(big);
        checkBox2.setChecked(small);
        checkBox3.setChecked(special);

        refresh();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getApplicationContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tv_pwd_random.getText());
                mPopupWindow.dismiss();
                Toasty.success(EditAccountActivity.this, "已复制："+tv_pwd_random.getText(), Toast.LENGTH_SHORT, false).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                length = progress+4;
                tv_length.setText("["+length+"]");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refresh();
            }
        });


        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    big = true;
                    refresh();
                }else{
                    big = false;
                    refresh();
                }
            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    small = true;
                    refresh();
                }else{
                    small = false;
                    refresh();
                }
            }
        });
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    special = true;
                    refresh();
                }else{
                    special = false;
                    refresh();
                }
            }
        });
    }

    private void refresh() {
        tv_pwd_random.setText(SimpleUtils.getRandomPwd(length,big,small,special));
    }

}
