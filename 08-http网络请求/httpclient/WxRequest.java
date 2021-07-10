package com.uboxol.vm.vbox.component.wxface;

import com.google.inject.Singleton;
import com.uboxol.vm.vbox.commons.UboxException;
import com.uboxol.vm.vbox.commons.Utils;
import com.uboxol.vm.vbox.commons.aes.AESUtils;
import com.uboxol.vm.vbox.commons.aes.Base64Util;
import com.uboxol.vm.vbox.commons.http.HttpResult;
import com.uboxol.vm.vbox.component.wxface.protocol.BaseEncrypt;
import com.uboxol.vm.vbox.component.wxface.protocol.WxFacePayReq;
import com.uboxol.vm.vbox.component.wxface.protocol.WxFacePayResp;
import com.uboxol.vm.vbox.component.wxface.protocol.WxFaceVendOutRptReq;
import com.uboxol.vm.vbox.vboxmsg.OrderRoute;
import com.uboxol.vm.vbox.vboxmsg.OrderRouteConvert;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by chengjie on 2019/7/12
 * Description:
 */
@Singleton
public class WxRequest {
    /**
     * 请求支付
     */
    public static OrderRoute requestPaymentToServer(OrderRoute orderRoute, String openId, String tradeSign) {
        WxLog.info("enter | payment");

        // 校验faceCode是否有效
        String faceCode = OrderRouteConvert.dynamicId(orderRoute);
        if (faceCode == null) {
            WxLog.info("WxFacePayLogic-onDataReceived faceCode:" + faceCode);
            orderRoute.setSubState(OrderRoute.SubState.FAILED);
            return orderRoute;
        }

        String productName = OrderRouteConvert.productName(orderRoute);
        WxFacePayReq req = new WxFacePayReq();
        req.setFace_code(faceCode);
        req.setOpenid(openId);
        req.setBody(productName);
        req.setProductId(orderRoute.getSkuId());//orderRoute.getSkuId()
        req.setTotal_fee("" + orderRoute.getPrice());//orderRoute.getPrice()
        req.setOut_trade_no(tradeSign);
        req.setVmid(OrderRouteConvert.vmId(orderRoute));//OrderRouteConvert.vmId(orderRoute)
        // 提交(POST)请求，获取响应
        String param = Utils.getJsonStr(req);
        WxLog.info("FacePay request [payment] to server, http request param:" + param);


        HttpResult httpResult = postSendString(
                WxConst.URL.ORDER_PAY,
                param,
                WxConst.CONNECTION_TIMEOUT,
                WxConst.SO_TIMEOUT
        );

        WxLog.info("FacePay request [payment] to server, http response code:" + httpResult.getCode() + ", msg:" + httpResult.getMsg());

        // 处理Http响应结果
        if (httpResult.getCode() != 200) {    // 发送请求失败
            orderRoute.setSubState(OrderRoute.SubState.FAILED);
            return orderRoute;
        }


        WxFacePayResp payResult = Utils.getObjByJson(httpResult.getMsg(), WxFacePayResp.class);
        WxFacePayResp.Data data = payResult.getData();
        if (payResult.getCode() == 200 && WxConst.MapV.SUCCESS.equals(data.getReturn_code()) && WxConst.MapV.SUCCESS.equals(data.getResult_code())) {  //支付成功

            orderRoute.setSubState(OrderRoute.SubState.DONE);

            /**
             * 将由本地生成的临时订单号替换成来自服务端返回的正式订单号
             */
            String localOrderId = OrderRouteConvert.orderId(orderRoute);
            String serverOrderId = payResult.getData().getOrder_id();
            WxLog.info(String.format("FacePay replace orderId from server, local: %s, server: %s", localOrderId, serverOrderId));
            // 替换成友宝订单号
            OrderRouteConvert.orderId(orderRoute, serverOrderId);

            // 返回用户实际支付金额，大屏根据 【商品原价-用户实际支付价格】= 优惠
            orderRoute.setAmount(Integer.valueOf(payResult.getData().getTotal_fee()));
            return orderRoute;
        } else {
            orderRoute.setSubState(OrderRoute.SubState.FAILED);
            return orderRoute;
        }
    }


    /**
     * 上报出货通知
     *
     * @param orderRoute
     */
    public static void vendOutRpt(OrderRoute orderRoute) {
        WxFaceVendOutRptReq req = new WxFaceVendOutRptReq();
        req.setOrderId(OrderRouteConvert.orderId(orderRoute));
        req.setStatus(orderRoute.getSubState() == OrderRoute.SubState.DONE ? "SUCCESS" : "FAIL");
        req.setVmid(OrderRouteConvert.vmId(orderRoute));

        String param = Utils.getJsonStr(req);
        WxLog.info("FacePay [vendoutRpt] to server, http request param:" + param);
        HttpResult httpResult = postSendString(
                WxConst.URL.VENDOR_STATE,
                param,
                WxConst.CONNECTION_TIMEOUT,
                WxConst.SO_TIMEOUT
        );
        WxLog.info("FacePay [vendoutRpt] to server, http response code:" + httpResult.getCode() + " msg:" + httpResult.getMsg());

        // 处理响应结果
        if (httpResult.getCode() != 200) {    // 发送请求失败
            WxLog.info("FacePay [vendOutRpt] request to server failure");
        }

 /*       BaseResp resp = Utils.getObjByJson(httpResult.getMsg(), BaseResp.class);
        WxLog.info("FacePay [vendoutRpt] to server, resp: " + JSON.toJSONString(resp));*/
    }


    /**
     * 发送http/https请求
     *
     * @param url
     * @param param
     * @param connectionTimeout
     * @param soTimeout
     * @return
     */
    public static HttpResult postSendString(String url, String param, int connectionTimeout, int soTimeout) {
        HttpParams httpParameters = new BasicHttpParams();
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
        // 读取超时
        HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);

        DefaultHttpClient client = getHttpClient(httpParameters);
        HttpResult httpResult = new HttpResult();
        try {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");

            if (WxConst.isEncrypted) {
                BaseEncrypt encrypt = new BaseEncrypt();
                encrypt.setData(AESUtils.encode(Base64Util.encryptString(param)));
                param = Utils.getJsonStr(encrypt);//加密
            }

            StringEntity requestEntity = new StringEntity(param, "utf-8");
            requestEntity.setContentEncoding("UTF-8");

            httpPost.setEntity(requestEntity);
            HttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();

            httpResult.setCode(code);
            if (code == 200) {
                String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (WxConst.isEncrypted) {
                    BaseEncrypt payResult = Utils.getObjByJson(responseStr, BaseEncrypt.class);
                    responseStr = Base64Util.decryptString(AESUtils.decode(payResult.getData()));
                }
                httpResult.setMsg(responseStr);
            } else {
                WxLog.error(new UboxException(url + "-postRequest-StatusCode:" + code));
                httpResult.setMsg("ErrorCode: " + code);
                WxLog.info("ErrorCode: " + code);
            }
        } catch (Exception e) {
            httpResult.setCode(1000);
            httpResult.setMsg(e.getMessage());
            WxLog.error(new UboxException(e));
            WxLog.info(e.getMessage());
        } finally {
            client.getConnectionManager().shutdown();
        }
        return httpResult;
    }
    /**
     * 发送http/https请求
     *
     * @param url
     * @param param
     * @param connectionTimeout
     * @param soTimeout
     * @return
     */
    public static HttpResult postSendString(boolean isEncryp,String url, String param, int connectionTimeout, int soTimeout) {
        HttpParams httpParameters = new BasicHttpParams();
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
        // 读取超时
        HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);

        DefaultHttpClient client = getHttpClient(httpParameters);
        HttpResult httpResult = new HttpResult();
        try {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");

            if (isEncryp) {
                BaseEncrypt encrypt = new BaseEncrypt();
                encrypt.setData(AESUtils.encode(Base64Util.encryptString(param)));
                param = Utils.getJsonStr(encrypt);//加密
            }

            StringEntity requestEntity = new StringEntity(param, "utf-8");
            requestEntity.setContentEncoding("UTF-8");

            httpPost.setEntity(requestEntity);
            HttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();

            httpResult.setCode(code);
            if (code == 200) {
                String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (isEncryp) {
                    BaseEncrypt payResult = Utils.getObjByJson(responseStr, BaseEncrypt.class);
                    responseStr = Base64Util.decryptString(AESUtils.decode(payResult.getData()));
                }
                httpResult.setMsg(responseStr);
            } else {
                WxLog.error(new UboxException(url + "-postRequest-StatusCode:" + code));
                httpResult.setMsg("ErrorCode: " + code);
                WxLog.info("ErrorCode: " + code);
            }
        } catch (Exception e) {
            httpResult.setCode(1000);
            httpResult.setMsg(e.getMessage());
            WxLog.error(new UboxException(e));
            WxLog.info(e.getMessage());
        } finally {
            client.getConnectionManager().shutdown();
        }
        return httpResult;
    }


    static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        X509Certificate[] chain,
                        String authType)
                        throws CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] chain,
                        String authType)
                        throws CertificateException {

                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

    }

    private static DefaultHttpClient getHttpClient(HttpParams params) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


}
