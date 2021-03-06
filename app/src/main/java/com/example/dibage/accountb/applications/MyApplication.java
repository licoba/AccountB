package com.example.dibage.accountb.applications;

import android.app.Application;
import android.content.Context;

import com.example.dibage.accountb.dao.DaoMaster;
import com.example.dibage.accountb.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by dibage on 2018/3/27.
 */

//初始化数据库和greenDAO核心类

public class MyApplication extends Application {
    private DaoSession daoSession;

    private static  MyApplication instance;
    private static Context context;


    public static Context getContext() {
        return context;
    }

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context= getApplicationContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "account-db");
        Database db =  helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
