package com.alless.roomdemo.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alless.roomdemo.dao.IStudentDao;
import com.alless.roomdemo.entity.PersonBean;
import com.alless.roomdemo.dao.IPersonDao;
import com.alless.roomdemo.entity.StudentEntity;

/**
 * Created by ${程杰} on 2018/3/28.
 * 描述:数据库管理类单例
 */

@Database(entities = {PersonBean.class, StudentEntity.class},version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sAppDatabase;
    public static AppDatabase getAppDatabase(Context context){
        if(sAppDatabase==null){
            synchronized (AppDatabase.class){
                if(sAppDatabase == null){
                    sAppDatabase = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"user.db")
                            .allowMainThreadQueries()//允许在主线程操作
                           // .addMigrations(MIGRATION_1_2,MIGRATION_2_3)//数据库升级迁移数据
                            .fallbackToDestructiveMigration()//数据库更新时清除数据；
                            .build();
                }
            }
        }
        return sAppDatabase;
    }

    public static void onDestroy(){
        sAppDatabase = null;
    }

    public abstract IPersonDao getPersonDao();

    public abstract IStudentDao getStudentDao();

    //数据库版本升级
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table person add column phone text");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'student_info'('name' TEXT,'age' INTEGER not null,'serialNum' TEXT not null,PRIMARY KEY ('serialNum'))");
        }
    };

}
