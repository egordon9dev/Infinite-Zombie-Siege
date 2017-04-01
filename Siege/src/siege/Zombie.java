package siege;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;
import java.util.*;
import java.text.DecimalFormat;

public class Zombie {

    private double x, y, xv, yv;
    private int pos;
    private static final double r = 7;
    private static final double speed = 0.1;
    private static double maxSpeed;
    private static final double speedRoot2 = (speed / Math.sqrt(2.0));
    private Direction dir;
    private long deathTime = 0l;
    public static final double MAX_HEALTH = 100.0;
    private double health;
    private AStar astar = new AStar();
    ArrayList<Node> lastGoodPath;

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
        pos = -1;
        maxSpeed = (r / 2.0);
        dir = Direction.UP;
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
        int i = (int) ((MAX_HEALTH - health) * 2.15);
        if (i > 215) {
            i = 215;
        }
        g.setColor(new Color(40 + i, 215 - i, 70));
        g.fillOval((int) x - camX, (int) y - camY, (int) r * 2, (int) r * 2);
        g.setColor(new Color(40, 180, 40));
        int k = 1;
        g.fillOval((int) x - camX + k, (int) y - camY + k, (int) r * 2 - k * 2, (int) r * 2 - k * 2);
        g.setColor(new Color(255, 130, 130, 190));
        for(Rectangle r : astar.obstacles) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        g.setColor(new Color(130, 130, 255, 190));
        int r = 20;
        if(astar.start != null) g.fillOval((int)astar.start.x + (AStar.NODE_SPACE/2) - r, (int)astar.start.y + (AStar.NODE_SPACE/2) - r, 2*r, 2*r);
        if(astar.end != null) g.fillOval((int)astar.end.x + (AStar.NODE_SPACE/2) - r, (int)astar.end.y + (AStar.NODE_SPACE/2) - r, 2*r, 2*r);
        g.setColor(new Color(130, 255, 130, 190));
        if(lastGoodPath != null) {
            for(Node n : lastGoodPath) {
                g.fillRect((int) n.x + k, (int) n.y + k, AStar.NODE_SPACE, AStar.NODE_SPACE);
            }
        }
    }

    public void updateVel(Player player, int camX, int camY, ArrayList<ArrayList<Tile>> tiles, int dt) {
        /*
        double xDist = x - player.getX();
        double yDist = y - player.getY();

        double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));

        // normalize distance vectors
        xDist /= dist;
        yDist /= dist;

        double scaledSpeed = speed * dt;

        xv = scaledSpeed * -xDist;
        yv = scaledSpeed * -yDist;
         */
        
        astar = new AStar();
        Point start = new Point(((int)x - camX)/AStar.NODE_SPACE,  ((int)y - camY)/AStar.NODE_SPACE);
        Point end = new Point(((int)player.getX() - camX)/AStar.NODE_SPACE,  ((int)player.getY() - camY)/AStar.NODE_SPACE);
        if(astar.run(start, end, camX, camY)) {
            lastGoodPath = new ArrayList<Node>();
            Node n = astar.end;
            lastGoodPath.add(n);
            while (astar.cameFrom.containsKey(n)) {
                n = astar.cameFrom.get(n);
                lastGoodPath.add(n);
            }
            pos = -1;
        } else {
            System.out.println("failure to find path");
            xv = 0;
            yv = 0;
        }
        if(lastGoodPath != null) {
            if (pos == -1) {
                pos = lastGoodPath.size() - 1;
            }
            if (pos > 0) {
                Node current = lastGoodPath.get(pos);
                Node next = lastGoodPath.get(pos - 1);

                xv = (next.x - current.x);
                yv = (next.y - current.y);
                double mag = Math.sqrt(Math.pow(xv, 2) + Math.pow(yv, 2));
                xv *= (speed*dt) / mag;
                yv *= (speed*dt) / mag;

                if (x >= next.x && y >= next.y) {
                    pos--;
                }
            }
        }
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

    public boolean walkable;
    public double x;
    public double y;
    public double gScore;
    public double fScore;

    public Node() {
        walkable = true;
        gScore = 9999999;
        fScore = 9999999;
    }

    public Node(boolean b) {
        this();
        walkable = b;
    }

    public Rectangle toRect() {
        return new Rectangle((int) x, (int) y, AStar.NODE_SPACE, AStar.NODE_SPACE);
    }

    public boolean isCloseTo(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }

    @Override
    public boolean equals(Object o) {
        Node n = (Node) o;
        return isCloseTo(n.x, x) && isCloseTo(n.y, y);
    }
}

class AStar {
    public boolean finished = false;
    public static final int NODE_SPACE = (int)Zombie.getR() * 2;;
    public final int GRID_WIDTH;
    public final int GRID_HEIGHT;
    public Node nodes[][];
    public Node start, end;
    public HashMap<Node, Node> cameFrom;
    public ArrayList<Rectangle> obstacles;
    
    public AStar() {
        GRID_WIDTH = (MyUI.getWorld().getMap().getTiles().get(0).size()*(int)Tile.w)/NODE_SPACE;
        GRID_HEIGHT = (MyUI.getWorld().getMap().getTiles().size()*(int)Tile.h)/NODE_SPACE;
        nodes = new Node[GRID_HEIGHT][GRID_WIDTH];
        cameFrom = new HashMap<Node, Node>();
        obstacles = new ArrayList<Rectangle>();
    }
    
    private double dist(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n2.x - n1.x, 2.0) + Math.pow(n2.y - n1.y, 2.0));
        //return Math.abs(n2.x- n1.x) + Math.abs(n2.y - n1.y);
    }

    private boolean isCol(Rectangle a, Rectangle b) {
        if (a.x + a.width <= b.x || a.x >= b.x + b.width
                || a.y + a.height <= b.y || a.y >= b.y + b.height) {
            return false;
        }
        return true;
    }

    public boolean run(Point pStart, Point pEnd, int camX, int camY) {
        int numPrevWalls = 0;
        for(ArrayList<Tile> i : MyUI.getWorld().getMap().getTiles()) {
            for(int j = 0; j < i.size(); j++) {
                if(i.get(j).getType() == Tile_t.WALL) {
                    if(j == i.size() - 1) {
                        obstacles.add(new Rectangle((int)i.get(j-numPrevWalls).getX() - camX, (int)i.get(j).getY() - camY, (int)i.get(j).w * (numPrevWalls+1), (int)i.get(j).h));
                        numPrevWalls = 0;
                    } else {
                        numPrevWalls++;
                    }
                } else if(numPrevWalls > 0) {
                    obstacles.add(new Rectangle((int)i.get(j-numPrevWalls).getX() - camX, (int)i.get(j).getY() - camY, (int)i.get(j).w * numPrevWalls, (int)i.get(j).h));
                    numPrevWalls = 0;
                }
            }
        }
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                nodes[i][j] = new Node();
                nodes[i][j].x = j * NODE_SPACE;
                nodes[i][j].y = i * NODE_SPACE;
            }
        }
        try {
            start = nodes[(int)pStart.y][(int)pStart.x];
            end = nodes[(int)pEnd.y][(int)pEnd.x];
        } catch(Exception e) {
            return false;
        }
        start.gScore = 0;
        start.fScore = dist(start, end);
        Comparator<Node> comparator = new NodeComparator();
        PriorityQueue<Node> openSet = new PriorityQueue<Node>(10, comparator);
        openSet.add(start);
        int loop_ctr = 0;
        while (openSet.size() > 0) {

            Node current = openSet.remove();
            if (current == end) {
                finished = true;
                return true;
            }
            ArrayList<Node> neighbors = new ArrayList<Node>();
            Node leftNode = new Node(false);
            Node topLeftNode = new Node(false);
            Node topNode = new Node(false);
            Node topRightNode = new Node(false);
            Node rightNode = new Node(false);
            Node bottomRightNode = new Node(false);
            Node bottomNode = new Node(false);
            Node bottomLeftNode = new Node(false);
            boolean hasLeft = current.x >= NODE_SPACE;
            boolean hasTop = current.y >= NODE_SPACE;
            boolean hasRight = current.x <= (nodes[0].length - 2) * NODE_SPACE;
            boolean hasBottom = current.y <= (nodes.length - 2) * NODE_SPACE;
            int centerX = (int) current.x / NODE_SPACE;
            int centerY = (int) current.y / NODE_SPACE;
            int left = centerX - 1;
            int top = centerY - 1;
            int right = centerX + 1;
            int bottom = centerY + 1;

            if (hasLeft) {
                leftNode = nodes[centerY][left];
                neighbors.add(leftNode);
            }
            if (hasLeft && hasTop) {
                topLeftNode = nodes[top][left];
                neighbors.add(topLeftNode);
            }
            if (hasTop) {
                topNode = nodes[top][centerX];
                neighbors.add(topNode);
            }
            if (hasTop && hasRight) {
                topRightNode = nodes[top][right];
                neighbors.add(topRightNode);
            }
            if (hasRight) {
                rightNode = nodes[centerY][right];
                neighbors.add(rightNode);
            }
            if (hasRight && hasBottom) {
                bottomRightNode = nodes[bottom][right];
                neighbors.add(bottomRightNode);
            }
            if (hasBottom) {
                bottomNode = nodes[bottom][centerX];
                neighbors.add(bottomNode);
            }
            if (hasBottom && hasLeft) {
                bottomLeftNode = nodes[bottom][left];
                neighbors.add(bottomLeftNode);
            }

            for (Node n : neighbors) {
                for (Rectangle o : obstacles) {
                    if (isCol(o, n.toRect())) {
                        n.walkable = false;
                    }
                }
            }
            if (!leftNode.walkable) {
                topLeftNode.walkable = false;
                bottomLeftNode.walkable = false;
            }
            if (!topNode.walkable) {
                topLeftNode.walkable = false;
                topRightNode.walkable = false;
            }
            if (!rightNode.walkable) {
                topRightNode.walkable = false;
                bottomRightNode.walkable = false;
            }
            if (!bottomNode.walkable) {
                bottomLeftNode.walkable = false;
                bottomRightNode.walkable = false;
            }

            //System.out.println("loop " + loop_ctr);
            //loop_ctr++;
            for (int i = 0; i < neighbors.size(); i++) {
                Node n = neighbors.get(i);
                if (!n.walkable) {
                    n.walkable = true;
                    continue;
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
        finished = true;
        return false;
    }
}
