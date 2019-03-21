### 一、清单文件配置provider ###

    <!--
        authorities ： 自定义，建议包名+fileprovider
        resource : 文件名自定义，需要在res下建立xml文件夹，创建file_paths.xml文件
    -->
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.sankeyun.bb.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
    </provider>

### 二、资源文件xxx.xml ###

	<?xml version="1.0" encoding="utf-8"?>
	<resources>
	    <paths>
	        <external-path path="" name="flag" />
	    </paths>
	  <!--  
	  name：一个引用字符串。name其实没什么用
	    path：文件夹“相对路径”，完整路径取决于当前的标签类型。
	    path 建议为空，表示指定目录下的所有文件、文件夹都可以被共享。
	        相当于"/storage/emulated/0/"
	    -->
	</resources>

### 三、调用系统相机 ###

    /**
     * 拍照功能
     * 系统拍照，结果在onActivityResult处理
     */
    private void photograph() {
        File file = new File(Environment.getExternalStorageDirectory(), "拍照");
        if (!file.exists()) {
            file.mkdir();
        }
        mCameraFile = new File(file, System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = getUriForFile(getContext(), mCameraFile);//获取uri,适配7.0
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PHOTOGRAPH);
        //mCameraFile.getPath='/storage/emulated/0/拍照/1517467048998.jpg'
    }

    //解决Android 7.0之后的Uri安全问题
    private Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider
                    .getUriForFile(context.getApplicationContext(), "com.sankeyun.bb.fileprovider", file);//这里的参数和清单文件相同
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
	
	//结果处理
	 public void onActivityResult(int req, int res, Intent data) {
	     if (RESULT_OK != res)
	         return;
	     switch (req) {
	         case PHOTOGRAPH://拍照
	             String path = mCameraFile.getPath();//图片路径
	             break;
	         default:
	             break;
	     }
	 }

### 通过uri获取文件路径的方法 ###

uri的表示有很多种
1.file:///storage/emulated/0/Android/data/。。。
2.content://media/external/images/media/212304
3.content://com.xxx.fileprovider/...

    /**
     * 获取真实路径
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
            //手动适配7.0 Matisse拍照后获取地址。
            if(data==null){
                if(uri.toString().contains("com.sankeyun.bb.fileprovider")){
                    String path = uri.getPath();
                    int index = path.lastIndexOf("/");
                    String fileName = path.substring(index + 1, path.length());
                    data = "/storage/emulated/0/"+fileName;
                }
            }
        }
        return data;
    }

对于1、2，方法getRealFilePath()可以获取到路径，但是3会返回null,可以根据情况，手动拼接路径（暂时处理）。