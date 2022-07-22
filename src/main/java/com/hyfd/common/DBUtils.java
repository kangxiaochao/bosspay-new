/**
 * 
 */
package com.hyfd.common;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.SQLException; 

/**
 * @author Administrator
 * @date 2016年7月23日上午9:24:38
 */
public class DBUtils {
	
	private static String url = "jdbc:mysql://rm-bp15x3195m158018mo.mysql.rds.aliyuncs.com:3306/bosspaybill?useUnicode=true";
    private static String user = "jfboss";  
    private static String psw = "Yafeida3201";  
      
    private static  Connection conn;  
      
    static {  
        try {  
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
            throw new RuntimeException(e);  
        }  
    }  
      
    private DBUtils() {  
          
    }  
      
    /** 
     * 获取数据库的连接 
     * @return conn 
     */  
    public static Connection getConnection() {  
        if(null == conn) {  
            try {  
                conn = DriverManager.getConnection(url, user, psw);  
            } catch (SQLException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            }  
        }  
        return conn;  
    }  
      
    /** 
     * 释放资源 
     * @param conn 
     * @param pstmt 
     * @param rs 
     */  
    public static void closeResources(Connection conn,PreparedStatement pstmt,ResultSet rs) {  
        if(null != rs) {  
            try {  
                rs.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } finally {  
                if(null != pstmt) {  
                    try {  
                        pstmt.close();  
                    } catch (SQLException e) {  
                        e.printStackTrace();  
                        throw new RuntimeException(e);  
                    } finally {  
                        if(null != conn) {  
                            try {  
                                conn.close();
                                conn = null;
                                DBUtils.conn = null;
                            } catch (SQLException e) {  
                                e.printStackTrace();  
                                throw new RuntimeException(e);  
                            }  
                        }  
                    }  
                }  
            }  
        }  
    }  


	public static void main(String[] args) throws Exception{
		Connection conn = DBUtils.getConnection();
		String sql = "select * from mp_bill_pkg where value in(1,2,5)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			for(int i=1;i<=rs.getMetaData().getColumnCount();i++){
				System.out.print(rs.getString(i)+"\t");
			}
//			System.out.print(rs.getString("id")+"\t");
//			System.out.print(rs.getDouble("price")+"\t");
			System.out.println();
		}
		DBUtils.closeResources(conn, pstmt, rs);
	}

}
