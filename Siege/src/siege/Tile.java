package siege;
import java.awt.Color;
import java.awt.Graphics;

public class Tile
{
    private double x, y;
    public static final double w = 25, h = 25;
    private Tile_t type;
    
    public double getX() { return x; }
    public double getY() { return y; }
    public Tile_t getType() { return type; }
    
    public void setType(Tile_t type) { this.type = type; }
    
    public Tile(double x, double y, Tile_t type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void paint(Graphics g, int camX, int camY) {
        if(type == Tile_t.WALL) {
            g.setColor(new Color(10, 10, 10));
            g.fillRect((int)x - camX, (int)y - camY, (int)w, (int)h);
        }else if(type == Tile_t.OPEN) {
            g.setColor(new Color(200, 200, 200));
            g.fillRect((int)x - camX, (int)y - camY, (int)w, (int)h);
        } else {
            g.setColor(new Color(255, 0, 200));
            g.fillRect((int)x - camX, (int)y - camY, (int)w, (int)h);
        }
    }
}
