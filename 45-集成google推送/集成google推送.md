*<u>ps:服务端和客户端都要能连接外网，否则gg。客户端sdk版本号不能随便变动，很容易报各种莫名其妙的错。</u>*

### 客户端

1. 在推送服务官网注册应用，获取到google-srvices.json文件，放在项目根目录。

2. 在根目录build.gradle文件中增加

   ~~~java
      dependencies {
   				...
           classpath 'com.google.gms:google-services:4.0.0'
       }
   ~~~

3. 在module目录build.gradle文件中增加

   ~~~java
   dependencies {
     implementation 'com.google.firebase:firebase-core:16.0.6'
     implementation 'com.google.firebase:firebase-messaging:17.3.4' 
   }
   // ADD THIS AT THE BOTTOM
   apply plugin: 'com.google.gms.google-services'
   ~~~

4. 添加自定义服务类。

   ~~~java
         <service
               android:name=".MyGoogleMessageService">
               <intent-filter>
                   <action android:name="com.google.firebase.MESSAGING_EVENT" />
               </intent-filter>
           </service>
                     
    /**
    * google推送
    */
   public class MyGoogleMessageService extends FirebaseMessagingService {
   
       public static final String KEY_START_SERVICE = "startService";
   
       public MyGoogleMessageService() {
       }
   
       /**
        * "notification" : {
        * "body" : "great match!",
        * "title" : "Portugal vs. Denmark",
        * "icon" : "myicon"
        * "color": #ffffff
        * },
        * "data":{
        * "body" : "great match!",
        * "title" : "Portugal vs. Denmark"
        * }
        * }
        *
        * @param remoteMessage
        */
       @Override
       public void onMessageReceived(RemoteMessage remoteMessage) {
           super.onMessageReceived(remoteMessage);
           //todo 收到的推送消息
           LogInfo.i("onMessageReceived messageId : " + remoteMessage.getMessageId());
           Map<String, String> data = remoteMessage.getData();
           if (data != null) {
               String key = data.get("key");
               String time = data.get("time");
               LogInfo.i("onMessageReceived key =" + key + " time=" + time);
               if (KEY_START_SERVICE.equals(key)) {
                   //todo 唤醒屏幕？
                   startService();
               }
           }
       }
   
       private void startService() {
           PackageManager packageManager = this.getPackageManager();
           Intent intent = packageManager.getLaunchIntentForPackage("com.alless.remote");
           startActivity(intent);
       }
   
       @Override
       public void onDeletedMessages() {
           super.onDeletedMessages();
       }
   
       @Override
       public void onMessageSent(String s) {
           super.onMessageSent(s);
       }
   
       @Override
       public void onSendError(String s, Exception e) {
           super.onSendError(s, e);
       }
   
       //只会在首次启动的时候生成，如果没保存，后面就要主动获取了。
       // SpUtils.setGoogleToken(FirebaseInstanceId.getInstance().getToken());
       @Override
       public void onNewToken(String s) {
           super.onNewToken(s);
           //发送token到服务器
           LogInfo.i("onNewToken: " + s);
           SpUtils.setGoogleToken(s);
       }
   
   }
   ~~~



### 服务端

1. pom.xml里配置

   ~~~java
         <!--google推送-->
           <dependency>
               <groupId>com.google.firebase</groupId>
               <artifactId>firebase-admin</artifactId>
               <version>8.2.0</version>
           </dependency>
   ~~~

2. 指定客户端的token，推送消息。

   ~~~java
    String fcmToken = NettyManager.getFcmToken(targetId);
   
           LogInfo.infos("fcmToken:"+fcmToken);
           if (!StringUtils.hasText(fcmToken))
               return;
           Message message = Message.builder()
                   .putData("key", key)
                   .putData("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                   .setToken(fcmToken)
                   .build();
           try {
               String response = FirebaseMessaging.getInstance().send(message);
               LogInfo.infos("PushMessage response:"+response);
           } catch (FirebaseMessagingException e) {
               e.printStackTrace();
               LogInfo.infos("PushMessage error:"+e.getMessage());
           }
   ~~~

   