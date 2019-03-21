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