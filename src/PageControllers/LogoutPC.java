package PageControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.DbRegistry;
import rdg.UserRDG;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class LogoutPC extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutPC() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Object userId = request.getSession(true).getAttribute("userid");
			if(userId==null){
				request.setAttribute("message", "There is no one logged in.");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
			}
			else{
				long id = (Long)userId;
				UserRDG u = UserRDG.find(id);
				
				if(u == null){
					request.setAttribute("message", "User was not found with id:" + id);
					getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
				}
				request.getSession(true).invalidate();
				request.setAttribute("message", "User '" + u.getUsername() + "' has been successfully logged out.");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/success.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			request.setAttribute("message", "SQLException");
			Connection connection = new DbRegistry().getConnection();
			try {
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
		}
	}	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
