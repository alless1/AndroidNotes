### handler引起的内存泄漏处理办法 ###

>1.静态内部类。 2.弱引用 3.销毁页面的时候移除消息

   
	 /**  
	     *   
	     * 实现的主要功能。  
	     *   
	     * @version 1.0.0  
	     * @author Abay Zhuang <br/>  
	     *         Create at 2014-7-28  
	     */  
    public class HandlerActivity2 extends Activity {  
      
        private static final int MESSAGE_1 = 1;  
        private static final int MESSAGE_2 = 2;  
        private static final int MESSAGE_3 = 3;  
        private final Handler mHandler = new MyHandler(this);  
      
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.activity_main);  
            mHandler.sendMessageDelayed(Message.obtain(), 60000);  
      
            // just finish this activity  
            finish();  
        }  

	    @Override  
	    public void onDestroy() {  
	        //  If null, all callbacks and messages will be removed.  
	        mHandler.removeCallbacksAndMessages(null);  
	    }  

      
        public void todo() {  
        };  
      
        private static class MyHandler extends Handler {  
            private final WeakReference<HandlerActivity2> mActivity;  
      
            public MyHandler(HandlerActivity2 activity) {  
                mActivity = new WeakReference<HandlerActivity2>(activity);  
            }  
      
            @Override  
            public void handleMessage(Message msg) {  
                System.out.println(msg);  
                if (mActivity.get() == null) {  
                    return;  
                }  
                mActivity.get().todo();  
            }  
        }  

