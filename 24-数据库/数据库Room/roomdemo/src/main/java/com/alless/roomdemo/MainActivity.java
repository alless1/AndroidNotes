package com.alless.roomdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alless.roomdemo.dao.IStudentDao;
import com.alless.roomdemo.entity.PersonBean;
import com.alless.roomdemo.dao.IPersonDao;
import com.alless.roomdemo.db.AppDatabase;
import com.alless.roomdemo.entity.StudentEntity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTv_name;
    private EditText mTv_age;
    private Button mAdd;
    private Button mDelete;
    private TextView mTv_show;
    private Button set;
    private Button query;
    private IPersonDao mPersonDao;
    private PersonBean mPersonByName;
    private EditText mTvNum;
    private IStudentDao mStudentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv_name = (EditText) findViewById(R.id.tv_name);
        mTv_age = (EditText) findViewById(R.id.tv_age);
        mTvNum = (EditText) findViewById(R.id.tv_num);
        mTv_show = (TextView) findViewById(R.id.tv_show);
        mAdd = (Button) findViewById(R.id.add);
        mDelete = (Button) findViewById(R.id.delete);
        set = (Button) findViewById(R.id.set);
        query = (Button) findViewById(R.id.query);
        mAdd.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        set.setOnClickListener(this);
        query.setOnClickListener(this);
        // mPersonDao = AppDatabase.getAppDatabase(MainActivity.this).getPersonDao();
        mStudentDao = AppDatabase.getAppDatabase(MainActivity.this).getStudentDao();
    }

    @Override
    public void onClick(View v) {
        String name = mTv_name.getText().toString();
        String ageStr = mTv_age.getText().toString();
        String num = mTvNum.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(num)) {
            Utils.showToast(this, "数据不完整");
            return;
        }
        int age = Integer.parseInt(ageStr);
        StudentEntity entity = new StudentEntity(name, age, num);
        switch (v.getId()) {
            case R.id.add:
                long l = mStudentDao.add(entity);
                Utils.showToast(this,l+"");
                break;
            case R.id.delete:
                int i = mStudentDao.delete(entity);
                Utils.showToast(this,i+"");
                break;
            case R.id.set:
                int i1 = mStudentDao.update(entity);
                Utils.showToast(this,i1+"");
                break;
            case R.id.query:
                StudentEntity studentByName = mStudentDao.getStudentByName(name);
                Utils.showToast(this, studentByName.toString());
                break;
        }
        List<StudentEntity> studentAll = mStudentDao.getStudentAll();
        mTv_show.setText(studentAll.toString());
    }


}
