package org.stenio.wx.thirdparty.api;

import org.stenio.common.util.ConfigUtil;
import org.stenio.common.util.HttpClient;
import org.stenio.common.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthAPI {

    private static final String COMPONENT_APPID = ConfigUtil.getString("wx.component_appid");

    private static final String COMPONENT_APPSECRET = ConfigUtil.getString("wx.component_appsecret");

    public static ComponentAccessToken getComponentAccessToken(String componentVerifyTicket) {
        Map<String, String> params = new HashMap<>();
        params.put("component_appid", COMPONENT_APPID);
        params.put("component_appsecret", COMPONENT_APPSECRET);
        params.put("component_verify_ticket", componentVerifyTicket);
        String responseBody = HttpClient.postJson("https://api.weixin.qq.com/cgi-bin/component/api_component_token", params);
        return JsonUtil.parse(responseBody, ComponentAccessToken.class);
    }

    public static PreAuthCode getPreAuthCode(String componentAccessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("component_appid", COMPONENT_APPID);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="
                + componentAccessToken;
        String responseBody = HttpClient.postJson(url, params);
        return JsonUtil.parse(responseBody, PreAuthCode.class);
    }

    public static AuthorizerInfoResult getAuthorizationInfo(String componentAccessToken, String authorizationCode) {
        Map<String, String> params = new HashMap<>();
        params.put("component_appid", COMPONENT_APPID);
        params.put("authorization_code", authorizationCode);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="
                + componentAccessToken;
        String responseBody = HttpClient.postJson(url, params);
        return JsonUtil.parse(responseBody, AuthorizerInfoResult.class);
    }

    public static AuthorizerTokenResult refreshAccessToken(String componentAccessToken, String authorizerAppid,
                                                           String authorizerRefreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("component_appid", COMPONENT_APPID);
        params.put("authorizer_appid", authorizerAppid);
        params.put("authorizer_refresh_token", authorizerRefreshToken);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="
                + componentAccessToken;
        String responseBody = HttpClient.postJson(url, params);
        AuthorizerTokenResult result = JsonUtil.parse(responseBody, AuthorizerTokenResult.class);
        result.setAuthorizer_appid(authorizerAppid);
        return result;

    }

    public static AuthorizerInfoResult getAuthorizerInfo(String componentAccessToken, String authorizerAppid) {
        Map<String, String> params = new HashMap<>();
        params.put("component_appid", COMPONENT_APPID);
        params.put("authorizer_appid", authorizerAppid);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="
                + componentAccessToken;
        String responseBody = HttpClient.postJson(url, params);
        return JsonUtil.parse(responseBody, AuthorizerInfoResult.class);
    }

}
