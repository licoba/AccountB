package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.RecordAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.entitys.Record;
import com.example.dibage.accountb.utils.EncryUtils;
import com.example.dibage.accountb.utils.FileUtils;
import com.example.dibage.accountb.utils.SPUtils;
import com.example.dibage.accountb.utils.SimpleUtils;
import com.example.dibage.accountb.utils.UIUtils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/*
备份文件恢复
 */
public class RecorveyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView swipeMenuRecyclerView;//recycleview
    Context context;
    String TAG = "RecorveyActivity";
    List<Record> recordList = new ArrayList<>();
    private RecordAdapter mAdapter;
    LinearLayout ll_empty;
    private View.OnClickListener mLayoutListener;
    String op_filepath = "";//定义全局，表示操作文件的路径
    private AlertDialog dialog;
    private AlertDialog dialog_pwd;
    private AlertDialog dialog_rename;
    private EditText et_pwd;
    DaoSession daoSession;
    AccountDao mAccountDao;
    private String content;
    private EditText et_filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorvey);
        initFBI();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mLayoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.layout1://覆盖导入
                        dialog.dismiss();
                        coverImport();
                        break;
                    case R.id.layout2://合并导入
                        mergeImport();
                        dialog.dismiss();
                        break;
                    case R.id.layout3://发送该文件
                        dialog.dismiss();
                        FileUtils.shareFile(context,new File(op_filepath));
                        break;
                    case R.id.layout4://重命名文件
                        dialog.dismiss();
                        renameFile();
                        break;
                }
            }
        };
    }

    //重命名文件
    private void renameFile() {
        showRenameDialog();
    }

    //重命名文件的dialog
    private void showRenameDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_rename, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog_rename = builder.create();
        dialog_rename.show();
        // 将对话框的大小按屏幕大小的百分比设置
        dialog_rename.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog_rename.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.75); //设置宽度
        dialog_rename.getWindow().setAttributes(lp);

        et_filename = view.findViewById(R.id.et_filename);
        et_filename.setText(FileUtils.getFileName(op_filepath));
        et_filename.setSelection(et_filename.getText().length());
        InputMethodManager inputManager = (InputMethodManager) et_filename
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_filename, 0);
        //必须要延迟启动软键盘，如果布局还未绘制完成，则showSoftInput()方法不起作用。
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    et_filename.requestFocus();
                    imm.showSoftInput(et_filename, 0);
                }
            }
        }, 150);

        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_rename.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_filename.getText().toString().isEmpty()){
                    Toasty.warning(context,"请输入文件名").show();
                }else {
                    FileUtils.renameFile(op_filepath,et_filename.getText().toString().trim());
                    dialog_rename.dismiss();
                    Toasty.success(context,"命名成功").show();
                    initData();
                    mAdapter.notifyDataSetChanged();
                    updateUI();
                }
            }
        });
    }

    //合并导入
    private void mergeImport() {
        showPwdDialog("merge");
    }

    //覆盖导入文件
    private void coverImport() {
        showPwdDialog("cover");
    }

    //输入保护密码的dialog
    private void showPwdDialog(final String opration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_pwd, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog_pwd = builder.create();
        dialog_pwd.show();
        // 将对话框的大小按屏幕大小的百分比设置
        dialog_pwd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog_pwd.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.75); //设置宽度
        dialog_pwd.getWindow().setAttributes(lp);

        et_pwd = view.findViewById(R.id.et_pwd);
        InputMethodManager inputManager = (InputMethodManager) et_pwd
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_pwd, 0);
        //必须要延迟启动软键盘，如果布局还未绘制完成，则showSoftInput()方法不起作用。
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    et_pwd.requestFocus();
                    imm.showSoftInput(et_pwd, 0);
                }
            }
        }, 150);

        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        TextView tv_warning = view.findViewById(R.id.tv_warning);
        if(opration.equals("merge")) {
            tv_warning.setVisibility(View.GONE);
        } else if(opration.equals("cover")) {
            tv_warning.setVisibility(View.VISIBLE);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_pwd.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chePwd(opration);
            }
        });
    }


    //判断输入的密码是否正确
    private void chePwd(String opration) {
        if (et_pwd.getText().toString().trim().equals("")) {
            Toasty.warning(context, "请先输入密码").show();
            return;
        }
        String key = et_pwd.getText().toString().trim();
//        String jiemi = AESUtil.aes(content, key, Cipher.DECRYPT_MODE);
        String jiemi = EncryUtils.getInstance().decryptString(content,key);
        if (jiemi == null) {
            Toasty.warning(context, "不是这个密码，再想想？").show();
            et_pwd.setText("");
        } else {
            if (opration.equals("cover")) {
                fugai(jiemi);
            } else if (opration.equals("merge")) {
                hebing(jiemi);
            }
            Toasty.success(context, "恢复成功").show();
            setResult(RESULT_OK);
            finish();
        }
    }

    //进行合并导入：如果账号存在不同则添加进去
    private void hebing(String jiemi) {
        List<Account> queryList = new ArrayList<>();
        List<Account> accountList = new ArrayList<>();
        Gson gson = new Gson();
        accountList = gson.fromJson(jiemi, new TypeToken<List<Account>>() {
        }.getType());
        daoSession = ((MyApplication) getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
        QueryBuilder<Account> qb = mAccountDao.queryBuilder();
        queryList.clear();
        queryList.addAll(qb.list());
        //不能直接mAccountDao.insert(account);因为accountID相同，会造成冲突
        for (int i = 0; i < accountList.size(); i++) {
            if (!existAccount(accountList.get(i), queryList)) {
                Account insertAccount = new Account();
                Account account = accountList.get(i);
                insertAccount.setUsername(account.getUsername());
                insertAccount.setDescription(account.getDescription());
                insertAccount.setFirstchar(account.getFirstchar());
                insertAccount.setRemark(account.getRemark());
                insertAccount.setPassword(account.getPassword());
                mAccountDao.insert(insertAccount);
            }
        }

    }

    public boolean existAccount(Account account, List<Account> listA) {
        for (int i = 0; i < listA.size(); i++) {
            if (account.getUsername().equals(listA.get(i).getUsername())
                    && account.getDescription().equals(listA.get(i).getDescription())
                    && account.getRemark().equals(listA.get(i).getRemark())
                    && account.getPassword().equals(listA.get(i).getPassword())
                    ) {
                return true;
            }

        }
        return false;
    }

    //进行覆盖导入：删除当前所有账号数据，然后进行大导入
    private void fugai(String jiemi) {
        List<Account> accountList = new ArrayList<>();
        Gson gson = new Gson();
        accountList = gson.fromJson(jiemi, new TypeToken<List<Account>>() {
        }.getType());
        daoSession = ((MyApplication) getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
        mAccountDao.deleteAll();//删除所有数据
        for (Account account : accountList) {
            mAccountDao.insert(account);
        }
    }

    //显示类似popwindow的dialog
    private void showDialog() {
        content = FileUtils.readString(op_filepath, "utf-8");//显示dialog的时候就获取文件内容
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_record, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        // 将对话框的大小按屏幕大小的百分比设置
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.75); //设置宽度
        dialog.getWindow().setAttributes(lp);

        LinearLayout ll1 = view.findViewById(R.id.layout1);
        LinearLayout ll2 = view.findViewById(R.id.layout2);
        LinearLayout ll3 = view.findViewById(R.id.layout3);
        LinearLayout ll4 = view.findViewById(R.id.layout4);
        ll1.setOnClickListener(mLayoutListener);
        ll2.setOnClickListener(mLayoutListener);
        ll3.setOnClickListener(mLayoutListener);
        ll4.setOnClickListener(mLayoutListener);

    }

    private void updateUI() {
        if (recordList == null || recordList.size() == 0) {
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_empty.setVisibility(View.GONE);
        }
    }

    private void initData() {
        context = this;
        recordList.clear();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/账号册子/";
        List<String> backups_paths = FileUtils.getDataByType(path, ".bkp");
        for (int i = 0; i < backups_paths.size(); i++) {
            File file = new File(backups_paths.get(i));
            String s = FileUtils.readString(backups_paths.get(i), "utf-8");
            String pwd = (String) SPUtils.get(context, "pwd_encrypt", "diabge");
            String key = EncryUtils.getInstance().encryptString(pwd,SimpleUtils.DEFAULT_KEY);
//            String key = AESUtil.aes(pwd, SimpleUtils.DEFAULT_KEY, Cipher.DECRYPT_MODE);
            String jiemi =  EncryUtils.getInstance().decryptString(s, key);
//            String jiemi = AESUtil.aes(s, key, Cipher.DECRYPT_MODE);
            List<Account> accountsList = new ArrayList<>();
            Gson gson = new Gson();
            accountsList = gson.fromJson(jiemi, new TypeToken<List<Account>>() {
            }.getType());
            Record record = new Record();

            record.setLocation(file.getAbsolutePath());
            record.setName(file.getName());
            record.setNumber(accountsList.size());
            recordList.add(record);

//            Log.e(TAG, "一个备份文件中所含数据的条数：" + accountsList.size());

        }
    }

    private void initFBI() {
        toolbar = findViewById(R.id.toolbar);
        swipeMenuRecyclerView = findViewById(R.id.swipeMenuRecyclerView);
        ll_empty = findViewById(R.id.ll_empty);
    }

    private void initView() {
        UIUtils.setToolbar(RecorveyActivity.this, toolbar, "备份文件恢复");
        mAdapter = new RecordAdapter(R.layout.item_record, recordList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        mSwipeMenuCreator = new SwipeMenuCreator() {
//            @Override
//            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
//                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
//                deleteItem.setWidth(200);
//                deleteItem.setText("删除");
//                deleteItem.setTextSize(16);
//                deleteItem.setTextColor(getResources().getColor(R.color.WhiteText));
//                deleteItem.setHeight(MATCH_PARENT);
//                deleteItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                swipeRightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
//            }
//        };
//
//        mSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
//            @Override
//            public void onItemClick(SwipeMenuBridge menuBridge) {
//                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。03.85/*-+2
//                menuBridge.closeMenu();
//                int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
//                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
//                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
//                if (FileUtils.deleteFile(recordList.get(adapterPosition).getLocation())) {
//                    recordList.remove(adapterPosition);
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    Toasty.info(context, "删除失败").show();
//                }
//                updateUI();
//
//            }
//        };
//
//        //revyvleView单个点击监听
//        swipeMenuRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
//            @Override
//            public void onItemClick(View itemView, int position) {
//                op_filepath = recordList.get(position).getLocation();
//                showDialog();
//            }
//        });

        swipeMenuRecyclerView.setLayoutManager(layoutManager);
        swipeMenuRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        swipeMenuRecyclerView.setAdapter(mAdapter);
        updateUI();
    }
}
