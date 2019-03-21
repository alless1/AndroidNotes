package com.alless.roomdemo.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alless.roomdemo.entity.PersonBean;

import java.util.List;

/**
 * Created by ${程杰} on 2018/3/28.
 * 描述:
 */
@Dao
public interface IPersonDao {
   @Query("select * from person")
   List<PersonBean> getPersonAll();

   @Query("select * from person where name = :name")
   PersonBean getPersonByName(String name);

   //可以插入单个对象，也可以插入对象集合
   //OnConflictStrategy.FAIL,OnConflictStrategy.ABORT 会抛出异常
   //OnConflictStrategy.IGNORE 无反应
   //OnConflictStrategy.REPLACE 更新原来的
   //可以根据主键（多个主键一直）或者索引来判断的
   @Insert(onConflict = OnConflictStrategy.IGNORE)
   void addPerson(PersonBean bean);

   //根据主键查找 替换元素
   @Update
   void updatePerson(PersonBean bean);

   //是根据主键去删除de
   @Delete
   void deletePerson(PersonBean bean);

}
