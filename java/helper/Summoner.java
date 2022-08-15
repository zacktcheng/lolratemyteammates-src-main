package helper;

public class Summoner {
    private String gameName; // "Yasuo%20Miso"
    ///////////////////////////////////////////////////////////////////////////////////////////
    private String uid;  // "qweqfalknaamcqwekprgfnm"
    private String rank; // "Rank: [DIAMOND I 52 lp]"
    private String summonerId; // "qwtjoqwepjqwprpoqwjrplqwkafotab"
    private String overAllWr; //  "63.24%"
    private String recentWrKda; // "65.0%(Average KDA: 3.86)"
    private int winsTotal; //  4396
    private int lossTotal; //  2200
    private int lossingStreak;
    private boolean dodgeWarning;
    private String dodgeStr;

    public Summoner(String userName){
        gameName = userName;
        uid = "";
        rank = "";
        summonerId = "";
        overAllWr = "";
        recentWrKda = "";
        winsTotal = 0;
        lossTotal = 0;
        dodgeWarning = false;
        lossingStreak = 0;
        dodgeStr = "";
    }
    //public void setGameName(String gameName){this.gameName = gameName;}
    public String toJsonString(){
        return "{"+"\"rank\":"+"\""+rank+"\","+"\"recentWrKda\":"+"\""+recentWrKda+"\","+"\"OverAllWr\":"+"\""+overAllWr+"%\","
                +"\"win/loss\":"+"\""+winsTotal+"/"+lossTotal+"\""+"}";
    }
    public void setUid(String id){
        uid = id;
    }
    public void setRank(String rank){
        this.rank = rank;
    }
    public void setSummonerId(String summonerId){this.summonerId =summonerId;}
    public void setOverAllWr(double overAllWr){
        this.overAllWr = overAllWr+"%";
    }
    public void setRecentWrKda(String recentWr){
        this.recentWrKda = recentWr;
    }
    public void setWinsTotal(int w){winsTotal = w;}
    public void setLossTotal(int l){lossTotal = l;}
    public void setLossingStreak(int s){lossingStreak = s;}
    public void setDodgeWarning(){dodgeWarning = checkDodgeWarning();}
    public void setDodgeStr(){dodgeStr = getDodgeWarning();}
    ///////////////////////////////////////////////////////////////////////////
    public String getGameName(){return gameName;}
    public String getUid(){return uid;}
    public String getRank(){return rank;}
    public String getSummonerId(){return summonerId;}
    public String getOverAllWr(){return overAllWr;}
    public String getRecentWrKda(){return recentWrKda;}
    public int getWinsTotal(){return winsTotal;}
    public int getLossTotal(){return lossTotal;}
    public String getDodgeWarning(){if(dodgeWarning)return "Dodge!!!Toxic Player!!!";return"No need to Dodge";}
    public boolean checkDodgeWarning(){
        int n = 0;
        if(Double.parseDouble(overAllWr.substring(0,4))<45.00)n++;
        if(Double.parseDouble(recentWrKda.substring(0,4))<35.01)n++;
        if(Double.parseDouble(recentWrKda.substring(18,22))<2.5)n++;
        if(lossingStreak>=4)n++;
        return n > 1;
    }
}