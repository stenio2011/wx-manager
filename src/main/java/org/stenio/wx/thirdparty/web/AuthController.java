package org.stenio.wx.thirdparty.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.stenio.common.util.ConfigUtil;
import org.stenio.common.util.IOUtil;
import org.stenio.common.util.StringUtil;
import org.stenio.common.util.XmlUtil;
import org.stenio.wx.thirdparty.api.AuthorizerInfoResult;
import org.stenio.wx.thirdparty.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by stenio on 2017/2/8.
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/receive_ticket", method = RequestMethod.POST)
    @ResponseBody
    public String receiveTicket(@RequestParam String msg_signature, @RequestParam String timestamp, @RequestParam String nonce, HttpServletRequest request) throws IOException {
        String xml = IOUtil.readString(request.getInputStream());
        String echoStr = extractEncrypt(xml);
        if (StringUtil.isEmpty(echoStr)) {
            return "FAIL";
        }
        int c = authService.receiveTicket(msg_signature, timestamp, nonce, echoStr);
        if (c > 0) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }

    private String extractEncrypt(String xml) {
        return XmlUtil.getStringByTagName(xml, "Encrypt");
    }

    @RequestMapping(value = "/authURL", method = RequestMethod.GET)
    public String sendRedirectToAuthUrl(Model model) {
        String preAuthCode = authService.getPreAuthCode();
        String redirectURL = ConfigUtil.getString("wx.redirectURL");
        String componentAppid = ConfigUtil.getString("wx.component_appid");
        model.addAttribute("componentAppid", componentAppid);
        model.addAttribute("preAuthCode", preAuthCode);
        model.addAttribute("redirectURI", redirectURL);
        String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid={componentAppid}&pre_auth_code={preAuthCode}&redirect_uri={redirectURI}";
        return "redirect:" + url;
    }

    @RequestMapping("/auth")
    @ResponseBody
    public AuthorizerInfoResult auth(String auth_code, int expires_in) {
        AuthorizerInfoResult authorizerInfoResult = authService.doAuth(auth_code);
        return authorizerInfoResult;
    }

}
