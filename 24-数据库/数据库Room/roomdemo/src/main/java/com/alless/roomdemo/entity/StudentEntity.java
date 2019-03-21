package com.alless.roomdemo.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ${程杰} on 2018/3/29.
 * 描述:
 */

@Entity(tableName = "student_info")
public class StudentEntity {
    @NonNull
    @PrimaryKey
    public String serialNum;

    public String name;
    public int age;

    public StudentEntity(){}

    @Ignore
    public StudentEntity(String name, int age, String serialNum){
        this.name = name;
        this.age = age;
        this.serialNum = serialNum;
    }

    @Override
    public String toString() {
        return "StudentEntity{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", serialNum='" + serialNum + '\'' +
                '}';
    }
}
