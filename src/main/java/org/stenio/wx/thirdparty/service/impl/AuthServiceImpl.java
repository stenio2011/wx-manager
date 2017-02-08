package org.stenio.wx.thirdparty.service.impl;

import aes.AesException;
import aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stenio.common.util.ConfigUtil;
import org.stenio.wx.thirdparty.api.AuthorizerInfoResult;
import org.stenio.wx.thirdparty.service.AuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private String token = ConfigUtil.getString("wx.component_appid");
    private String encodingAesKey = ConfigUtil.getString("wx.encoding_aes_key");
    private String appId = ConfigUtil.getString("wx.token");

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

    private int saveTicket(String ticket){

        return 1;
    }

    private String extractTicket(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(xml);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);
        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("ComponentVerifyTicket");
        String ticket = nodelist1.item(0).getTextContent();
        return ticket;
    }

    @Override
    public String getPreAuthCode() {
        return null;
    }

    @Override
    public String getAuthorizerAccessToken(int appId) {
        return null;
    }

    @Override
    public AuthorizerInfoResult getAuthorizerInfo(String appId) {
        return null;
    }

    @Override
    public void refreshAccessToken(String appId) {

    }

    @Override
    public void doAuth(String authCode) {

    }
}
