<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    <ui:composition>

        <p:dialog id="${entityName}EditDlg" widgetVar="${entityName}EditDialog" modal="true" resizable="true" draggable="true" appendTo="@(body)" header="${crudActionName} ${viewTitle}">
            <h:form id="${entityName}EditForm">
                <h:panelGroup id="display">
                    <p:panelGrid columns="2" layout="grid" styleClass="ui-panelgrid-blank form-group" rendered="${selectedNotNull}">
    <#list fieldInfoList as fieldInfo>
                        <h:panelGroup styleClass="md-inputfield">
        <#if fieldInfo.dateTime>
                            <p:calendar id="${fieldInfo.fieldName}" pattern="yyyy/MM/dd HH:mm:ss" value="${fieldInfo.selectedFieldValue}"${fieldInfo.requiredTag} showOn="button" readonlyInput="true" showButtonPanel="true" navigator="true"/>
        <#elseif fieldInfo.booleanField>
                            <p:selectBooleanCheckbox id="${fieldInfo.fieldName}" value="${fieldInfo.selectedFieldValue}"${fieldInfo.requiredTag}/>
        <#elseif fieldInfo.blob>
                            <p:inputTextarea rows="4" cols="30" id="${fieldInfo.fieldName}" value="${fieldInfo.selectedFieldValue}" ${fieldInfo.requiredTag}/>
        <#else>
                            <p:inputText id="${fieldInfo.fieldName}" value="${fieldInfo.selectedFieldValue}" ${fieldInfo.requiredTag}/>
        </#if>
                            <label>${fieldInfo.fieldLabelWithRequiredMark}</label>
                        </h:panelGroup>
</#list>
                    </p:panelGrid>
                    <br/>
                    <p:commandButton validateClient="true" actionListener="${saveListner}" value="${buttonSave}" update="display,:${entityName}EditDlg, :${entityName}ListForm:datalist, :growl" icon="ui-icon-check" oncomplete="handleSubmit(args, '${entityName}EditDialog');"/>
                    &nbsp;&nbsp;
                    <p:commandButton immediate="true" value="${buttonCancel}" icon="ui-icon-cancel" onclick="PF('${entityName}EditDialog').hide()"/>
                </h:panelGroup>
            </h:form>
        </p:dialog>

    </ui:composition>
</html>
