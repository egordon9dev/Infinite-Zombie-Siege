package siege;


/**
 *
 * @author ethan
 */
public class RoundHandler {
    private int round;
    private long startT;
    public static final long TOTAL_TIME = 45000000;
    public boolean timeOut;
    
    public RoundHandler() {
        round = 1;
        startT = System.currentTimeMillis();
    }
    public void incRound() {
        round++;
        startT = System.currentTimeMillis();
    }
    public void start() {
        startT = System.currentTimeMillis();
    }
    public long getTime() {
        return System.currentTimeMillis() - startT;
    }
    public int getRound() {
        return round;
    }
    public double getReqPts() {
        return 50 * Math.pow(round, 5.0);
    }
    public boolean isTimeOut() {
        return timeOut;
    }
    public void setTimeOut() {
        timeOut = true;
    }
}