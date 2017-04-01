package siege;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.util.*;
import java.text.DecimalFormat;
    
public class Player
{
    private double x, y, xv, yv;
    private static final double r = 15.0;
    
    private double points;
    
    //change this to get cheats
    private static final boolean hasHacks = false;
    
    private static double speed = hasHacks ? 3.0 : 1.2;
    private static double maxSpeed = hasHacks ? Double.MAX_VALUE : r/2.0;
    private double speedRoot2 = (speed / Math.sqrt(2.0));
    
    
    private Direction dir;
    
    private double health;
    public static final double MAX_HEALTH = 100;
    
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        xv = 0.0;
        yv = 0.0;
        
        dir = Direction.UP;
        
        points = 0.0;
        health = MAX_HEALTH;
    }
    public double getPoints() { return points; };
    public void addPoints(double points) { this.points += points; }
    public void resetPoints() { points = 0.0; }
    public double getHealth() { return health; }
    public boolean isDead() { return health <= 0.0 ? true : false; }
    public void setHealth(double h) { health = h; }
    public void changeHealth(double h) { health += h; }
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
    
    public void update() {
        if(xv > maxSpeed) x += maxSpeed;
        else if(xv < -maxSpeed) x -= maxSpeed;
        else x += xv;
        if(yv > maxSpeed) y += maxSpeed;
        else if(yv < -maxSpeed) y -= maxSpeed;
        else y += yv;
    }
    public void moveBack() {
        if(xv > maxSpeed) x -= maxSpeed;
        else if(xv < -maxSpeed) x += maxSpeed;
        else x -= xv;
        if(yv > maxSpeed) y -= maxSpeed;
        else if(yv < -maxSpeed) y += maxSpeed;
        else y -= yv;
    }
    
    public void paint(Graphics g, int camX, int camY) {
        DecimalFormat df = new DecimalFormat("#.##");
        //g.setColor(Color.GRAY);
        //g.fillRect(0, 0, 160, 120);
        //g.setColor(Color.GREEN);
        //g.drawString("player:", 20, 20);
        //g.drawString("x: " + df.format(x), 20, 40);
        //g.drawString("y: " + df.format(y), 20, 60);
        //g.drawString("camX: " + df.format(camX), 20, 80);
        //g.drawString("camY: " + df.format(camY), 20, 100);
        if(health > MAX_HEALTH) health = MAX_HEALTH;
        if(health < 0) health = 0;
        int i = (int)((MAX_HEALTH-health)*2.15);
        g.setColor(new Color(40+i, 215-i, 70));
        g.fillOval((int)x - camX, (int)y - camY, (int)r * 2, (int)r * 2);
        g.setColor(new Color(40, 40, 40));
        int k = 1;
        //g.fillRect((int)x - camX, (int)y - camY, (int)r*2, (int)r*2);
        g.fillOval((int)x-camX+k, (int)y-camY+k, (int)r*2 - k*2, (int)r*2 - k*2);
    } 
    
    public void updateVel(boolean w, boolean a, boolean s, boolean d, int dt) {
        if(w) {
            if(a) {
                xv = -speedRoot2 * dt;
                yv = -speedRoot2 * dt;
                dir = Direction.UP_LEFT;
            } else if(d) {
                xv = speedRoot2 * dt;
                yv = -speedRoot2 * dt;
                dir = Direction.UP_RIGHT;
            } else {
                xv = 0.0;
                yv = -speed * dt;
                dir = Direction.UP;
            }
        } else if(s) {
            if(a) {
                xv = -speedRoot2 * dt;
                yv = speedRoot2 * dt;
                dir = Direction.DOWN_LEFT;
            } else if(d) {
                xv = speedRoot2 * dt;
                yv = speedRoot2 * dt;
                dir = Direction.DOWN_RIGHT;
            } else {
                xv = 0.0;
                yv = speed * dt;
                dir = Direction.DOWN;
            }
        } else if(a) {
            xv = -speed * dt;
            yv = 0.0;
            dir = Direction.LEFT;
        } else if(d) {
            xv = speed * dt;
            yv = 0.0;
            dir = Direction.RIGHT;
        } else {
            xv = 0.0;
            yv = 0.0;
        }
    }
    
}
