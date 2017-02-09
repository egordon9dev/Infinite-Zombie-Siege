package car_game_1_25_17;
public final class Collisions
{
    private Collisions(){}

    public static boolean rectRect(Rect rect1, Rect rect2) {
        if(rect1.x + rect1.w < rect2.x  ||
        rect1.x > rect2.x + rect2.w ||
        rect1.y + rect1.h < rect2.y ||
        rect1.y > rect2.y + rect2.h) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean circTile(Circ circ, Tile tile) {
        Rect rect = new Rect(tile.getX(), tile.getY(), Tile.w, Tile.h);

        //if circle isn't in the corner
        /*if(circ.x >= rect.x && circ.x <= rect.x + rect.w &&
        circ.y >= rect.y && circ.y <= rect.y + rect.h) {*/
        //if circle isn't outside rectangle
        if(!(circ.x + 2*circ.r < rect.x ||
            circ.x > rect.x + rect.w ||
            circ.y + 2*circ.r < rect.y ||
            circ.y > rect.y + rect.h)) {
            double cx = circ.x + circ.r;
            double cy = circ.y + circ.r;

            int TOP = 0, BOTTOM = 1, RIGHT = 2, LEFT = 3;
            double[] offsets = new double[4];
            offsets[TOP] = rect.y - cy;
            offsets[BOTTOM] = cy - (rect.y + rect.h);
            offsets[RIGHT] = cx - (rect.x + rect.w);
            offsets[LEFT] = rect.x - cx;
            int maxI = 0;
            for(int i = 1; i < 4; i++) {
                if(offsets[i] < 0) offsets[i] = 0;
                if(offsets[i] > offsets[maxI]) maxI = i;
            }
            if(maxI == TOP) {
                circ.y = rect.y - 2*circ.r;
            } else if(maxI == BOTTOM) {
                circ.y = rect.y + rect.h;
            } else if(maxI == RIGHT) {
                circ.x = rect.x + rect.w;
            } else if(maxI == LEFT) {
                circ.x = rect.x - 2*circ.r;
            }
            
           
            return true;
        } else {
            return false;
        }/*
        }else {
        double r2 = Math.pow(circ.r, 2);
        double xLeft = Math.pow(rect.x - circ.x, 2);
        double xRight = Math.pow(circ.x-(rect.x + rect.w), 2);
        double yTop = Math.pow(rect.y - circ.y, 2);
        double yBottom = Math.pow(circ.y - (rect.y + rect.h), 2);
        //pyth. thm.
        if(xLeft + yTop <= r2 || xRight + yTop <= r2 || xRight + yBottom <= r2 || xLeft + yBottom <= r2 ) {
        return true;
        } else {
        return false;
        }
        }*/
    }
    //circ2 gets moved out of the way of circ1     d = c1    r = c2
    public static boolean circCirc(Circ circ1, Circ circ2) {
        double cx1 = circ1.x + circ1.r;
        double cy1 = circ1.y + circ1.r;
        
        double cx2 = circ2.x + circ2.r;
        double cy2 = circ2.y + circ2.r;
        //distance between centers
        double dx = cx2-cx1;
        double dy = cy2-cy1;
        
        double dist = Math.pow( Math.pow(dx, 2)+Math.pow(dy, 2), 0.5 );
        double newDist = circ1.r + circ2.r;
        if( dist < newDist) {
            double r = newDist / dist;
            
            circ2.x = circ1.x + (dx * r);
            circ2.y = circ1.y + (dy * r);
            
            return true;
        } else {
            return false;
        }
    }

    public static boolean bulletTile(Bullet bullet, Tile tile) {
        Circ circ = new Circ(bullet.getX(), bullet.getY(), bullet.getR());
        Rect rect = new Rect(tile.getX(), tile.getY(), Tile.w, Tile.h);

        //if circle isn't outside rectangle
        if(!(circ.x + 2*circ.r < rect.x ||
            circ.x > rect.x + rect.w ||
            circ.y + 2*circ.r < rect.y ||
            circ.y > rect.y + rect.h)) {
            return true;
        } else {
            return false;
        }
    }
}