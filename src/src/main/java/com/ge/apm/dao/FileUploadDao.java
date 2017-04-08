/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Repository
@Transactional(readOnly = true)
public class FileUploadDao {

    JdbcTemplate template = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
    static String SQL_SAVE = "insert into file_uploaded(file_name,file_content) values(?,?) ";
    static String SQL_DELETE = "delete from file_uploaded where id=?";
    static String SQL_QUERY = "select file_name,file_content from file_uploaded where id=?";
    static String SQL_QUERY_NAME_BY_ID = "select file_name from file_uploaded where id=?";

    private LobHandler lobHandler;

    public LobHandler getLobHandler() {
        return lobHandler;
    }

    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

    public Integer saveUploadFile(InputStream contentStream, int contentLength, String fileName) throws SQLException {
        Connection con = template.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = null;
        try{
            ps.setString(1, fileName);
            ps.setBinaryStream(2, contentStream, contentLength);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            Integer lastId = rs.getInt(1);
            return lastId;
        }
        finally{
            if(rs!=null) rs.close();
            if(ps!=null) ps.close();
            con.close();
        }
    }

    public int deleteUploadFile(Integer id) {
        return template.update(SQL_DELETE, id);
    }

    public Object[] getAttachmentFile(Integer id) throws SQLException {
        Connection con = template.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_QUERY);
        ResultSet rs = null;
        try{
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next())
                return null;
            InputStream is = rs.getBinaryStream(2);
            return new Object[]{rs.getString(1), is};
        }
        finally{
            if(rs!=null) rs.close();
            if(ps!=null) ps.close();
            con.close();
        }
    }

    public String getFileNameById(Integer fileId) throws SQLException {
        Connection con = template.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_QUERY_NAME_BY_ID);
        ResultSet rs = null;
        try{
            ps.setInt(1, fileId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);
        }
        finally{
            if(rs!=null) rs.close();
            if(ps!=null) ps.close();
            con.close();
        }
    }
}
