package com.mashibing.internalcommon.util;

public class RedisPrefixUtils {


    //  乘客验证码的前缀
    public static String verificationCodePrefix = "verification-code-";

    //  token前缀
    public static String tokenPrefix = "token-";

    /**
     * 根据手机号生成key
     * @param phone
     * @return
     */
    public static String generateKeyByPhone(String phone,String identity){
        return  verificationCodePrefix  + identity + "-" + phone;
    }


    /**
     * 根据手机号和身份标识，生成key
     * @param phone
     * @param identity
     * @return
     */
    public static String generatorTokenKey(String phone, String identity, String tokenType){
        return tokenPrefix + phone + "-" + identity + "-" + tokenType;
    }
}
