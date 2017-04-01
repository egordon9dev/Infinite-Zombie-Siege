package siege;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.util.*;

public class Bullet
{
    private double x, y, xv, yv;
    private static double r;
    
    private double speed = 6.0;
    private double speedRoot2 = speed / Math.sqrt(2.0);
    private long startTime;
    private int damage;
    public Bullet(double x, double y, Direction dir) {
        this.x = x;
        this.y = y;
        this.damage = 10;
        r = 2.0;
        if(dir == Direction.UP) {
            xv = 0.0;
            yv = -speed;
        } else if(dir == Direction.UP_RIGHT) {
            xv = speedRoot2;
            yv = -speedRoot2;
        } else if(dir == Direction.RIGHT) {
            xv = speed;
            yv = 0.0;
        } else if(dir == Direction.DOWN_RIGHT) {
            xv = speedRoot2;
            yv = speedRoot2;
        } else if(dir == Direction.DOWN) {
            xv = 0.0;
            yv = speed;
        } else if(dir == Direction.DOWN_LEFT) {
            xv = -speedRoot2;
            yv = speedRoot2;
        } else if(dir == Direction.LEFT) {
            xv = -speed;
            yv = 0.0;
        } else if(dir == Direction.UP_LEFT) {
            xv = -speedRoot2;
            yv = -speedRoot2;
        }
        startTime = System.currentTimeMillis();
    }
    
    public long getDeltaTime() { return System.currentTimeMillis() - startTime; }
    public double getX() { return x; }
    public double getY() { return y; }
    public static double getR() { return r; }
    public double getXV() { return xv; }
    public double getYV() { return yv; }
    public double getDamage() { return damage; }
    
    public void setXV(double xv) { this.xv = xv; }
    public void setYV(double yv) { this.yv = yv; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public static void setR(double r) { Bullet.r = r; }
    
    public void update() {
        x += xv;
        y += yv;
    }
    public void paint(Graphics g, double camX, double camY) {
        g.setColor(new Color(40, 40, 40));
        g.fillOval((int)(x - camX), (int)(y - camY), (int)(r * 2.0), (int)(r * 2.0));
    }
}