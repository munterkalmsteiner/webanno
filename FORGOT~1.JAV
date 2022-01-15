package de.tudarmstadt.ukp.clarin.webanno.ui.core.resetPassword.controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ForgotPass
 */
@WebServlet(name = "ForgotPasswordServlet" , urlPatterns = {"/forgotPassword"})
public class ForgotPasswordServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Connection conn = DBConnection.getConnection();
			
			String email = request.getParameter("email");
			String nPassword = request.getParameter("npass");
			String cPassword = request.getParameter("cpass");
			
			PreparedStatement stmt = conn.prepareStatement("select password from forgotpasswordschema.forgetpassword where email=?");
			stmt.setString(1, email);
			
			ResultSet rs = stmt.executeQuery();
			String currPassword = "";
			if(rs.next()) {
				currPassword = rs.getString(1);
			}else {
				request.setAttribute("Error", "Details for this Email could not be found. Please Register first !!!");
				request.getRequestDispatcher("errorHandler.jsp").forward(request, response);
			}
			
			if(!nPassword.equals(currPassword)) {
				if(nPassword.equals(cPassword)) {
					PreparedStatement ps = conn.prepareStatement("update forgotpasswordschema.forgetpassword set password=? where email=?");
					ps.setString(2, email);
					ps.setString(1, cPassword);
					
					int i = ps.executeUpdate();
					
					if(i > 0) {
						response.sendRedirect("updatedPassword.jsp");
					}else {
//						response.sendRedirect("errorHandler.jsp");
						request.setAttribute("Error", "New Password Could not be updated !!!");
						request.getRequestDispatcher("errorHandler.jsp").forward(request, response);
					}
				}else {
					request.setAttribute("Error", "Entries in New Password and Confirm password fields do not match !!!");
					request.getRequestDispatcher("errorHandler.jsp").forward(request, response);
				}
			}else {
				request.setAttribute("Error", "New Password Cannot be same as Old Password !!!");
				request.getRequestDispatcher("errorHandler.jsp").forward(request, response);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
