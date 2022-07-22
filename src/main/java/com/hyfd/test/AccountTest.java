package com.hyfd.test;

import com.hyfd.common.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class AccountTest {

    public static void main(String[] args){
        int level = 2;
        String sql = "SELECT DISTINCT parent_id from mp_agent where `level` = " + level +";";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("parent_id"));
                
            }
        } catch (Exception e) {
            System.err.println("获取运营商信息发生异常");
        } finally {
            DBUtils.closeResources(conn, pstmt, rs);
        }
    }

}
