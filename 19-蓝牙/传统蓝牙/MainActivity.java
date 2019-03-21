package com.alless.bluetoothdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDialog;
    private static final String TAG = "MainActivity";
    private ListView mListView;
    private View mProgress;
    private boolean isSearch = false;
    private List<BluetoothBean> mBeanList = new ArrayList<>();
    private List<BluetoothBean> mMatchList = new ArrayList<>();
    private Button mSearch;
   // private UUID MY_UUID = UUID.fromString("abcd1234-ab12-ab12-ab12-abcdef123456");//随便定义一个
    private UUID MY_UUID = UUID.randomUUID();
    private BluetoothDevice mDevice;
    private ListView mMatchView;
    private MyBaseAdapter mMyBaseAdapter;
    private MyBaseAdapter mMyMatchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.open_1).setOnClickListener(this);
        findViewById(R.id.open_2).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        mSearch = (Button) findViewById(R.id.search);
        mMatchView = (ListView) findViewById(R.id.listview_match);
        mListView = (ListView) findViewById(R.id.listview);
        mSearch.setOnClickListener(this);
        mProgress = findViewById(R.id.progress);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mMyBaseAdapter = new MyBaseAdapter(mBeanList);
        mListView.setAdapter(mMyBaseAdapter);
        mMyMatchAdapter = new MyBaseAdapter(mMatchList);
        mMatchView.setAdapter(mMyMatchAdapter);

        mListView.setOnItemClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("正在开启中。。。");

        //监听蓝牙状态改变
        // 设置广播信息过滤
        IntentFilter filter = new IntentFilter();
        // 每搜索到一个设备就会发送一个该广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 当全部搜索完后发送该广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //监听蓝牙开启关闭状态
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //监听蓝牙配对状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 设置优先级
        filter.setPriority(Integer.MAX_VALUE);
        // 注册蓝牙搜索广播接收者，接收并处理搜索结果
        registerReceiver(receiver, filter);

        //获取已经配对的蓝牙设备 ,并显示
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BluetoothBean bluetoothBean = new BluetoothBean();
                bluetoothBean.setName(device.getName());
                bluetoothBean.setAddress(device.getAddress());
                mMatchList.add(bluetoothBean);
                mMyMatchAdapter.notifyDataSetChanged();
            }
        }

    }

    class MyBaseAdapter extends BaseAdapter {

        private List<BluetoothBean> mList;

        MyBaseAdapter(List<BluetoothBean> list){
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(MainActivity.this);
            }
            ((TextView) convertView).setText(mList.get(position).getName() + ":" + mList.get(position).getAddress());
            return convertView;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_1://动态开启蓝牙
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                break;
            case R.id.open_2://静态开启蓝牙
                if (!mBluetoothAdapter.isEnabled()) {
                    requestBluetoothPermission();
                }
                break;
            case R.id.close://关闭蓝牙
                mBluetoothAdapter.disable();
                break;
            case R.id.search:
                if (!mBluetoothAdapter.isEnabled()) {
                    showToast("请先开启蓝牙");
                    return;
                }
                //如果当前在搜索，就先取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                if (isSearch) {
                    mSearch.setText("搜索设备");
                    mBluetoothAdapter.cancelDiscovery();
                    mProgress.setVisibility(View.GONE);
                    isSearch = !isSearch;
                    return;
                } else {
                    mSearch.setText("停止搜索");
                    mProgress.setVisibility(View.VISIBLE);
                    isSearch = !isSearch;
                }

                // 开启搜索
                mBeanList.clear();
                mMyBaseAdapter.notifyDataSetChanged();
                mBluetoothAdapter.startDiscovery();

                break;
        }
    }

    private static final int REQUEST_BLUETOOTH_PERMISSION = 10;

    private void requestBluetoothPermission() {
        //判断系统版本
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //向用户解释权限，红米4总是返回false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showToast("需要模糊定位权限");
                }
                //申请权限
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_BLUETOOTH_PERMISSION);
            } else {
                showToast("已经获取定位权限");
                mBluetoothAdapter.enable();
            }
        } else {
            //直接开启
            mBluetoothAdapter.enable();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("授权成功");
                mBluetoothAdapter.enable();
            } else {
                showToast("授权失败");
                // 跳转到应用详情，手动设置定位权限
                getAppDetailSettingIntent(this);
            }
        }
    }

    /**
     * 跳转到应用设置界面
     */
    private void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    /**
     * 监听蓝牙搜索设备，搜索完成，打开关闭蓝牙
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND: //找到设备
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        BluetoothBean bluetoothBean = new BluetoothBean();
                        bluetoothBean.setName(device.getName());
                        bluetoothBean.setAddress(device.getAddress());
                        mBeanList.add(bluetoothBean);
                        mMyBaseAdapter.notifyDataSetChanged();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED://搜索完成
                    showToast("搜索完成");
                    mProgress.setVisibility(View.GONE);
                    mSearch.setText("搜索设备");
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED://状态改变
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    switch (state) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            mProgressDialog.show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            mProgressDialog.cancel();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED://配对状态
                   mDevice= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (mDevice.getBondState()){
                        case BluetoothDevice.BOND_BONDING:
                            showToast("正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            showToast("完成配对");
                            BluetoothBean bluetoothBean = new BluetoothBean();
                            bluetoothBean.setName(mDevice.getName());
                            bluetoothBean.setAddress(mDevice.getAddress());
                            mMatchList.add(bluetoothBean);
                            mMyMatchAdapter.notifyDataSetChanged();
                            connect(mDevice);
                            break;
                        case BluetoothDevice.BOND_NONE:
                            showToast("取消配对");
                            break;
                    }
                    break;
            }
        }
    };


    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        BluetoothBean bluetoothBean = mBeanList.get(position);
        String address = bluetoothBean.getAddress();
        mDevice = mBluetoothAdapter.getRemoteDevice(address);
        try {
            Boolean returnValue = false;
            if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                Method createBond = BluetoothDevice.class.getMethod("createBond");
                returnValue = (Boolean) createBond.invoke(mDevice);
                if(returnValue){
                    connect(mDevice);
                }
            } else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                connect(mDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void connect(BluetoothDevice device) {
        Log.d(TAG, "connect: "+MY_UUID.toString());
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
