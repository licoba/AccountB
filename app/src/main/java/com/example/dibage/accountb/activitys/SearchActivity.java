package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.AccountAdapter;
import com.example.dibage.accountb.adapters.ChangeColorAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.utils.AccountUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class SearchActivity extends AppCompatActivity {
    private Context context;
    List<Account> accountsList = new ArrayList<>();
    private Button btn_cancel;
    private ListView lv_result;
    private TextView tv_tip;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = getApplicationContext();

        btn_cancel = findViewById(R.id.btn_cancel);
        lv_result = findViewById(R.id.lv_result);
        tv_tip = findViewById(R.id.tv_tip);
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new mTextWatcher());
//        et_search.getFocusable(true);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.finish();
            }
        });


    }


    class mTextWatcher implements TextWatcher {

        String s;

        /**
         * CharSequence s：文本改变之前的内容
         * int start：文本开始改变时的起点位置，从0开始计算
         * int count：要被改变的文本字数，即将要被替代的选中文本字数
         * int after：改变后添加的文本字数，即替代选中文本后的文本字数
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //禁止输入空格
            if (s.toString().contains(" ")) {
                String[] str = s.toString().split(" ");
                String str1 = "";
                for (int i = 0; i < str.length; i++) {
                    str1 += str[i];
                }
                et_search.setText(str1);
                et_search.setSelection(start);
            }
        }

        //文本变化之后要做的事：
        // 得到文本输入框中的内容
        // 先从数据库查数据，然后更新到ListView
        //把ListView中所有s字符变色
        @Override
        public void afterTextChanged(Editable editable) {




            s = editable.toString().trim();
            Log.e("SA输入字符：", s);
            if (!s.equals("")) {
                //获取dao实例
                DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                AccountDao mAccountDao = daoSession.getAccountDao();
                //获取queryBuilder，通过queryBuilder来实现查询功能
                QueryBuilder<Account> qb = mAccountDao.queryBuilder();
                WhereCondition whereCondition1 = AccountDao.Properties.Description.like('%' + s + '%');
                WhereCondition whereCondition2 = AccountDao.Properties.Username.like('%' + s + '%');
                qb.whereOr(whereCondition1, whereCondition2);
                qb.orderAsc(AccountDao.Properties.Firstchar, AccountDao.Properties.Username);

                accountsList = qb.list();
                accountsList = AccountUtils.orderListAccount(accountsList);
                Log.e("SA结果:", accountsList.toString());
                Log.e("SA结果总数:", accountsList.size() + "");

                //填充到ListView
                tv_tip.setVisibility(View.GONE);
                if(accountsList.size()==0){
                    tv_tip.setText("抱歉，无匹配记录");
                    tv_tip.setVisibility(View.VISIBLE);
                }


            }else{
                    accountsList.clear();
                    tv_tip.setText("请输入关键字");
                    tv_tip.setVisibility(View.VISIBLE);
            }
            ChangeColorAdapter accountAdapter = new ChangeColorAdapter(context, R.layout.item_listview, accountsList);
            accountAdapter.setsearchString(s);
            lv_result.setAdapter(accountAdapter);
        }
    }
}
