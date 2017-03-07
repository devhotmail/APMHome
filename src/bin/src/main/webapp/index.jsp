<%@ page contentType="text/html; charset=GBK"%>
<%
String param = "";
if (request.getParameter("opid") != null) {
        param = param + "opid=" + request.getParameter("opid")+"&";
}
if (request.getParameter("hosid") != null) {
        param = param + "ipid=" + request.getParameter("hosid");
}
%>
<html>
    <head>
        <META HTTP-EQUIV="refresh" CONTENT="0;URL=http://192.192.190.110:8880/rissi/web/go?un=itpschina&pw=iischina&uri=/home.xhtml&<%=param%>">
    </head>
</html>
