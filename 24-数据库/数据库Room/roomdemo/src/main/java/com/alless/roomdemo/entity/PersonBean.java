package com.alless.roomdemo.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ${程杰} on 2018/3/28.
 * 描述:
 * 索引 indices = {@Index(value = "name",unique = true)}
 */
@Entity(tableName = "person",indices = {@Index(value = "name",unique = true)})
public class PersonBean {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int age;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Ignore
    public PersonBean(String name,int age){
        this.name = name;
        this.age = age;
    }
    public PersonBean(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "PersonBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
