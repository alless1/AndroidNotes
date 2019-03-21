##一.带cookie的网络请求
####1.在请求需要设置缓存,并且登录过.

		compile 'com.zhy:okhttputils:2.6.2'
	
       // 初始化sHttpclient,添加cookie存储器
        sHttpclient = new OkHttpClient.Builder()
                // 添加cookie存储器
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
                .build();
####2.使用
	
    //重写网络请求的方法  //  增加cookie请求 重写的方法
    @Override
    public void loadDataByGet(final CallBack<FavoriteList> callBack, final int reqType) {
        String baseUrl = "http://www.oschina.net/action/api/favorite_list";
        Map<String, String> params = getParamsMap();
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("&");
        }
        baseUrl = baseUrl + sb.substring(0, sb.lastIndexOf("&")).toString();

        // 创建请求
        Request request = new Request
                .Builder()
                .url(baseUrl)
                .build();

        // 执行请求得到结果
        //Response response = sHttpclient.newCall(request).execute();

        sHttpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(call, e, id, reqType);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "1111onResponse: " + response);
                Type type = ((ParameterizedType) CollectionProtocol.this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                FavoriteList result = XmlUtils.toBean((Class<FavoriteList>) type,response.body().bytes() );
                callBack.onResponse(result, id, reqType);

            }
        });
    }

####注意:次方法是在子线程中,如果不适用,请使用okhttpUtils的工具.