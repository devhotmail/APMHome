package com.ge.apm.service.asset;

import com.ge.apm.dao.BiomedGroupRepository;
import com.ge.apm.domain.BiomedGroup;
import webapp.framework.web.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Created by 212605082 on 2017/5/16.
 */
@FacesConverter("biomedGroupConverter")
public class BiomedGroupConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value != null && value.trim().length() > 0) {
            try {
                BiomedGroupRepository biomedGroupDao = WebUtil.getBean(BiomedGroupRepository.class);
                return biomedGroupDao.findById(Integer.parseInt(value));
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Integer."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if(object != null) {
            return String.valueOf(((BiomedGroup) object).getId());
        }
        else {
            return null;
        }
    }
}
