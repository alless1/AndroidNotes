package com.alless.roomdemo.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.alless.roomdemo.entity.StudentEntity;

import java.util.List;

/**
 * Created by ${程杰} on 2018/3/29.
 * 描述:方法参数可以为集合，返回值也可以为集合
 */
@Dao
public interface IStudentDao extends BaseDao<StudentEntity> {
    /**
     * 如果不指定onConflict,插入重复主键对象会carsh
     *可以根据主键（多个主键一致）或者索引来判断OnConflictStrategy的条件
     *OnConflictStrategy.IGNORE 无反应
     *OnConflictStrategy.REPLACE 更新原来的
     * @param entity 可以插入单个对象，也可以插入对象集合
     * @return 可以为void，long返回插入的行Id，如果参数是集合,也会返回long[] 或者long{}
     */
/*    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addStudent(StudentEntity entity);*/

    /**
     * 根据主键查找对象删除
     * @param entity 也可以为集合
     * @return 删除的行号
     */
/*    @Delete
    int deleteStudent(StudentEntity entity);*/


    /**
     * 根据主键查找对象更新
     * @return 返回更新的行号
     */
/*    @Update
    int updateStudent(StudentEntity entity);*/



    //查询所有
    @Query("select * from student_info")
    List<StudentEntity> getStudentAll();

    //按条件查询
    @Query("select * from student_info where name = :name")
    StudentEntity getStudentByName(String name);

}
