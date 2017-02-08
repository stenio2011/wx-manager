package org.stenio.wx.thirdparty.service;


import org.stenio.wx.thirdparty.api.AuthorizerInfoResult;

public interface AuthService {

    int receiveTicket(String msgSignature, String timeStamp, String nonce, String echoStr);

    String getPreAuthCode();

    String getAuthorizerAccessToken(int appId);

    AuthorizerInfoResult getAuthorizerInfo(String appId);

    void refreshAccessToken(String appId);

    void doAuth(String authCode);

}
