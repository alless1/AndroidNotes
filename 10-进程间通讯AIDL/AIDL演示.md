定义一个aidl的接口,再通过绑定服务将接口的引用传递出去.


# AIDL调用远程方法

## 一 服务端

1.新建 接口.aidl文件(编译后自动生成java文件)

	package com.demo.aidl;
	
	interface IMyAidlInterface {
	   int add(int a,int b);
	}

2.新建service,对外暴露接口.

		public class MyService extends Service {
		    @Nullable
		    @Override
		    public IBinder onBind(Intent intent) {
		        return mIBinder;
		    }
		    private IBinder mIBinder = new IMyAidlInterface.Stub(){
		        @Override
		        public int add(int a, int b) throws RemoteException {
		            return a+b;
		        }
		    };
		
		}

AndroidManifest.xml中配置:

<service android:name=".MyService" android:exported="true"/>

## 二 客户端

1.复制.adil文件,包括包名.

2.在程序中绑定服务,获取接口对象.

     //通过意图绑定远程服务
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.myapplication", "com.example.myapplication.MyService"));
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIMyAidlInterface = null;
            }
        };
        bindService(intent, mConn,BIND_AUTO_CREATE);

# AIDL传输自定义类型.

## 一 服务端 (自定义类java文件的包名和aidl文件的包名要相同)

1.新建 接口.aidl文件 (aidl文件夹下)

	package com.example.sserver;
	//手动导入Person包
	import com.example.sserver.Person;
	
	interface IPersonInterface {
	    //指定输入或者输出in/out
	    List<Person> addPerson(in Person person);
	}

2.新建 自定义类.aidl文件 (aidl文件夹下)

	package com.example.sserver;
	// 描述
	parcelable Person;

3.新建 自定义类.java 实现Parcelable接口,注意元素的读写顺序一致.(java文件夹下) 

	public class Person implements Parcelable{
		...
	    protected Person(Parcel in) {
	        name = in.readString();
	        age = in.readInt();
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeString(name);
	        dest.writeInt(age);
	    }
		...
	}

4.新建服务PersonService (java文件夹下)

	public class PersonService extends Service {
	    private List<Person> mPersons;
	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
	        mPersons = new ArrayList<Person>();
	        return mIBinder;
	    }
	    private IBinder mIBinder = new IPersonInterface.Stub() {
	        @Override
	        public List<Person> addPerson(Person person) throws RemoteException {
	            mPersons.add(person);
	            return mPersons;
	        }
	    };
	    
	}

注册服务:

	<service android:name=".PersonService" android:exported="true"/>


## 二 客户端

1.把 自定义类.java 复制到java文件夹下,原包名保持一致.
  把 自定义类.aidl 和 接口.aidl 复制到 aidl文件夹下,包名保持一致.

2.通过意图绑定服务,获取接口,就可以调用接口里的方法了.

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.sserver", "com.example.sserver.PersonService"));
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIPersonInterface = IPersonInterface.Stub.asInterface(service);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIPersonInterface = null;
            }
        };
        bindService(intent, mConn, BIND_AUTO_CREATE);
