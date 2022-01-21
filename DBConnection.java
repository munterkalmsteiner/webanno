package de.tudarmstadt.ukp.clarin.webanno.ui.core.resetPassword.controller;

import java.sql.*;

public class DBConnection {
	private static Connection conn;
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		    conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "viscabarca");

		    System.out.println("Connected!");	
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return conn;
	}
	
//	public static void main(String[] args) {
//        // create a new connection from MySQLJDBCUtil
//        try (Connection conn = DBConnection.getConnection()) {
//
//            // print out a message
//            System.out.println(String.format("Connected to database %s "
//                    + "successfully.", conn.getCatalog()));
//        } catch (SQLException ex) {
//            System.out.println(ex.getMessage());
//        }
//    }
//
}
