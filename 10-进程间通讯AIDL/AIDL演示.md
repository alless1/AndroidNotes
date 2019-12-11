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


### 说明 ###

	关于标识TAG（in,out,inout）的解释：
	AIDL中的定向 tag 表示了在跨进程通信中数据的流向，其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，而 inout 则表示数据可在服务端与客户端之间双向流通。其中，数据流向是针对在客户端中的那个传入方法的对象而言的。in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；out 的话表现为服务端将会接收到那个对象的参数为空的对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。

	简单一点说：就是客户端传入的参数对象，是否会被服务端改变。in是不会被改变，out是会被改变，而且先会被清空。inout是会被改变。

