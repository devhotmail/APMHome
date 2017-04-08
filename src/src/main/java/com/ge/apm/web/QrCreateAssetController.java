package com.ge.apm.web;

import com.ge.apm.service.wechat.QrCreateAssetService;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2017/3/17.
 */
@Controller
public class QrCreateAssetController {

    @Autowired
    WxMpService wxMpService;

    @Autowired
    QrCreateAssetService qrCreateAssetService;

    @RequestMapping(value = "/qrCreateAsset/{openId}", method = RequestMethod.GET )
    public String qrCreateAsset(@PathVariable String openId, HttpServletRequest request, Model model){

        WxJsapiSignature s;

        try {
            s = wxMpService.createJsapiSignature(request.getRequestURL().toString() + "?" + request.getQueryString());
        } catch (WxErrorException ex) {
            Logger.getLogger(QrCreateAssetController.class.getName()).log(Level.SEVERE, null, ex);
            return "asset/qrCreateAsset";
        }

        String qrCode = request.getParameter("qrCode");

        model.addAttribute("openId", openId);
        model.addAttribute("appId", s.getAppid());
        model.addAttribute("timestamp", s.getTimestamp());
        model.addAttribute("nonceStr", s.getNoncestr());
        model.addAttribute("signature", s.getSignature());
        model.addAttribute("qrCode", qrCode);
        model.addAttribute("assetGroupList", qrCreateAssetService.getMsg("assetGroup"));

        return "asset/qrCreateAsset";
    }

    @RequestMapping(value = "/validateQrCode", method = RequestMethod.GET )
    @ResponseBody
    public String validateQrCode(String openId, String qrCode,HttpServletRequest request, HttpServletResponse response) {

        String validateResult = qrCreateAssetService.validateQrCode(openId, qrCode);
        return validateResult;
    }

    @RequestMapping(value = "/submitQrAssetInfo", method = RequestMethod.POST )
    @ResponseBody
    public Boolean submitQrAssetInfo(HttpServletRequest request){

        String openId = request.getParameter("openId");
        String qrCode = request.getParameter("qrCode");
        String voiceServerId = request.getParameter("voiceServerId");
        String imageServerIds = request.getParameter("imageServerIds");
        String uploaderFileBase64 = request.getParameter("uploaderFileBase64");
        String comment = request.getParameter("comment");
        String assetName = request.getParameter("assetName");
        String assetGroupSelect = request.getParameter("assetGroupSelect");
        String orgSelect = request.getParameter("orgSelect");
        String userSelect = request.getParameter("userSelect");

        Boolean result = Boolean.FALSE;
        try {
            result = qrCreateAssetService.createAsset(openId, qrCode, voiceServerId, imageServerIds, uploaderFileBase64, comment, assetName, assetGroupSelect, orgSelect, userSelect);
        } catch (WxErrorException ex) {
            Logger.getLogger(QrCreateAssetController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
        //return "asset/qrResultInfo";
    }

    @RequestMapping(value = "/getOrgList")
    @ResponseBody
    public Object getOrgList(HttpServletRequest request){

        String qrCode = request.getParameter("qrCode");
        return qrCreateAssetService.getOrgList(qrCode);

    }

    @RequestMapping(value = "/getUserList")
    @ResponseBody
    public Object getUserList(HttpServletRequest request){

        String orgIdStr = request.getParameter("orgId");

        try {
            if(orgIdStr != null && !orgIdStr.equals("")){
                return qrCreateAssetService.getUserList(Integer.valueOf(orgIdStr));
            }
        } catch (Exception ex) {
            Logger.getLogger(QrCreateAssetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

}
