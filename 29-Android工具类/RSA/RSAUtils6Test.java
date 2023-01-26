package com.example.wifichangedemo;

import android.support.test.runner.AndroidJUnit4;

import com.example.wifichangedemo.util.RSAUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

/**
 * Created by chengjie on 2018/9/25
 * Description:
 */
@RunWith(AndroidJUnit4.class)
public class RSAUtils6Test {
    public static final String PUBLIC_KEY_STR ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCg+2pZ2CXMb5WfJmGS0OAZVM53\n" +
            "FsUPb1FxHV5Z7dHuPXu32eFJdY1KiBlEdiNuBuJOTr2KriiT/swW1wXVM5YPEMTN\n" +
            "XkDlNYT3tIestbO4CC5Sak0u0XVHlC32koQ+TQxNvnS+pOf/PJuo/AUsJ2wMRLGa\n" +
            "2mTOWiQP2fuIRflmBwIDAQAB";
    public static final String PRIVATE_KEY_STR ="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKD7alnYJcxvlZ8m\n" +
            "YZLQ4BlUzncWxQ9vUXEdXlnt0e49e7fZ4Ul1jUqIGUR2I24G4k5OvYquKJP+zBbX\n" +
            "BdUzlg8QxM1eQOU1hPe0h6y1s7gILlJqTS7RdUeULfaShD5NDE2+dL6k5/88m6j8\n" +
            "BSwnbAxEsZraZM5aJA/Z+4hF+WYHAgMBAAECgYAkMcLspmOBPolmeguwJdRs/Zta\n" +
            "PT1bRg0KoK7JC2aK6Tn60TsM+CgnXMhJk0JOB7A0KRIGrzeiZsReUkQWdn3DVAz3\n" +
            "s5keBlrZ+mU/Xrwbs0anYXKBbnHqrx0rvCgBoFVfEQ95ADnx6MWLVTDEjaZeK8bG\n" +
            "4v2ZrxyV5uU8tF9gwQJBANG7cqcdVEHUUHtTsH8wn4/HclZ05oPNA9wRcSG+x+Lm\n" +
            "dgnRiESQAk8Wkng+n8Ho/R9en6h+BaSQQMfO2pc0WucCQQDEftJSosPQSOjfvhMc\n" +
            "lRZKvOXg6E2RgzRTXUrrqMhTRE8W6R9p5m6J6L25DwSmOt/9yGFoaVFJxP5Iuaeg\n" +
            "R1fhAkB4Cwkxu6jJUQOLwzgHZMY/XlIESD7Ue5jU8irFvNPd/Djdn8+WJrMbn3jQ\n" +
            "WE/KhlezB87ses0yBtr9Qll9KBtLAkB+J14pJ87H/sdkPshOZXl2ami8SOjkL3R8\n" +
            "wTzEqAxVSVntv6Yne4YkjISiN34oq0v7x5aiTlueHReU4X05bhSBAkA88fE5WTca\n" +
            "jX1Ul0vD+iqwkrD+X7cNp3pHyDv0lbO+s7bEeLiCx1Oaj71w/GyPogTZijqaaef+\n" +
            "TWp63x3AzTQa";

    @Test
    public void print(){
        PublicKey publicKey = RSAUtils.keyStrToPublicKey(PUBLIC_KEY_STR);
        PrivateKey privateKey = RSAUtils.keyStrToPrivate(PRIVATE_KEY_STR);
        String testStr1 = "测试RSA加密效果。";

        String encryptDataByPublicKey = RSAUtils.encryptDataByPublicKey(testStr1.getBytes(), publicKey);
        String decryptedToStrByPrivate = RSAUtils.decryptedToStrByPrivate(encryptDataByPublicKey, privateKey);

        System.out.println("原文："+testStr1+"\n 加密后："+encryptDataByPublicKey+"\n 解密后："+decryptedToStrByPrivate);
        assertEquals("2", 1+1);
    }
}
