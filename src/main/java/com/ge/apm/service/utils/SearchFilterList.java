package com.ge.apm.service.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.dao.SearchFilter.Operator;

/**
 *
 * @author 212560881
 */
public class SearchFilterList {

    public enum RELATION_TYPE {
        AND, OR
    }

    private List<SearchFilter> andFilter;
    private List<SearchFilter> orFilter;

    public SearchFilter addSearchFilter(String tableAlias, String filed, Operator op, Object value, RELATION_TYPE type) {
        if (RELATION_TYPE.AND == type) {
            SearchFilter sf = new SearchFilter(filed, op, value);
            sf.tableAlias = tableAlias;
            andFilter.add(sf);
            return sf;
        }

        if (RELATION_TYPE.OR == type) {
            SearchFilter sf = new SearchFilter(filed, op, value);
            sf.tableAlias = tableAlias;
            orFilter.add(sf);          
            return sf;
        }
        return null;
    }

    public SearchFilterList() {
        andFilter = new ArrayList<SearchFilter>();
        orFilter = new ArrayList<SearchFilter>();
    }

    public List<SearchFilter> getAndFilter() {
        return andFilter;
    }

    public void setAndFilter(List<SearchFilter> andFilter) {
        this.andFilter = andFilter;
    }

    public List<SearchFilter> getOrFilter() {
        return orFilter;
    }

    public void setOrFilter(List<SearchFilter> orFilter) {
        this.orFilter = orFilter;
    }

    private String getAndSql() {
        String andSql = "";
        StringBuilder andBuilder = new StringBuilder();
        for (SearchFilter filter : andFilter) {
            andBuilder.append("(");
            andBuilder.append(filter.getConditionSql());
            //andBuilder.append(getFilterSql(filter)); 
            andBuilder.append(")");
            andBuilder.append(" and ");
        }
        andSql = andBuilder.toString().trim();
        if (!"".equals(andSql)) {
            andSql = andSql.substring(0, andSql.length() - 3);
        }

        return andSql;
    }

    private String getOrSql() {
        String orSql = "";
        StringBuilder orBuilder = new StringBuilder();
        for (SearchFilter filter : orFilter) {
            orBuilder.append("(");
            orBuilder.append(filter.getConditionSql());
            orBuilder.append(")");
            orBuilder.append(" or ");
        }
        orSql = orBuilder.toString().trim();
        if (!"".equals(orSql)) {
            orSql = orSql.substring(0, orSql.length() - 3);
        }

        return orSql;
    }

    public String getConditionSql() {
        String result = "";
        List<String> conditions = new ArrayList<String>();
        
        String andSql = getAndSql();
        if(StringUtils.isNotBlank(andSql)){
            conditions.add(andSql);
        }
        
        String orSql = getOrSql();
        if(StringUtils.isNotBlank(orSql)){
            conditions.add(String.format("(%s)",orSql));
        }
           
        result = StringUtils.join(conditions, " and ");
        if(StringUtils.isNotBlank(result)){
            result = String.format(" where %s", result);
        }
        return result;
    }

    public List<String> getTablesAbrr() {
        List<String> tablesAbbr = new ArrayList<String>();
        for (SearchFilter filter : andFilter) {
            String table = filter.tableAlias;
            tablesAbbr.add(table);
        }

        for (SearchFilter filter : orFilter) {
            String table = filter.tableAlias;
            tablesAbbr.add(table);
        }

        return tablesAbbr;
    }

    private String getFilterSql(SearchFilter sf) {
        String sql = "";
        String opSign = "";
        String value = sf.value.toString();
        if (sf.operator == SearchFilter.Operator.GT) {
            opSign = ">";
        } else if (sf.operator == SearchFilter.Operator.GTE) {
            opSign = ">=";
        } else if (sf.operator == SearchFilter.Operator.LT) {
            opSign = "<";
        } else if (sf.operator == SearchFilter.Operator.LTE) {
            opSign = "<=";
        } else if (sf.operator == SearchFilter.Operator.LIKE) {
            opSign = "like";
            value ="'%" + value + "%'";
        } else if (sf.operator == SearchFilter.Operator.IN) {
            opSign = "in";
        } else if(sf.operator == SearchFilter.Operator.EQ) {
            opSign = "=";
            value ="'" + value + "'";
        }
        else {
            return "";
        }
        sql = String.format("%s.%s %s %s", sf.tableAlias, sf.fieldName, opSign, value);
        return sql;
    }
}
