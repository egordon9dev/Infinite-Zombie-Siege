 package car_game_1_25_17;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.util.*;
import java.text.DecimalFormat;
    
public class Zombie
{
    private double x, y, xv, yv;
    private static final double r = 7;
    private static final double speed = .2;
    private static double maxSpeed;
    private static final double speedRoot2 = (speed / Math.sqrt(2.0));
    private boolean isDead = false;
    private Direction dir;
    private long deathCtr;
    private long deathTime = 0l;
    
    public static double getSpeed() { return speed; }
    public double getX() { return x; }
    public double getY() { return y; }
    public static double getR() { return r; }
    public double getXV() { return xv; }
    public double getYV() { return yv; }
    public Direction getDir() { return dir; }
    public void setXV(double xv) { this.xv = xv; }
    public void setYV(double yv) { this.yv = yv; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDead(boolean b) { isDead = b; }
    public boolean getDead() { return isDead; }
    public long getDeathCtr() { return deathCtr; }
    public void resetDeathCtr() { deathTime = System.currentTimeMillis(); }
    public void updateDeathCtr() { deathCtr = System.currentTimeMillis() - deathTime; }
    
    public Zombie(double x, double y) {
        this.x = x;
        this.y = y;
        xv = 0.0;
        yv = 0.0;
        maxSpeed = (r/2.0);
        dir = Direction.UP;
    }
    
    public void update() {
        if(xv > maxSpeed) x += maxSpeed;
        else if(xv < -maxSpeed) x -= maxSpeed;
        else x += xv;
        if(yv > maxSpeed) y += maxSpeed;
        else if(yv < -maxSpeed) y -= maxSpeed;
        else y += yv;
    }
    
    public void paint(Graphics g, int camX, int camY) {
        g.setColor(new Color(40, 180, 40));
        //g.fillRect((int)x - camX, (int)y - camY, (int)r*2, (int)r*2);
        g.fillOval((int)x - camX, (int)y - camY, (int)r * 2, (int)r * 2);
    } 
    
    public void updateVel(Player player, int dt) {
       double xDist = player.getX() - x;
       double yDist = player.getY() - y;
       
       double dist = Math.pow(Math.pow(xDist, 2) + Math.pow(yDist, 2), 0.5);
       
       /** normalize distance vectors **/
       xDist /= dist;
       yDist /= dist;
       
       double scaledSpeed = speed * dt;
       
       xv = scaledSpeed * xDist;
       yv = scaledSpeed * yDist;
       
    }
    
}
