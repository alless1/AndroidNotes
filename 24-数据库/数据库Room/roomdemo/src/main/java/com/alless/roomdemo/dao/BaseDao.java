package com.alless.roomdemo.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

/**
 * Created by ${程杰} on 2018/3/29.
 * 描述:
 */
@Dao
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long add(T t);

    @Delete
    int delete(T t);

    @Update
    int update(T t);

}
