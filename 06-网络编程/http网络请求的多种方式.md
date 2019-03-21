



Http-get请求方式
					//1.新建URL路径
					String path = getString(R.string.serverip);
					URL url = new URL(path+"?qq="+URLEncoder.encode(qq,"utf-8")+"&password="+URLEncoder.encode(pwd,"utf-8"));
					//2.用url打开连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if(code == 200){
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("请求失败："+code);
					}

Http-post请求方式
					//post请求提交数据
					String path = getString(R.string.serverip);
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//重要：记得设置请求方式post
					conn.setRequestMethod("POST");
					//重要：记得设置数据的类型
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					String data = "qq="+URLEncoder.encode(qq, "utf-8")+"&password="+URLEncoder.encode(pwd, "utf-8");
					//重要，记得设置数据的长度
					conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
					//重要，记得给服务器写数据
					conn.setDoOutput(true);//声明要给服务器写数据
					//重要，把数据写给服务器。
					conn.getOutputStream().write(data.getBytes());
					
					int code = conn.getResponseCode();
					if(code == 200){
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("请求失败："+code);
					}	







HttpClient-get请求方式。


					//httpclient get 请求提交数据
					String path = getString(R.string.serverip)+"?qq="+URLEncoder.encode(qq)+"&password="+URLEncoder.encode(pwd);

					//1.打开浏览器
					HttpClient client = new DefaultHttpClient();
					//2.输入地址
					HttpGet httpGet = new HttpGet(path);
					//3.敲回车
					HttpResponse response = client.execute(httpGet);
					//获取状态码。
					int code = response.getStatusLine().getStatusCode();
					if(code == 200){
						//获取输入流。
						InputStream is = response.getEntity().getContent();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("请求失败,返回码"+code);
					}

HttpClient-post请求方式。
					//httpclient get 请求提交数据
					String path = getString(R.string.serverip);

					//1.打开浏览器
					HttpClient client = new DefaultHttpClient();
					//2.输入地址
					HttpPost httpPost = new HttpPost(path);
					//设置一个url表单的数据
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("qq", qq));
					parameters.add(new BasicNameValuePair("password", pwd));
					httpPost.setEntity(new UrlEncodedFormEntity(parameters));

					//3.敲回车
					HttpResponse response = client.execute(httpPost);
					int code = response.getStatusLine().getStatusCode();
					if(code == 200){
						InputStream is = response.getEntity().getContent();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("请求失败,返回码"+code);
					}
开源框架
特性：无需在子线程。
需要复制com包。

AsyncHttpClient-get网络请求。
		//登陆的操作，网络请求
		AsyncHttpClient client = new AsyncHttpClient();
		String url = getResources().getString(R.string.serverip)+"?qq="+URLEncoder.encode(qq)+"&password="+URLEncoder.encode(pwd);
		//get请求，传入url。
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Toast.makeText(MainActivity.this, new String(responseBody), 0).show();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(MainActivity.this, new String(responseBody), 0).show();
			}
		});


AsyncHttpClient-post网络请求。（非常方便！）
不需要编码信息（自动utf-8），直接传入参数。
		//登陆的操作，网络请求
		AsyncHttpClient client = new AsyncHttpClient();
		String url = getResources().getString(R.string.serverip);
		//数据集合。
		RequestParams params = new RequestParams();
		params.put("qq", qq);
		params.put("password", pwd);
		//传入数据集合。
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Toast.makeText(MainActivity.this, new String(responseBody), 0).show();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(MainActivity.this, new String(responseBody), 0).show();
			}
		});

AsyncHttpClient-post文件上传。手机端。
复制包。
在文本框直接输入手机中文件夹的地址。
	public void upload(View view){
		//String path = et_path.getText().toString().trim();		
		File file = new File(path);
		//判断手机中文件位置是否存在
		if(file.exists()){
			String serverurl = getString(R.string.server);
			AsyncHttpClient client = new AsyncHttpClient();
			 RequestParams params = new RequestParams();
			try {
				params.put("file", file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} // Upload a File
			client.post(serverurl, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					Toast.makeText(MainActivity.this, "上传成功", 0).show();
				}
				
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					progressBar1.setMax(totalSize);
					progressBar1.setProgress(bytesWritten);
					super.onProgress(bytesWritten, totalSize);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(MainActivity.this, "上传失败", 0).show();
				}
			});
			
		}else{
			Toast.makeText(this, "文件不存在，请检查路径", 0).show();
		}
	}

文件上传-servlet。复制包到webcontent.
复制代码的时候导包选择commons.fileupload。


