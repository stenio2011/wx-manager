package org.stenio.wx.thirdparty.service.impl;

import aes.AesException;
import aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.stenio.common.util.ConfigUtil;
import org.stenio.common.util.RedisTemplate;
import org.stenio.common.util.StringUtil;
import org.stenio.common.util.XmlUtil;
import org.stenio.wx.thirdparty.api.*;
import org.stenio.wx.thirdparty.service.AuthService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    public static final String AUTHORIZER_ACCESS_TOKEN_PREFIX = "authorizer_access_token:";

    private String appId = ConfigUtil.getString("wx.component_appid");
    private String encodingAesKey = ConfigUtil.getString("wx.encoding_aes_key");
    private String token = ConfigUtil.getString("wx.token");

    private static final String TICKET = "ticket";
    private static final String COMPONENT_ACCESS_TOKEN_KEY = "component_access_token";

    @Override
    public int receiveTicket(String msgSignature, String timeStamp, String nonce, String echoStr) {
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token,
                    encodingAesKey, appId);
            String xml = wxcpt.verifyUrl(msgSignature, timeStamp, nonce, echoStr);
            String ticket = extractTicket(xml);
            return saveTicket(ticket);
        } catch (AesException e) {
            logger.error("token or encoding_aes_key or appid is error", e);
            return -1;
        } catch (Exception e) {
            logger.error("error", e);
            return -1;
        }
    }

    private int saveTicket(String ticket) {
        RedisTemplate.set(TICKET, ticket);
        return 1;
    }

    private String extractTicket(String xml) throws ParserConfigurationException, IOException, SAXException {
        return XmlUtil.getStringByTagName(xml, "ComponentVerifyTicket");
    }

    private String getComponentAccessToken() {
        String componentAccessToken = RedisTemplate.get(COMPONENT_ACCESS_TOKEN_KEY);
        if (StringUtil.isEmpty(componentAccessToken)) {
            ComponentAccessToken accessToken = fetchComponentAccessToken();
            componentAccessToken = accessToken.getComponent_access_token();
            RedisTemplate.setex(COMPONENT_ACCESS_TOKEN_KEY, accessToken.getExpires_in() - 60, componentAccessToken);
        }
        return componentAccessToken;
    }

    private ComponentAccessToken fetchComponentAccessToken() {
        String componentVerifyTicket = getComponentVerifyTicket();
        if (StringUtil.isEmpty(componentVerifyTicket)) {
            throw new IllegalStateException("component verify ticket is null");
        }
        return AuthAPI.getComponentAccessToken(componentVerifyTicket);
    }

    private String getComponentVerifyTicket() {
        return RedisTemplate.get(TICKET);
    }


    @Override
    public String getPreAuthCode() {
        String componentAccessToken = getComponentAccessToken();
        PreAuthCode preAuthCode = AuthAPI.getPreAuthCode(componentAccessToken);
        return preAuthCode.getPre_auth_code();
    }

    @Override
    public String getAuthorizerAccessToken(int appId) {
        AuthorizerAccessToken authorizerAccessToken = RedisTemplate.get(AUTHORIZER_ACCESS_TOKEN_PREFIX + appId, AuthorizerAccessToken.class);
        if (authorizerAccessToken == null) {
            return null;
        }
        return authorizerAccessToken.getAuthorizer_access_token();
    }

    @Override
    public AuthorizerInfoResult getAuthorizerInfo(String appId) {
        String componentAccessToken = getComponentAccessToken();
        AuthorizerInfoResult authorizerInfo = AuthAPI.getAuthorizerInfo(componentAccessToken, appId);
        return authorizerInfo;
    }

    @Override
    public void refreshAccessToken(String appId) {
        String componentAccessToken = getComponentAccessToken();
        String authorizerAccessTokenKey = AUTHORIZER_ACCESS_TOKEN_PREFIX + appId;
        AuthorizerAccessToken authorizerAccessToken = RedisTemplate.get(authorizerAccessTokenKey, AuthorizerAccessToken.class);
        if (authorizerAccessToken == null) {
            return;
        }
        String authorizerAppid = authorizerAccessToken.getAuthorizer_appid();
        authorizerAccessToken = AuthAPI.refreshAccessToken(componentAccessToken, authorizerAccessToken.getAuthorizer_appid(),
                authorizerAccessToken.getAuthorizer_refresh_token());
        authorizerAccessToken.setAuthorizer_appid(authorizerAppid);
        RedisTemplate.setex(authorizerAccessTokenKey, authorizerAccessToken.getExpires_in() - 60, authorizerAccessToken);
    }

    @Override
    public AuthorizerInfoResult doAuth(String authCode) {
        AuthorizerInfoResult authorizationInfo = getAuthorizationInfo(authCode);
        if (authorizationInfo != null) {
            // save token
            AuthorizationInfo authorization = authorizationInfo.getAuthorization_info();
            saveAuthorizationAccessToken(new AuthorizerAccessToken(authorization.getAuthorizer_appid(), authorization.getAuthorizer_access_token(), authorization.getExpires_in(),
                    authorization.getAuthorizer_refresh_token()));
            AuthorizerInfoResult authorizerInfo = getAuthorizerInfo(
                    authorizationInfo.getAuthorization_info().getAuthorizer_appid());
            return authorizerInfo;
        }
        return authorizationInfo;
    }

    @Override
    public void autoRefreshAccessToken() {
        String keyPattern = "authorizer_access_token:*";
        Set<String> keys = RedisTemplate.keys(keyPattern);
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Long ttl = RedisTemplate.ttl(key);
                if (ttl.longValue() < 600L) {
                    StringTokenizer st = new StringTokenizer(key, ":");
                    st.nextToken();
                    refreshAccessToken(st.nextToken());
                }
            }
        }
    }

    private void saveAuthorizationAccessToken(AuthorizerAccessToken authorizerAccessToken) {
        RedisTemplate.setex(AUTHORIZER_ACCESS_TOKEN_PREFIX + authorizerAccessToken.getAuthorizer_appid(), authorizerAccessToken.getExpires_in(), authorizerAccessToken);
    }

    private AuthorizerInfoResult getAuthorizationInfo(String authCode) {
        return fetchAuthorizationInfo(authCode);
    }

    private AuthorizerInfoResult fetchAuthorizationInfo(String authCode) {
        AuthorizerInfoResult authorizationInfo = null;
        String componentAccessToken = getComponentAccessToken();
        return AuthAPI.getAuthorizationInfo(componentAccessToken, authCode);
    }
}
