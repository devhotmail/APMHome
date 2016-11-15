/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import java.io.InputStream;
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
    static String SQL_SAVE = "insert into file_uploaded2(file_name,file_content) values(?,?) ";
    static String SQL_DELETE = "delete from file_uploaded2 where id=?";
    static String SQL_QUERY = "select file_content from file_uploaded2 where id=?";

    private LobHandler lobHandler;

    public LobHandler getLobHandler() {
        return lobHandler;
    }

    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

    public Integer saveUploadFile(InputStream contentStream, int contentLength, String fileName) throws SQLException {

        PreparedStatement ps = template.getDataSource().getConnection().prepareStatement(SQL_SAVE, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, fileName);
        ps.setBinaryStream(2, contentStream, contentLength);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        Integer lastId = rs.getInt(1);
        ps.close();
        return lastId;
    }

    public int deleteUploadFile(Integer id) {
        return template.update(SQL_DELETE, id);
    }

    public InputStream getAttachmentFile(Integer id) throws SQLException {

        PreparedStatement ps = template.getDataSource().getConnection().prepareStatement(SQL_QUERY);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        InputStream is = rs.getBinaryStream(1);
        return is;

    }

}
