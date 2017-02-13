<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    <ui:composition template="/layout/template.xhtml">

        <ui:define name="title">
            <h:outputText value="${viewTitle}"/>
        </ui:define>

        <ui:define name="body">
            <p align="center"><b>${viewTitle}</b></p>
            <h:form id="${entityName}ListForm">
                <p:dataTable id="datalist" value="${lazyModel}" var="item" widgetVar="varDataList"
                    selectionMode="single" selection="${selected}"
                    lazy="true" paginator="true" paginatorPosition="${toolbarPosition}" paginatorAlwaysVisible="true"
                    rowKey="<~item.hashCode()~>" sortMode="single" sortOrder="descending" 
                    paginatorTemplate="{CurrentPageReport} {Toolbar} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} &nbsp;&nbsp;&nbsp;&nbsp; ${rowsPerPage}: {RowsPerPageDropdown}"
                    currentPageReportTemplate="${recordCountLabel}: {totalRecords}"
                    rows="20" emptyMessage="${noRecordFound}"
                    rowsPerPageTemplate="20,40,60,90">

                    <p:ajax event="rowSelect"/>
                    
<#list fieldInfoList as fieldInfo>
    <#if fieldInfo.relationshipOne>
    <#else>
                    <p:column headerText="${fieldInfo.fieldLabel}"${fieldInfo.sortBy}${fieldInfo.filterBy}>
	<#if fieldInfo.dateTime>
                    <#if dataTableEnableFilter>
                        <f:facet name="filter">
                            <p:inputMask mask="9999-99-99 ${msgFilterTo} 9999-99-99" onkeyup="filterByDateField(event, this, 'varDataList', ' ${msgFilterTo} ');" onblur="filterByDateFieldOnBlur(this, ' ${msgFilterTo} ');"/>
                        </f:facet>
                    </#if>
                        <h:outputText value="${fieldInfo.itemFieldValue}">
                            <f:convertDateTime pattern="yyyy/MM/dd HH:mm:ss" />
                        </h:outputText>
	<#elseif fieldInfo.booleanField>
                    <#if dataTableEnableFilter>
                        <f:facet name="filter">
                            <p:selectOneMenu onchange="PF('varDataList').filter()">
                                <f:converter converterId="javax.faces.Boolean" />
                                <f:selectItem itemLabel="${msgAll}" itemValue="" />
                                <f:selectItem itemLabel="${msgTrue2Yes}" itemValue="true" />
                                <f:selectItem itemLabel="${msgFalse2No}" itemValue="false" />
                            </p:selectOneMenu>
                        </f:facet>
                    </#if>
                        <h:outputText value="${fieldInfo.booleanFieldValue}"/>
	<#else>
                        <h:outputText value="${fieldInfo.itemFieldValue}"/>
	</#if>
                    </p:column>
    </#if>
</#list>
                    <f:facet name="{Toolbar}">
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <p:commandButton ${buttonIconTitle}="${exportToXLS}" icon="ui-icon-arrowstop-1-s" ajax="false">
                            <p:dataExporter type="xls" target="datalist" fileName="${entityName}_export"/>
                        </p:commandButton>
                        &nbsp;&nbsp;
                    </f:facet>
                </p:dataTable>
            </h:form>
        </ui:define>
    </ui:composition>
</html>
