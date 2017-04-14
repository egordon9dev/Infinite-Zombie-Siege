package siege;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.util.*;
import java.text.DecimalFormat;

public class Zombie {
    private double x, y, xv, yv;
    private static final double r = 7;
    private static final double speed = 0.1;
    private double maxSpeed;
    private static final double speedRoot2 = (speed / Math.sqrt(2.0));
    private Direction dir;
    private long deathTime = 0l;
    public static final double MAX_HEALTH = 100.0;
    private double health;
    private AStar astar;
    ArrayList<Node> path;
    private boolean genNewPath;
    private int pos;
    //camera position when path is generated
    private int camX0;
    private int camY0;
    //zombie path generation period (ms)
    private static final int PATH_GEN_PERIOD = 1000;
    private long pathGenTime = 0;
    private Point start, end;
    private int numPaths = 0;
    public static double getSpeed() {
        return speed;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public static double getR() {
        return r;
    }
    public double getXV() {
        return xv;
    }
    public double getYV() {
        return yv;
    }
    public Direction getDir() {
        return dir;
    }
    public void setXV(double xv) {
        this.xv = xv;
    }
    public void setYV(double yv) {
        this.yv = yv;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void resetDeathCtr() {
        deathTime = System.currentTimeMillis();
    }
    public long getDeathCt() {
        return System.currentTimeMillis() - deathTime;
    }
    public double getHealth() {
        return health;
    }
    public void setHealth(double n) {
        health = n;
    }
    public void changeHealth(double n) {
        health += n;
        if (health <= 0.0) {
            health = 0.0;
        }
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        }
    }
    public Zombie(double x, double y) {
        this.x = x;
        this.y = y;
        health = MAX_HEALTH;
        xv = 0.0;
        yv = 0.0;
        maxSpeed = r / 2.01;
        dir = Direction.UP;
        genNewPath = true;
        pos = -1;
        camX0 = 0;
        camY0 = 0;
    }
    public void update() {
        if (xv > maxSpeed) {
            x += maxSpeed;
        } else if (xv < -maxSpeed) {
            x -= maxSpeed;
        } else {
            x += xv;
        }
        if (yv > maxSpeed) {
            y += maxSpeed;
        } else if (yv < -maxSpeed) {
            y -= maxSpeed;
        } else {
            y += yv;
        }
    }
    public void paint(Graphics g, int camX, int camY) {
        if (MyUI.DEBUG_GRAPHICS) {
            g.setColor(new Color(255, 50, 50, 150));
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.drawString("z ( " + x + ", " + y + " )", 20, 20);
            drawAStarDebug(g, camX, camY);
        }
        int i = (int) ((MAX_HEALTH - health) * 2.15);
        if (i > 215) {
            i = 215;
        }
        if (MyUI.DEBUG_GRAPHICS) {
            g.setColor(new Color(40 + i, 215 - i, 70, 80));
        } else {
            g.setColor(new Color(40 + i, 215 - i, 70, 180));
        }
        g.fillOval((int) x - camX, (int) y - camY, (int) r * 2, (int) r * 2);
        if (MyUI.DEBUG_GRAPHICS) {
            g.setColor(new Color(40, 180, 40, 80));
        } else {
            g.setColor(new Color(40, 180, 40, 180));
        }
        int k = 1;
        g.fillOval((int) x - camX + k, (int) y - camY + k, (int) r * 2 - k * 2, (int) r * 2 - k * 2);
        if (MyUI.DEBUG_GRAPHICS) {
            g.setColor(new Color(180, 180, 40, 50));
            for (Node ny[] : astar.nodes) {
                for (Node nx : ny) {
                    if(nx.x < 0 || nx.x > MyUI.nWidth || nx.y < 0 || nx.y > MyUI.nHeight) {
                        System.out.println("INVALID NODE");
                    } else {
                        g.fillRect((int)(nx.x + 1), (int)(nx.y + 1), AStar.NODE_SPACE - 2, AStar.NODE_SPACE - 2);
                    }
                }
            }
            g.setColor(new Color(200, 50, 130, 180));
            try {
                g.fillRect((int) start.x * AStar.NODE_SPACE, (int) start.y * AStar.NODE_SPACE, AStar.NODE_SPACE, AStar.NODE_SPACE);
                g.fillRect((int) end.x * AStar.NODE_SPACE, (int) end.y * AStar.NODE_SPACE, AStar.NODE_SPACE, AStar.NODE_SPACE);
                Node current = path.get(pos);
                g.fillRect((int) current.x, (int) current.y, AStar.NODE_SPACE, AStar.NODE_SPACE);
            } catch (Exception e) {
            }
            g.setColor(new Color(200, 50, 130, 70));
            if (path != null) {
                for (Node n : path) {
                    g.fillRect((int) n.x, (int) n.y, AStar.NODE_SPACE, AStar.NODE_SPACE);
                }
            }
        }
    }
    private void drawAStarDebug(Graphics g, int camX, int camY) {
        g.setColor(new Color(180, 180, 180, 150));
        for (Rectangle r : astar.obstacles) {
            g.fillRect(r.x + (camX0 - camX), r.y + (camY0 - camY), r.width, r.height);
        }
        g.setColor(new Color(150, 220, 150, 150));
        if (path != null) {
            for (Node n : path) {
                g.fillRect((int) n.x + (camX0 - camX), (int) n.y + (camY0 - camY), AStar.NODE_SPACE, AStar.NODE_SPACE);
            }
        }
        g.setColor(new Color(110, 110, 255, 200));
        if (astar.start != null) {
            g.fillRect((int) astar.start.x + (camX0 - camX), (int) astar.start.y + (camY0 - camY), AStar.NODE_SPACE, AStar.NODE_SPACE);
        }
        if (astar.end != null) {
            g.fillRect((int) astar.end.x + (camX0 - camX), (int) astar.end.y + (camY0 - camY), AStar.NODE_SPACE, AStar.NODE_SPACE);
        }
        g.setColor(new Color(150, 60, 255, 140));
        try {
            Node current = path.get(pos);
            g.fillRect((int) current.x + (camX0 - camX), (int) current.y + (camY0 - camY), AStar.NODE_SPACE, AStar.NODE_SPACE);
        } catch (Exception e) {
        }
    }
    public void updateVel(Player player, int camX, int camY, ArrayList<ArrayList<Tile>> tiles, int dt) {
        final int deltaCamX = camX0 - camX;
        final int deltaCamY = camY0 - camY;
        if (genNewPath) {
            MyUI.writeLog("attempting to generate a new path");
            astar = new AStar(camX, camY);
            pos = -1;
            //--------------------------------------------------------------------------------------------------------------//
            //-------------------------------------      ZOMBIE   :   START      -------------------------------------------//
            //--------------------------------------------------------------------------------------------------------------//
            start = new Point((int) (x + r - camX) / AStar.NODE_SPACE, (int) (y + r - camY) / AStar.NODE_SPACE);
            Rectangle zColRect = new Rectangle((int) start.x * AStar.NODE_SPACE, (int) start.y * AStar.NODE_SPACE, AStar.NODE_SPACE, AStar.NODE_SPACE);
            for (Rectangle o : astar.obstacles) {
                if (astar.isCol(o, zColRect)) {
                    outer:
                    for (int i = 0; i < 8; i++) {
                        switch (i) {
                            case 0:
                                start = new Point(start.x - 1, start.y);
                                break;
                            case 1:
                                start = new Point(start.x - 1, start.y + 1);
                                break;
                            case 2:
                                start = new Point(start.x, start.y + 1);
                                break;
                            case 3:
                                start = new Point(start.x + 1, start.y + 1);
                                break;
                            case 4:
                                start = new Point(start.x + 1, start.y);
                                break;
                            case 5:
                                start = new Point(start.x + 1, start.y - 1);
                                break;
                            case 6:
                                start = new Point(start.x, start.y - 1);
                                break;
                            case 7:
                                start = new Point(start.x - 1, start.y - 1);
                                break;
                        }
                        zColRect.x = (int) start.x * AStar.NODE_SPACE;
                        zColRect.y = (int) start.y * AStar.NODE_SPACE;
                        for (Rectangle w : astar.obstacles) {
                            if (astar.isCol(w, zColRect)) {
                                continue outer;
                            }
                        }
                        //success
                        break;
                    }
                    break;
                }
            }
            //--------------------------------------------------------------------------------------------------------------//
            //----------------------------------      PLAYER   :   END      ------------------------------------------------//
            //--------------------------------------------------------------------------------------------------------------//
            end = new Point((int) ((player.getX() + player.getR() - camX) / AStar.NODE_SPACE), (int) ((player.getY() + player.getR() - camY) / AStar.NODE_SPACE));
            Rectangle pColRect = new Rectangle((int) end.x * AStar.NODE_SPACE, (int) end.y * AStar.NODE_SPACE, AStar.NODE_SPACE, AStar.NODE_SPACE);
            for (Rectangle o : astar.obstacles) {
                if (astar.isCol(o, pColRect)) {
                    outer:
                    for (int i = 0; i < 8; i++) {
                        switch (i) {
                            case 0:
                                end = new Point(end.x - 1, end.y);
                                break;
                            case 1:
                                end = new Point(end.x - 1, end.y + 1);
                                break;
                            case 2:
                                end = new Point(end.x, end.y + 1);
                                break;
                            case 3:
                                end = new Point(end.x + 1, end.y + 1);
                                break;
                            case 4:
                                end = new Point(end.x + 1, end.y);
                                break;
                            case 5:
                                end = new Point(end.x + 1, end.y - 1);
                                break;
                            case 6:
                                end = new Point(end.x, end.y - 1);
                                break;
                            case 7:
                                end = new Point(end.x - 1, end.y - 1);
                                break;
                        }
                        pColRect.x = (int) end.x * AStar.NODE_SPACE;
                        pColRect.y = (int) end.y * AStar.NODE_SPACE;
                        for (Rectangle w : astar.obstacles) {
                            if (astar.isCol(w, pColRect)) {
                                continue outer;
                            }
                        }
                        //success
                        break;
                    }
                    break;
                }
            }
            boolean hasClearPath = true;
            for (Rectangle o : astar.obstacles) {
                if (astar.isCol(o, zColRect) || astar.isCol(o, pColRect)) {
                    hasClearPath = false;
                    break;
                }
            }
            if (hasClearPath) {
                MyUI.writeLog("start/end nodes are good");
                try {
                    if (astar.run(player, start, end, camX, camY)) {
                        path = new ArrayList<Node>();
                        Node n = astar.end;
                        path.add(n);
                        while (astar.cameFrom.containsKey(n)) {
                            n = astar.cameFrom.get(n);
                            path.add(n);
                        }
                        camX0 = camX;
                        camY0 = camY;
                        genNewPath = false;
                        pathGenTime = System.currentTimeMillis();
                        numPaths++;
                        MyUI.writeLog("SUCCESS #" + numPaths + ": path of length "
                                + path.size() + " generated");
                    } else {
                        MyUI.writeLog("A* failed to find path. the path may be blocked.");/*
                        double dist = Integer.MAX_VALUE;
                        double zx = x + r;
                        double zy = y + r;
                        for(int i = 0; i < path.size(); i++) {
                            double d = Math.sqrt(Math.pow((path.get(i).x + AStar.NODE_SPACE/2)-zx, 2) + Math.pow((path.get(i).y + AStar.NODE_SPACE/2)-zy, 2));
                            if(d < dist) {
                                dist = d;
                                pos = i;
                            }
                        }*/
                    }
                } catch (InvalidStartNodeException e) {
                    MyUI.writeLog(e.getMessage());
                    xv = 0;
                    yv = 0;
                    System.out.println("ERROR: start node out of bounds");
                } catch (InvalidEndNodeException e) {
                    MyUI.writeLog(e.getMessage());
                    xv = 0;
                    yv = 0;
                    System.out.println("ERROR: end node out of bounds");
                }
            } else {
                MyUI.writeLog("UH OH. the path is not clear. UH OH.");
            }
        }
        //follow path
        if (path != null && (pos > 1 || pos == -1)) {
            MyUI.writeLog("zombie is attempting to follow the generated path");
            if (path.size() <= 1) {
                MyUI.writeLog("ERROR: invalid PATH. cannot update zombie movement" + System.currentTimeMillis());
                genNewPath = true;
                return;
            }
            if (pos == -1) {
                pos = path.size() - 1;
            }
            MyUI.writeLog("pos = " + pos + "\tzombie:" + "\t(" + (x - camX) + ", " + (y - camY) + ")"
                    + "\tnode:" + "\t(" + path.get(pos).x + ", " + path.get(pos).y + ")");
            double n1x, n1y, d1x, d1y, magd1;
            final double zx = x + r - camX;
            final double zy = y + r - camY;
            do {
                Node current = path.get(pos);
                //node 1 position
                n1x = current.x + deltaCamX + AStar.NODE_SPACE / 2;
                n1y = current.y + deltaCamY + AStar.NODE_SPACE / 2;
                //distance vector
                d1x = zx - n1x;
                d1y = zy - n1y;
                magd1 = Math.sqrt(Math.pow(d1x, 2) + Math.pow(d1y, 2));
                double magV = Math.sqrt(Math.pow(xv, 2) + Math.pow(yv, 2));
                xv = speed * dt * (-d1x / magV);
                yv = speed * dt * (-d1y / magV);
                if (magd1 < AStar.NODE_SPACE) {
                    pos--;
                }
            } while (magd1 < AStar.NODE_SPACE && pos > 0);
        } else { //straight line path
            MyUI.writeLog("zombie is using straight line path");
            double xDist = x - player.getX();
            double yDist = y - player.getY();
            double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
            // normalize distance vectors
            xDist /= dist;
            yDist /= dist;
            double scaledSpeed = speed * dt;
            xv = scaledSpeed * -xDist;
            yv = scaledSpeed * -yDist;
            genNewPath = true;
        }
        if (System.currentTimeMillis() - pathGenTime >= PATH_GEN_PERIOD) {
            genNewPath = true;
        }
        MyUI.writeLog("\n");
    }
}
class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node n1, Node n2) {
        if (n1.fScore < n2.fScore) {
            return -1;
        }
        if (n1.fScore > n2.fScore) {
            return 1;
        }
        return 0;
    }
}
class Node {
    public double x;
    public double y;
    public double gScore;
    public double fScore;
    public Node() {
        gScore = 9999999.99;
        fScore = 9999999.99;
    }
    public Rectangle toRect() {
        return new Rectangle((int) x, (int) y, AStar.NODE_SPACE, AStar.NODE_SPACE);
    }
    public Point toPoint() {
        return new Point((int) x + AStar.NODE_SPACE / 2, (int) y + AStar.NODE_SPACE / 2);
    }
}
class InvalidStartNodeException extends Exception {
    InvalidStartNodeException(String msg) {
        super(msg);
    }
}
class InvalidEndNodeException extends Exception {
    InvalidEndNodeException(String msg) {
        super(msg);
    }
}
class AStar {
    public static final int NODE_SPACE = 30;
    public final int GRID_WIDTH;
    public final int GRID_HEIGHT;
    public Node nodes[][];
    public Node start, end;
    public HashMap<Node, Node> cameFrom;
    public ArrayList<Rectangle> obstacles;
    public AStar(int camX, int camY) {
        GRID_WIDTH = (MyUI.getWorld().getMap().getTiles().get(0).size() * (int) Tile.w) / NODE_SPACE;
        GRID_HEIGHT = (MyUI.getWorld().getMap().getTiles().size() * (int) Tile.h) / NODE_SPACE;
        cameFrom = new HashMap<Node, Node>();
        updateObstacles(camX, camY);
        updateNodes();
    }
    private double dist(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n2.x - n1.x, 2.0) + Math.pow(n2.y - n1.y, 2.0));
        //return Math.abs(n2.x- n1.x) + Math.abs(n2.y - n1.y);
    }
    public boolean isCol(Rectangle a, Rectangle b) {
        if (a.x + a.width <= b.x || a.x >= b.x + b.width
                || a.y + a.height <= b.y || a.y >= b.y + b.height) {
            return false;
        }
        return true;
    }
    public boolean isCol(Point a, Circ b) {
        if (Math.sqrt(Math.pow((b.x + b.r) - a.x, 2) + Math.pow((b.y + b.r) - a.y, 2)) <= b.r) {
            return true;
        }
        return false;
    }
    public void updateObstacles(int camX, int camY) {
        int numPrevWalls = 0;
        obstacles = new ArrayList<Rectangle>();
        for (ArrayList<Tile> i : MyUI.getWorld().getMap().getTiles()) {
            for (int j = 0; j < i.size(); j++) {
                if (i.get(j).getType() == Tile_t.WALL) {
                    if (j == i.size() - 1) {
                        obstacles.add(new Rectangle((int) i.get(j - numPrevWalls).getX() - camX, (int) i.get(j).getY() - camY, (int) i.get(j).w * (numPrevWalls + 1), (int) i.get(j).h));
                        numPrevWalls = 0;
                    } else {
                        numPrevWalls++;
                    }
                } else if (numPrevWalls > 0) {
                    obstacles.add(new Rectangle((int) i.get(j - numPrevWalls).getX() - camX, (int) i.get(j).getY() - camY, (int) i.get(j).w * numPrevWalls, (int) i.get(j).h));
                    numPrevWalls = 0;
                }
            }
        }
    }
    public void updateNodes() {
        nodes = new Node[GRID_HEIGHT][GRID_WIDTH];
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                nodes[i][j] = new Node();
                nodes[i][j].x = j * NODE_SPACE;
                nodes[i][j].y = i * NODE_SPACE;
            }
        }
    }
    public boolean run(Player player, Point pStart, Point pEnd, int camX, int camY) throws InvalidStartNodeException, InvalidEndNodeException {
        updateObstacles(camX, camY);
        updateNodes();
        cameFrom = new HashMap<Node, Node>();
        try {
            start = nodes[(int) pStart.y][(int) pStart.x];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidStartNodeException("\n\n\n\n\nInvalid Start Node Exception: location ( "
                    + (int) pStart.x + ", " + (int) pStart.x + " )\t" + "max size ( "
                    + nodes[0].length + ", " + nodes.length + " )\n\n\n\n\n");
        }
        try {
            end = nodes[(int) pEnd.y][(int) pEnd.x];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidEndNodeException("\n\n\n\n\nInvalid End Node Exception: location ( "
                    + (int) pEnd.y + ", " + (int) pEnd.x + " )" + "max size ( "
                    + nodes[0].length + ", " + nodes.length + " )\n\n\n\n\n");
        }
        start.gScore = 0;
        start.fScore = dist(start, end);
        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> openSet = new PriorityQueue<Node>(10, comparator);
        openSet.add(start);
        ArrayList<Node> unwalkable;
        MyUI.writeLog("astar running");
        //int ctr = 0;
        while (openSet.size() > 0) {
            //MyUI.writeLog("loop "+ctr);
            //ctr++;
            unwalkable = new ArrayList<Node>();
            Node current = openSet.remove();
            if (isCol(current.toPoint(), new Circ(player.getX()-camX, player.getY()-camX, player.getR()))) {
                return true;
            }
            ArrayList<Node> neighbors = new ArrayList<Node>();
            int centerX = (int) (current.x / NODE_SPACE);
            int centerY = (int) (current.y / NODE_SPACE);
            int left = centerX - 1;
            int top = centerY - 1;
            int right = centerX + 1;
            int bottom = centerY + 1;
            Node nCL = null, nTC = null, nCR = null, nBC = null,
                    nTL = null, nTR = null, nBR = null, nBL = null;
            boolean initCL = false, initCR = false, initTC = false, initBC = false;
            try {
                nCL = nodes[centerY][left];
                neighbors.add(nCL);
                initCL = true;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                nTC = nodes[top][centerX];
                neighbors.add(nTC);
                initTC = true;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                nCR = nodes[centerY][right];
                neighbors.add(nCR);
                initCR = true;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                nBC = nodes[bottom][centerX];
                neighbors.add(nBC);
                initBC = true;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            if (initCL && initTC) {
                nTL = nodes[top][left];
                neighbors.add(nTL);
            }
            if (initCR && initTC) {
                nTR = nodes[top][right];
                neighbors.add(nTR);
            }
            if (initCR && initBC) {
                nBR = nodes[bottom][right];
                neighbors.add(nBR);
            }
            if (initCL && initBC) {
                nBL = nodes[bottom][left];
                neighbors.add(nBL);
            }
            for (Node n : neighbors) {
                for (Rectangle o : obstacles) {
                    if (isCol(o, n.toRect())) {
                        unwalkable.add(n);
                    }
                }
            }
            boolean addTL = false, addTR = false, addBR = false, addBL = false;
            for (int i = 0; i < unwalkable.size(); i++) {
                Node n = unwalkable.get(i);
                if (initCL && n.equals(nCL)) {
                    addTL = true;
                    addBL = true;
                }
                if (initCR && n.equals(nCR)) {
                    addTR = true;
                    addBR = true;
                }
                if (initTC && n.equals(nTC)) {
                    addTL = true;
                    addTR = true;
                }
                if (initBC && n.equals(nBC)) {
                    addBL = true;
                    addBR = true;
                }
            }
            if (nTL != null && addTL) {
                unwalkable.add(nTL);
            }
            if (nTR != null && addTR) {
                unwalkable.add(nTR);
            }
            if (nBR != null && addBR) {
                unwalkable.add(nBR);
            }
            if (nBL != null && addBL) {
                unwalkable.add(nBL);
            }
            outer:
            for (int i = 0; i < neighbors.size(); i++) {
                Node n = neighbors.get(i);
                for(Node q : unwalkable) {
                    if(q.equals(n))
                    continue outer;
                }
                double tentative_gScore = current.gScore + dist(current, n);
                //not a better path
                if (tentative_gScore >= n.gScore) {
                    continue;
                }
                //it's good. save it
                n.gScore = tentative_gScore;
                n.fScore = n.gScore + dist(n, end);
                openSet.add(n);
                cameFrom.put(n, current);
            }
        }
        return true;
    }
}
