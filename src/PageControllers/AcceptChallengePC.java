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
import rdg.ChallengeRDG;
import rdg.DeckRDG;
import rdg.GameRDG;
import rdg.HandRDG;
import util.ChallengeStatus;
import util.IdGenerator;

/**
 * Servlet implementation class AcceptChallenge
 */
@WebServlet("/AcceptChallenge")
public class AcceptChallengePC extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcceptChallengePC() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			long userid =-1;
			boolean process = true;
			try{
				userid = (Long)request.getSession(true).getAttribute("userid");
			}
			catch(NullPointerException e){
				request.setAttribute("message", "Need to log in.");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
				return;
			}
			long challengeId = (Long.parseLong(request.getParameter("challenge")));
			ChallengeRDG challenge = ChallengeRDG.find(challengeId);
					
			if(challenge == null){
				request.setAttribute("message", "Challenge was not found");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
				return;
			}
			if(challenge.getChallengee() != userid) {
				request.setAttribute("message", "This is not your challenge.");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
				return;
			}
			if(challenge.getChallenger() == userid) {
				request.setAttribute("message", "Can't accept your own challenge.");
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
				return;
			}
			
			challenge.setStatus(ChallengeStatus.ACCEPTED);
		
			challenge.update();
			DeckRDG deck1 = DeckRDG.findByPlayer(challenge.getChallenger());
			DeckRDG deck2 = DeckRDG.findByPlayer(challenge.getChallengee());
			if(deck1==null){
				request.setAttribute("message", "Challenger doesn't have a deck.");
				if(!response.isCommitted())
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
			}
			else if(deck2 == null){
				request.setAttribute("message", "Challengee doesn't have a deck.");
				if(!response.isCommitted())
				getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
			}
			GameRDG game = new GameRDG(IdGenerator.getInstance().createID(), 
						challenge.getChallenger(), 
						challenge.getChallengee(), 
						deck1.getId(), 
						deck2.getId(),
						"playing",
						"playing");
			game.insert();
			HandRDG hand1 = new HandRDG(0L, 
					game.getId(), 
					game.getPlayer1(),
					0, 
					deck1.getCards().size(), 
					0, IdGenerator.getInstance().createID());
			hand1.insert();
			HandRDG hand2 = new HandRDG(0L, 
					game.getId(), 
					game.getPlayer2(), 
					0, deck2.getCards().size(), 
					0, IdGenerator.getInstance().createID());
			hand2.insert();
			request.setAttribute("message", "Challenge was accepted");
			if(!response.isCommitted())
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/success.jsp").forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("message", "SQL error");
			Connection connection = new DbRegistry().getConnection();
			try {
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			getServletContext().getRequestDispatcher("/WEB-INF/jsp/failure.jsp").forward(request, response);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", e.getMessage());
			if(!response.isCommitted())
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
