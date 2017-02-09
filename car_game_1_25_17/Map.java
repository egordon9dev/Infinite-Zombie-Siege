package car_game_1_25_17;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
public class Map
{
    //number of extra tiles to load outside of the visible window
    private ArrayList<ArrayList<Tile>> tiles;
    //private ArrayList<Tile> deletedTiles;
    private ArrayList<Point> deletedTilePts;

    private int extraLoad = 1;
    private SimplexNoise_octave simplexNoise;
    public Map(int windowW, int windowH) {
        deletedTilePts = new ArrayList<Point>();
        tiles = new ArrayList<ArrayList<Tile>>();
        for(int y = 0; y < (windowH / Tile.h) + 1 + (2*extraLoad); y++) {
            tiles.add(new ArrayList<Tile>());
            for(int x = 0; x < (windowW / Tile.w) + 1+ (2*extraLoad); x++) {
                tiles.get(y).add(null);
            }
        }

        simplexNoise = new SimplexNoise_octave(1245123);
    }

    public void deleteTilePt(Point pt) {
        deletedTilePts.add(pt);
    }

    public void update(int windowW, int windowH, int startX, int startY) {
        double k = 0.001;
        for(int y = 0; y < (windowH / Tile.h) + 1 + (2*extraLoad); y++) {
            for(int x = 0; x < (windowW / Tile.w) + 1 + (2*extraLoad); x++) {
                int xPos = (int)( (startX - startX%Tile.w) + (x - extraLoad)*Tile.w );
                int yPos = (int)( (startY - startY%Tile.h) + (y - extraLoad)*Tile.h );

                double xNoise = xPos*k;
                double yNoise = yPos*k;
                if( simplexNoise.noise( xNoise, yNoise) > .3) {
                    tiles.get(y).set(x, new Tile(xPos, yPos, Tile_t.WALL));
                } else {
                    tiles.get(y).set(x, new Tile(xPos, yPos, Tile_t.OPEN));
                }

            }
        }
        for(Point p : deletedTilePts) {
            if(p == null) break;
            
            int xCoord = (int)((p.x-(startX-startX%Tile.w)) / Tile.w) + extraLoad;
            if (xCoord < 0) xCoord = 0;
            if (xCoord > (int)( (windowW / Tile.w) + (2*extraLoad) )) xCoord = (int)( (windowW / Tile.w) + (2*extraLoad) );
            
            int yCoord = (int)((p.y-(startY-startY%Tile.h)) / Tile.h) + extraLoad;
            if(yCoord < 0) yCoord = 0;
            if(yCoord > (int)( (windowH / Tile.h) + (2*extraLoad) )) yCoord = (int)( (windowH / Tile.h) + (2*extraLoad) );
            
            tiles.get(yCoord).get(xCoord).setType(Tile_t.OPEN);
        }
    }
    //get list so the user can modify it
    public ArrayList<ArrayList<Tile>> getTiles() { return tiles; }

    public void paint(Graphics g, int camX, int camY) {
        for(ArrayList<Tile> i : tiles) {
            for(Tile j : i) {
                j.paint(g, camX, camY);
            }
        }
        g.setColor(Color.GRAY);
        g.fillRect(0, 200, 100, 30);
        g.setColor(Color.GREEN);
        g.drawString("del. tiles: " + deletedTilePts.size(), 0, 220);
        //paint each tile and check tile type to know whether or not to color it in
    }

    
    
    double CosineInterpolate(double a, double b, double x)
    {
        double f = (1 - Math.cos(x * Math.PI)) * .5;

        return (a * (1 - f)) + b * f;
    }

    double Noise(int nOctave, int x, int y)
    {
        if (nOctave == 1)
        {
            int n = x + y * 57;
            n = (n << 13) ^ n;
            return (1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
        }
        if (nOctave == 2)
        {
            int n = x + y * 13;
            n = (n << 17) ^ n;
            return (1.0 - ((n * (n * n * 6113 + 8629) + 4599271) & 0x11fffffffl) / 2927419459.0);
        }
        if (nOctave == 3)
        {
            int n = x + y * 17;
            n = (n << 11) ^ n;
            return (1.0 - ((n * (n * n * 6961 + 2713) + 1456703) & 0x17fffffffl) / 39801473.0);
        }
        return -99;
    }

    double SmoothNoise(int nOctave, double x, double y)
    {
        double corners = (Noise(nOctave, (int)x - 1, (int)y - 1) + Noise(nOctave, (int)x + 1, (int)y - 1) + Noise(nOctave, (int)x - 1, (int)y + 1) + Noise(nOctave, (int)x + 1, (int)y + 1)) / 16;
        double sides = (Noise(nOctave, (int)x - 1, (int)y) + Noise(nOctave, (int)x + 1, (int)y) + Noise(nOctave, (int)x, (int)y - 1) + Noise(nOctave, (int)x, (int)y + 1)) / 8;
        double center = Noise(nOctave, (int)x, (int)y) / 4;
        return corners + sides + center;

    }

    double InterpolatedNoise(int nOctave, double x, double y)
    {
        int integer_X = (int)(x);
        double fractional_X = x - integer_X;

        int integer_Y = (int)(y);
        double fractional_Y = y - integer_Y;

        double v1 = SmoothNoise(nOctave, integer_X, integer_Y);
        double v2 = SmoothNoise(nOctave, integer_X + 1, integer_Y);
        double v3 = SmoothNoise(nOctave, integer_X, integer_Y + 1);
        double v4 = SmoothNoise(nOctave, integer_X + 1, integer_Y + 1);

        double i1 = CosineInterpolate(v1, v2, fractional_X);
        double i2 = CosineInterpolate(v3, v4, fractional_X);

        return CosineInterpolate(i1, i2, fractional_Y);
    }

    double GenerateNoise_2D(double x, double y)
    {
        double total = 0;
        int p = 2;
        int n = 2; //Number_Of_Octaves - 1

        for (int i = 0; i < n; ++i)
        {
            int frequency = (int)Math.pow(2, i);
            int amplitude = (int)Math.pow(p, i);

            total = total + (InterpolatedNoise(i+1, x * frequency, y * frequency) * amplitude);
        }
        return total;
    }
}
