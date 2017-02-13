<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    <ui:composition>

        <p:dialog id="${entityName}ViewDlg" widgetVar="${entityName}ViewDialog" modal="true" resizable="true" draggable="true" appendTo="@(body)" header="${crudActionName} ${viewTitle}">
            <h:form id="${entityName}ViewForm">
                <h:panelGroup id="display">
                    <p:panelGrid columns="2" rendered="${selectedNotNull}">
<#list fieldInfoList as fieldInfo>
<#if fieldInfo.relationshipOne>
<#else>
                        <h:outputLabel value="${fieldInfo.fieldLabel}" for="${fieldInfo.fieldName}" />
    <#if fieldInfo.dateTime>
                        <h:outputText value="${fieldInfo.selectedFieldValue}">
                            <f:convertDateTime pattern="yyyy/MM/dd HH:mm:ss" />
                        </h:outputText>
    <#elseif fieldInfo.booleanField>
                        <p:selectBooleanCheckbox id="${fieldInfo.fieldName}" value="${fieldInfo.selectedFieldValue}"${fieldInfo.requiredTag} disabled="true"/>
    <#else>
                        <h:outputText value="${fieldInfo.selectedFieldValue}"/>
    </#if>
</#if>
</#list>
                    </p:panelGrid>
                    <br/>
                    <p:commandButton rendered="${renderDeleteButton}" icon="ui-icon-trash" actionListener="${deleteAction}" value="${buttonDelete}" update="display,:${entityName}ViewDlg, :${entityName}ListForm:datalist, :growl" oncomplete="handleSubmit(args, '${entityName}ViewDialog');"/>
                    &nbsp;&nbsp;
                    <p:commandButton immediate="true" value="${buttonClose}" icon="glyphicon-off" onclick="PF('${entityName}ViewDialog').hide()"/>
                    
                </h:panelGroup>
            </h:form>
        </p:dialog>

    </ui:composition>
</html>
