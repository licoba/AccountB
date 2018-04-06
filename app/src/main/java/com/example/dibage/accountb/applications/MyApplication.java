package com.example.dibage.accountb.applications;

import android.app.Application;

import com.example.dibage.accountb.dao.DaoMaster;
import com.example.dibage.accountb.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by dibage on 2018/3/27.
 */

//初始化数据库和greenDAO核心类

public class MyApplication extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "account-db");
        Database db =  helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
