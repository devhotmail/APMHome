<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="http://java.sun.com/jsf/html"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:p="http://primefaces.org/ui"
                template="/WEB-INF/template.xhtml">

    <ui:define name="content">
        <div class="ui-g">
            <div class="ui-g-12">

                <p align="center">
                    <b>${viewTitle}</b>
                </p>
            <h:form id="${entityName}ListForm">
                <p:dataTable id="datalist" value="${lazyModel}" var="item" widgetVar="varDataList"
                    selectionMode="single" selection="${selected}"
                    lazy="true" paginator="true" paginatorPosition="${toolbarPosition}" paginatorAlwaysVisible="true"
                    rowKey="${itemId}" sortMode="single" sortOrder="descending" sortBy="${itemId}"
                    paginatorTemplate="{CurrentPageReport} {Toolbar} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} &nbsp;&nbsp;&nbsp;&nbsp; ${rowsPerPage}: {RowsPerPageDropdown}"
                    currentPageReportTemplate="${recordCountLabel}: {totalRecords}"
                    rows="20" emptyMessage="${noRecordFound}"
                    rowsPerPageTemplate="20,40,60,90">

                    <p:ajax event="rowSelect" update="createButton editButton viewButton deleteButton"/>
                    <p:ajax event="filter" listener="${onFilter}" update="createButton editButton viewButton deleteButton"/>
                    
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
                        <p:commandButton ${buttonIconTitle}="${exportToXLS}" icon="ui-icon-arrowstop-1-s" class="btn btn-sm btn-gray" ajax="false">
                            <p:dataExporter type="xls" target="datalist" fileName="${entityName}_export"/>
                        </p:commandButton>
                        &nbsp;&nbsp;
                        <p:commandButton id="createButton" icon="ui-icon-plus" class="btn btn-sm btn-gray" ${buttonIconTitle}="${buttonCreate}" actionListener="${createListner}" update=":${entityName}EditDlg" oncomplete="PF('${entityName}EditDialog').show()"/>
                        <p:commandButton id="viewButton" icon="ui-icon-search" class="btn btn-sm btn-gray" ${buttonIconTitle}="${buttonView}" actionListener="${viewListner}" update=":${entityName}ViewDlg" oncomplete="PF('${entityName}ViewDialog').show()" disabled="${buttonDisabled}"/>
                        <p:commandButton id="editButton" icon="ui-icon-pencil" class="btn btn-sm btn-gray" ${buttonIconTitle}="${buttonEdit}" actionListener="${editListner}" update=":${entityName}EditDlg" oncomplete="PF('${entityName}EditDialog').show()" disabled="${buttonDisabled}"/>
                        <p:commandButton id="deleteButton" icon="ui-icon-trash" class="btn btn-sm btn-gray" ${buttonIconTitle}="${buttonDelete}" actionListener="${deleteListner}" update=":${entityName}ViewDlg" oncomplete="PF('${entityName}ViewDialog').show()" disabled="${buttonDisabled}"/>
                    </f:facet>
                </p:dataTable>
            </h:form>

            <ui:include src="Edit.xhtml"/>
            <ui:include src="View.xhtml"/>

            </div>
        </div>
        
    </ui:define>

</ui:composition>