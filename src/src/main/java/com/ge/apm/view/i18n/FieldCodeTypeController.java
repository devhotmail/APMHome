package com.ge.apm.view.i18n;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.ge.apm.dao.FieldCodeTypeRepository;
import com.ge.apm.domain.FieldCodeType;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean
@ViewScoped
public class FieldCodeTypeController extends JpaCRUDController<FieldCodeType> {

	private static final long serialVersionUID = 1L;
	FieldCodeTypeRepository dao = null;
	FieldCodeType selectedType = null;
	UserAccount currentUser = null;

	@Override
	protected void init() {
		dao = WebUtil.getBean(FieldCodeTypeRepository.class);
		selectedType = new FieldCodeType();
		this.currentUser = UserContextService.getCurrentUserAccount();
	}

	@Override
	protected FieldCodeTypeRepository getDAO() {
		return dao;
	}

	@Override
	public void onAfterDataChanged() {
		DbMessageSource.reLoadMessages();
	};

	public List<FieldCodeType> getFieldCodeTypeList() {
		return dao.find();
	}

	public FieldCodeType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(FieldCodeType selectedType) {
		this.selectedType = selectedType;
	}
}