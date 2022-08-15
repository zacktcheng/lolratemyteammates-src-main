package helper;
import java.io.*;
import java.util.*;
import java.net.*;

public class Commons {
    public static final String ApiKey = "RGAPI-9e020b40-dfd4-4238-87ff-f4d7c915e5c4";
    private static HttpURLConnection connection;
    public static void main(String[] args) throws IOException {
//        ArrayList<String> playerList = getPlayerList();
//        long start1 = System.currentTimeMillis();
//        for(String playerName: playerList){
//            String playerNameFix = playerName.replace(" ", "%20");
//            Summoner summoner1 = new Summoner(playerNameFix);
//            processPlayer(summoner1);
//            System.out.println(summoner1.toJsonString());
//            System.out.println(generateOutput(summoner1));
//        }
//        long end1 = System.currentTimeMillis();
//        double TotalTime = (end1-start1)/1000.0;
//        System.out.println("Time used: "+ TotalTime +" seconds");
    }
	public static void processPlayer(Summoner summoner1) throws IOException {
        String uid = getUid(summoner1.getGameName());
        ArrayList<String> playerMatches1 = getMatches(uid);
        String[] arr0 = getWinRate(playerMatches1,uid);
        String winRateKda = arr0[0];
        int losingStreak = Integer.parseInt(arr0[1]);
        String id = getSummonerId(uid);
        String[] arr = getRankAndOverallWinRate(id);//0-rank 1-OverAllwinrate 2-win 3-loss
        summoner1.setUid(uid);
        summoner1.setRank(arr[0]);
        summoner1.setSummonerId(id);
        summoner1.setRecentWrKda(winRateKda);
        summoner1.setLossingStreak(losingStreak);
        summoner1.setOverAllWr(Double.parseDouble(arr[1]));
        summoner1.setWinsTotal(Integer.parseInt(arr[2]));
        summoner1.setLossTotal(Integer.parseInt(arr[3]));
        summoner1.setDodgeWarning();
    }

    public static String generateOutput(Summoner s){
        String tab = "";
        String fakeName = s.getGameName().replace("%20"," ");
        int t = 16-fakeName.length();
        for(int i=0; i<t; i++) tab += " ";
        return  "Player name: ["+s.getGameName().replace("%20"," ")+"]"+tab+ " Recent 20 games win rate: "
                +"["+ s.getRecentWrKda() +"] "+s.getRank()+" OverAll win rate: "+s.getOverAllWr()+"% "+s.getWinsTotal()+"/"+s.getLossTotal() +"(win/lose)";
    }
    public static ArrayList<String> getPlayerList(String names){
        ArrayList<String> list = new ArrayList<>();
        Scanner sc2 = new Scanner(names);
        String str = "";
        boolean check = false;
        while(sc2.hasNext()){
            String temp = sc2.next();
            if(temp.equals("joined")) {
                list.add(str);
                str = "";
                sc2.next();
                sc2.next();
                check = false;
            }else if(check){
                str = str+" "+temp;
            }else {
                str = temp;
                check = true;
            }
        }
        return list;
    }

    public static String getSummonerId(String uid) throws IOException {
        String id;
        String api4 = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/";
        String api = api4+uid+"?api_key="+ApiKey;
        id = requestStr(api);
        String temp = "";
        for(int i=7;i<150;i++){
            char a = id.charAt(i);
            if(a == '"') break;
            temp += a;
        }
        return temp;
    }

    public static String[] getRankAndOverallWinRate(String id) throws IOException {
        String rank ="";int win=0;int loss =0;double wr;
        String api5 = "https://na1.api.riotgames.com/lol/league/v4/entries/by-summoner/";
        String api = api5+id+"?api_key="+ApiKey;
        String temp = requestStr(api);
        int index = temp.indexOf("RANKED_SOLO_5x5");
        temp = temp.substring(index);
        int i = 17;
        for(int k=25;k<50;k++){
            if(temp.charAt(k)=='"') {
                i = k;
                break;
            }
            rank = rank + temp.charAt(k);
        }
        i+=10; rank += " ";
        while(true){
            if(temp.charAt(i)=='"') break;
            rank += temp.charAt(i);
            i++;
        }
        rank +=" "; int count = 0;
        while(count != 11){
            if(temp.charAt(i) == '"') count++;
            i++;
        }
        i++;
        while(temp.charAt(i)!= ','){
            rank += temp.charAt(i);
            i++;
        }
        i += 8; rank+=" lp";
        String winstr = ""; String lossstr ="";
        while(temp.charAt(i) !=','){
            winstr += temp.charAt(i);
            i++;
        }
        i += 10;
        while(temp.charAt(i)!= ','){
            lossstr += temp.charAt(i);
            i++;
        }
        win = Integer.parseInt(winstr);
        loss = Integer.parseInt(lossstr);
        wr = 100.0*win/(win+loss);
        wr = Math.round(wr*100.0)/100.0;
        String wrstr = String.valueOf(wr);
        String[] arr = {rank,wrstr,winstr,lossstr};
        return arr;
        //return rank + "overAll win rate: ["+wr+"%] "+win+"/"+loss+"(win/loss)";
    }

    public static String[] getWinRate(ArrayList<String> match,String uid) throws IOException {
        double win=0;double loss=0; int gameNumber = 20;  double kda=0; int losingStreak = 0; int tempLoss=0;
        String api3 = "https://americas.api.riotgames.com/lol/match/v5/matches/";
        for(int i=0;i<gameNumber;i++){
            String game = match.get(i);
            String api = api3+game+"?api_key="+ApiKey;
            String content = requestStr(api);
            int index = content.lastIndexOf(uid);
            String T = content.substring(index-4000,index);
            int n = T.indexOf("\"kda\"");
            String temp2 = T.substring(n,n+20);
            String Totalkda="";
            for(int m=0;m<10;m++){
                if(temp2.charAt(m+6) == ',') break;
                Totalkda += temp2.charAt(m+6);
            }
            //System.out.println(Totalkda);
            kda+=Double.parseDouble(Totalkda);
            String temp = content.substring(index);
            int windex = temp.indexOf("win");
            String temp3 = temp.substring(windex,windex+20);
            if(temp3.contains("true")){
                win++;
                losingStreak = Math.max(losingStreak,tempLoss);
                tempLoss = 0;
            }else{
                loss++;
                tempLoss++;
            }
        }
        kda = kda /(win+loss);
        kda = Math.round(kda*100.0)/100.0;
        double rate = 100.0*win/(win+loss);
        String recentWrKda = String.valueOf(rate)+"%"+"(Average KDA: "+kda+")";
        return new String[]{recentWrKda,String.valueOf(losingStreak)};
    }

    public static ArrayList<String> getMatches(String uid) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String api2 = "https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/";
        String api = api2 +uid+"/ids?type=ranked&start=0&count=20"+"&api_key="+ApiKey;
        String content = requestStr(api);
        for(int i=0;i<20;i++){
            String temp = content.substring(2+17*i,2+17*i+14);
            list.add(temp);
        }
        return list;
    }

    public static String getUid(String playerName) throws IOException{
        String uid;
        String api1 = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
        String api = api1+playerName+"?api_key="+ApiKey;
        uid = requestStr(api);
        int index = uid.indexOf("puuid")+8;
        uid = uid.substring(index,index+78);
        return uid;
    }

    public static String requestStr(String inputUrl) throws IOException {
        BufferedReader reader;
        String line; String output;
        StringBuilder responseContent = new StringBuilder(); //StringBuffer responseContent = new StringBuffer();
        URL url = new URL(inputUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null){
            responseContent.append(line);
        }
        output = responseContent.toString();
        reader.close();
        connection.disconnect();
        return output;
    }
}
