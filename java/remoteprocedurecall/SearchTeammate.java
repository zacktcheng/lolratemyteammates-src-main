package remoteprocedurecall;
import org.json.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.*;
import helper.Commons;
import helper.Summoner;

/**
 * Servlet implementation class SearchTeammate
 */
public class SearchTeammate extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final int MAX_USERNAME_COUNT = 5;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchTeammate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		JSONArray usernames = new JSONArray();		
		String usernameTag = "username";
		String usernameValue = request.getParameter(usernameTag);
		
		//////////////////////////////////////////		
        ArrayList<String> playerList = Commons.getPlayerList(usernameValue);
        long start1 = System.currentTimeMillis();
        JSONArray jarray = new JSONArray();
        for(String playerName: playerList){
            String playerNameFix = playerName.replace(" ", "%20");
            Summoner summoner1 = new Summoner(playerNameFix);
            Commons.processPlayer(summoner1);
            JSONObject obj = new JSONObject();
            obj.put("playerName",summoner1.getGameName().replace("%20", " "));
            obj.put("rank",summoner1.getRank());
            obj.put("recentWrKda",summoner1.getRecentWrKda());
            obj.put("overAllWr",summoner1.getOverAllWr());
            obj.put("win/loss",summoner1.getWinsTotal()+"/"+summoner1.getLossTotal());
            obj.put("dodgeWarning",summoner1.checkDodgeWarning());
            jarray.put(obj);
        }
        long end1 = System.currentTimeMillis();
        double TotalTime = (end1-start1)/1000.0;
        String Times = TotalTime + " Seconds";
        writer.print(jarray);
        System.out.println(Times);
        //writer.print("Time used: "+ TotalTime +" seconds");
	}
}
