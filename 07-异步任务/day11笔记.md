##一 、 Widget

> 窗口小部件 、 桌面小部件


###实现Widget步骤

1. 拷贝以下代码到清单文件注册

		 <!-- 其实就是注册一个广播接收者 -->
        <receiver android:name="com.itheima.widgetdemo.ExampleAppWidgetProvider" >
		    <intent-filter>
		        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
		    </intent-filter>
		    
		    
		    <!-- meta-data ： 元数据   描述这份数据的数据。
		    	@xml/ ： res/xml
		    -->
		    
		    <meta-data android:name="android.appwidget.provider"
		               android:resource="@xml/example_appwidget_info" />
		</receiver>
        

2. 在res/新建一个文件夹  xml , 然后在里面定义一个xml文件，写入以下内容


		<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
		    android:minWidth="40dp"
		    android:minHeight="140dp"
		    android:updatePeriodMillis="0"
		    android:initialLayout="@layout/example_appwidget"
		    android:resizeMode="horizontal|vertical">
		
		  <!--   
		     
		    android:minWidth="40dp" ： 小部件的最小宽度
		    android:minHeight="40dp" 小部件最小高度
		    android:updatePeriodMillis="86400000"  过了多久时间才更新小部件上的内容
		    android:previewImage="@drawable/preview" ： 预览的图像
		    android:initialLayout="@layout/example_appwidget"  ： 初始化布局，其实就是小部件长什么样子
		    android:configure="com.example.android.ExampleAppWidgetConfigure"  额外的配置
		    android:resizeMode="horizontal|vertical"  这个小部件可以水平或者纵向拉伸放大区域
		    android:widgetCategory="home_screen|keyguard"  属于哪一种类型
		    android:initialKeyguardLayout="@layout/example_keyguard"> 锁屏显示的样子
		    
		     -->
		</appwidget-provider>

3. 定义布局。 initialLayout 需要的布局

4. 定义一个类， 继承AppWidgetProvider ，然后在清单文件注册即可。


		public class ExampleAppWidgetProvider extends AppWidgetProvider{

		}


##1. 先呈现数据上来显示

> 把目前运行了多少个进程，占用多少内存，显示到widget上。


		@Override
		public void onCreate() {
			super.onCreate();
			
			
			//更新窗口小部件内容。
			
			//1. 查询目前运行的进程有几个  占用的内存是多少
			int count = ProcessProvider.getRunningProcessCount(this);
			long useMemory = ProcessProvider.getUsedMemory(this);
			long totalMemory = ProcessProvider.getTotalMemory(this);
			
			//2.  更新到小部件上。 
			/*
			 * 获取一个类的对象
			 * 	1. 直接new
			 * 2. 单例模式 、 这个类身上有一个静态方法
			 * 3. 工厂模式 、 构建者模式 。  alertdialog.builder   BitmapFactory
			 * 
			 */
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			
			
			//使用小部件的管理者去更新小部件上的内容 , 系统会找到这个类，然后找到所有与这个类有关的那些窗口小部件，更新上面的内容。
			//参数一： Component name 组件名   组件名其实就是对某一个组件的包装。 里面包含两个成员， 包名 、 类名。
			ComponentName provider = new ComponentName(this, ProcessWidget.class);
			
			//参数二： RemoteView
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
			
			//把这个布局身上的这两个控件的值编程 括号的第二个参数
			views.setTextViewText(R.id.process_count, "正在运行的进程:"+count+"个");
			views.setTextViewText(R.id.process_memory, "可用的内存:"+Formatter.formatFileSize(this, totalMemory - useMemory ));
			
			//让桌面小部件管理者去 ，更新 ProcessWidget 相关的那些所有小部件， 更新他们的UI ，变成process_widget布局
			appWidgetManager.updateAppWidget(provider, views);
		}


##2. 操作widget然后执行背后逻辑

* 需求

>  点击一键清理，执行后续的清理逻辑。

* 实现思路

> 点击按钮，发送一个广播，接收广播，然后执行清理逻辑


				//发送一个广播， 请问 intent在这怎么写。 代表意思。 意图。。。
					
					Intent intent =new Intent();
					intent.setAction("com.itheima.widget.clear");
					
					
					//点击一键清理之后，发送一个广播出来
					PendingIntent pendingIntent = PendingIntent.getBroadcast(ProcessWidgetService.this, 1, intent, 0);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);



		/**
		 * 注册接收那个一键清理的广播接收者
		 */
		private void regiseterReceiver() {
			
			mReceiver = new ProcessClearReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.itheima.widget.clear");
			this.registerReceiver(mReceiver, filter);
			
		}
	
	
		class ProcessClearReceiver extends BroadcastReceiver{
	
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("有人点击了一键清理...");
				
					//1. 清理进程 + 内存
					int beforeCount = ProcessProvider.getRunningProcessCount(context);
					long beforeMemory = ProcessProvider.getUsedMemory(context);
				
					//a. 先获取所有的进程
					
					//b. 清理进程。
					
					//c. 比较前后，就知道清理了多少个进程。
					List<ProcessBean> runList = ProcessProvider.getProcessList(context);
					for (ProcessBean processBean : runList) {
						if(processBean.pkg.packageName.equals(getPackageName())){
							//跳过当前循环，执行下一次循环
							continue;
						}
						ProcessProvider.killProcess(context, processBean.pkg.packageName);
					}
					
					int afterCount = ProcessProvider.getRunningProcessCount(context);
					long afterMemory = ProcessProvider.getUsedMemory(context);
			
					//有进程被干掉了
					if(beforeCount > afterCount){
						
						int count = beforeCount - afterCount;
						long memory = beforeMemory - afterMemory;
						
						String text = "清理了"+count+"个进程，释放了"+Formatter.formatFileSize(context, memory)+"内存";
						
						ToastUtils.makeText(context, text);
					}else{
						ToastUtils.makeText(context, "没有可清理的进程");
					}
				
				
				//2. 清理了多少个进程，释放了多少内存。   没有可清理的进程。
				
				
			}

##3. 细节处理

* 小部件不可用停止服务

		//小部件不可用了。
		@Override
		public void onDisabled(Context context) {
			super.onDisabled(context);
			context.stopService(new Intent(context , ProcessWidgetService.class));
		}

* 服务销毁也应该取消广播接收者注册

	内存泄漏： 一个对象存在的必要性了，但是没有被销毁还占据内存。
	
	内存溢出： 内存不足， 不够用

		
		@Override
		public void onDestroy() {
			super.onDestroy();
			//服务销毁，取消注册广播接收者
			unregisterReceiver(mReceiver);
		}

* 取消线程执行。

		@Override
		public void onDestroy() {
			super.onDestroy();
			//服务销毁，取消注册广播接收者
			unregisterReceiver(mReceiver);
			
			//取消后续的动作
			isCancel = true; 
		}

###疑问

1. 怎么widget也能在子线程更新UI。

		views.setTextViewText(R.id.process_count, "正在运行的进程:"+count+"个");

> 其实这并不是我们去更新，而是拜托AppWidgetManger 去更新 updateAppWidget

2. 广播接收者不是有两种注册方式么？ 为什么这里使用动态注册（代码注册）?

> 动态注册其实是与组件绑定了， 静态注册（清单文件注册）是与应用绑定。 当组件销毁的时候，广播接收者也就不能收广播了。 当小部件都已经不存在的时候，也就不用再收广播了，所以不需要使用静态注册。



# 二 、 定时器

### Java

* Timer

			final Timer timer = new Timer();
				
				/*
				 * 参数一：执行的任务
				 * 参数二：　第一次间隔多久执行
				 * 参数三：　以后的每一次任务间隔多久执行　
				 * 
				 * 		间隔使用单位都是　毫秒
				 */
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						System.out.println("------"+System.currentTimeMillis());
						//停止任务
						timer.cancel();
						
					}
				}, 0, 1000);

* Executors


						
		final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
			
			/*
			 * 参数一： 执行的任务
			 * 参数二： 第一次间隔多久执行
			 * 参数三： 以后每次间隔多久
			 * 参数四： 时间单位
			 */
			pool.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("----=======++++" + System.currentTimeMillis());
					
					pool.shutdown();
				}
			}, 0, 1, TimeUnit.SECONDS);

### Android

* Handler

			Handler handler = new Handler();
			
			//handler.sendMessageDelayed(msg, delayMillis)
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("handler执行了...");
				}
			}, 5000);
			
			handler.removeCallbacks(r)

* AlarmManager

			
			private AlarmManager manager;
			private PendingIntent operation;
		
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				
				
				/*Handler handler = new Handler();
				
				//handler.sendMessageDelayed(msg, delayMillis)
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("handler执行了...");
					}
				}, 5000);
				
				handler.removeCallbacks(r)*/
				
				registerReceiver(new AlarmReceiver(), new IntentFilter("com.itheima.alarm"));
				
				
				manager = (AlarmManager) getSystemService(ALARM_SERVICE);
				
				Intent intent =new Intent();
				intent.setAction("com.itheima.alarm");
				operation = PendingIntent.getBroadcast(this, 1, intent, 0);
				
				manager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1000, operation);
			}
			
			class AlarmReceiver extends BroadcastReceiver{
		
				@Override
				public void onReceive(Context context, Intent intent) {
					System.out.println("5点钟到了， 该起床写代码了...");
					
					manager.cancel(operation);
				}
				
			}

#三、 AsyncTask

> 异步任务

* Thread + Handler 

> 灵活性很高， 但是代码量比较多。

* AsyncTask

> 线程池 + Handler的包装，让程序开发变得简单一点。

* onPreExecute

	调用时机
	
		第一个调用的方法，在任务还没开始前就调用了

	调用线程

		在主线程调用

	方法作用

		用于配置任务，如： 显示一个进度条，提醒用户 将要加载数据了...		

	方法参数

		无

	方法返回值

		无


* doInBackground

		调用时机
			
			在 onPreExecute(), 执行完毕后就调用

		调用的线程

			在子线程调用

		方法作用

			用于执行耗时操作，比如：联网请求，获取数据

		方法参数

			类型： 由类上面的泛型第一个参数指定。
			值： 是从execute方法传递进来的。


		方法返回值

* onProgressUpdate

		调用时机

			在手动调用publishProgress的时候执行
		调用线程

			在主线程调用

		方法作用

			用于更新进度条..提醒用户。

		方法参数

			类型： 由类上面的第二个泛型限定
			值： publishProgress 传递过来的。

		方法返回值
			无

* onPostExecute


		调用时机

			在doInBackground 执行完毕后调用
		调用线程

			在主线程调用
		方法作用
		
			更新UI。 (主线程调用 + doInBackground请求下来的数据)
			
		方法参数

			类型： 由类上面的泛型第三个定义
			值： 从doInBackground的方法值传过来的。
		
		方法返回值

			无

###AsyncTask细节

1.  实例化以及execute方法必须在主线程中执行
2.  以上四个方法不要手动调用
3.  一个任务对象只能执行一次， 如果执行多次将会抛出异常。
4.  从1.6开始， 任务允许并发执行 。 到了3.0开始，只能是一个任务自己执行。
 
		如果想改变这个现状可以调用 executeOnExecutor( 来给定自己的线程池， 定义自己的排队机制。

#四、短信备份&还原


* 短信备份

	1. 查询所有短信

			/**
			 * 查询数据库中的所有短信，并且返回一个集合 
			 * @param context
			 * @return
			 */
			public static List<SmsBean> readSms(Context context){
				
				List<SmsBean> list =new ArrayList<SmsBean>();
				
				ContentResolver resolver = context.getContentResolver();
				Uri uri = Uri.parse("content://sms");
				Cursor cursor = resolver.query(uri, new String[]{"address","body","type","date"}, null, null, null);
				while(cursor.moveToNext()){
					String address = cursor.getString(0);
					String body = cursor.getString(1);
					String type = cursor.getString(2);
					String date = cursor.getString(3);
					
					list.add(new SmsBean(address, body, type, date));
				}
				cursor.close();
				return list;
			}


	2. 使用xml存储短信

			try {
				//1. 得到序列化器
				XmlSerializer xml = Xml.newSerializer();
				//2. 指定这个xml存放的位置
				OutputStream os = new FileOutputStream("/mnt/sdcard/smss.xml");
				xml.setOutput(os, "UTF-8");
				
				//3. 拼接xml
				
				xml.startDocument("utf-8", true);
				xml.startTag(null, "smss");
				
				
				for (SmsBean smsBean : list) {
					xml.startTag(null, "sms");
					
						xml.startTag(null, "address");
						xml.text(smsBean.address);
						xml.endTag(null, "address");
						
						xml.startTag(null, "body");
						xml.text(smsBean.body);
						xml.endTag(null, "body");
						
						xml.startTag(null, "date");
						xml.text(smsBean.date);
						xml.endTag(null, "date");
						
						xml.startTag(null, "type");
						xml.text(smsBean.type);
						xml.endTag(null, "type");
						
						
					xml.endTag(null, "sms");
				}
				
				xml.endTag(null, "smss");
				xml.endDocument();
				
			} catch (Exception e) {
				e.printStackTrace();
			}


#五 总结

* appWidget【熟悉】

> 参照文档 + 加上自己的猜测。 

	1. 先简单实现一个widget。
	2. 移植集成项目
	3. 在代码里面更新widget内容
	4. 在widget上操作代码

* 定时器【了解】

	java
		timer
		executeors

	android
		handler
		alarmmanager

* AsyncTask【精通】

4个

		onPreExecute

			作用：  弹出一个进度条对话框

		doInBackground【必须】
	
			作用： 执行耗时操作。 请求数据

		onProgressUpdate

			作用： 更新UI  ， 更新进度条

		onPostExecute

			作用： 更新UI 【数据 +主线程】

* 细节：

	1. 不要手动调用四个方法
	2. 不能重复执行
	3. 实例创建和execute 必须在主线程。
			

* 短信备份

	读短信

		ContentResovler

	存短信

		xml