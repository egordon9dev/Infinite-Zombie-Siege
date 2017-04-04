package siege;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.*;//ArrayList;
import javax.swing.KeyStroke;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class MyWorld extends JPanel{

    public static Player player;
    private Map map;

    public Map getMap() {
        return map;
    }

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Zombie> zombies = new ArrayList<Zombie>();

    //private boolean keyW = false, keyS = false, keyA = false, keyD = false, keySpace;
    private int nWidth, nHeight;
    private int laggedCamX = 0, laggedCamY = 0;
    private long startTimeUpdate = System.currentTimeMillis();
    private int deltaTimeUpdate = 20;
    private final int NUM_OF_ZOMBIES = 1;
    /* drunk mode (camera delay)  --    ratio of weight of new camera : weight of old camera
     * 1.0 = very responsive
     * 0.x = delayed
     * 0.0 = doesn't move
     */
    private final double drunkMode = 1 - Math.pow(.93, Player.getSpeed() + 1);
    //MyKeyListener myKeyListener;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    static JLabel label = new JLabel();
    private boolean keyW = false, keyA = false, keyS = false, keyD = false, keySpace = false;
    private static final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right", SPACE = "space",
            UP_RELEASE = "up release", DOWN_RELEASE = "down release", RIGHT_RELEASE = "right release", LEFT_RELEASE = "left release",
            SPACE_RELEASE = "space release";

    private boolean running = false;

    public static final RoundHandler round = new RoundHandler();

    public void setRunning(boolean b) {
        running = b;
    }

    MyWorld() {
        //myKeyListener = new MyKeyListener();
        //addKeyListener(myKeyListener);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), UP);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), DOWN);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), LEFT);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), RIGHT);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), SPACE);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), UP_RELEASE);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), DOWN_RELEASE);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), LEFT_RELEASE);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), RIGHT_RELEASE);
        this.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), SPACE_RELEASE);

        this.getActionMap().put(UP, new ActionMove(UP));
        this.getActionMap().put(DOWN, new ActionMove(DOWN));
        this.getActionMap().put(LEFT, new ActionMove(LEFT));
        this.getActionMap().put(RIGHT, new ActionMove(RIGHT));
        this.getActionMap().put(SPACE, new ActionMove(SPACE));
        this.getActionMap().put(UP_RELEASE, new ActionMove(UP_RELEASE));
        this.getActionMap().put(DOWN_RELEASE, new ActionMove(DOWN_RELEASE));
        this.getActionMap().put(LEFT_RELEASE, new ActionMove(LEFT_RELEASE));
        this.getActionMap().put(RIGHT_RELEASE, new ActionMove(RIGHT_RELEASE));
        this.getActionMap().put(SPACE_RELEASE, new ActionMove(SPACE_RELEASE));

        setFocusable(true);
        setBackground(Color.GRAY);
        setDoubleBuffered(true);

        nWidth = MyUI.nWidth;
        nHeight = MyUI.nWidth;

        player = new Player(400, nHeight / 2.0);

        map = new Map(nWidth, nHeight);
        map.update(nWidth, nHeight, 0, 0);
/*
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    update();
                }
            }
        });
        t1.start();*/
        //setPreferredSize(new Dimension(600,600));
        Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                update();
                repaint();
            }
        });
        timer.start();
    }

    
    private class ActionMove extends AbstractAction {

        String dir;

        ActionMove(String dir) {

            this.dir = dir;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!running) {
                round.start();
            }
            if (dir.equals(UP)) {
                keyW = true;
            } else if (dir.equals(DOWN)) {
                keyS = true;
            } else if (dir.equals(LEFT)) {
                keyA = true;
            } else if (dir.equals(RIGHT)) {
                keyD = true;
            } else if (dir.equals(SPACE)) {
                keySpace = true;
                if (player.getDir() == Direction.UP || player.getDir() == Direction.DOWN) {
                    for (int i = 0; i < round.getRound(); i++) {
                        bullets.add(new Bullet(player.getX() + player.getR() - Bullet.getR() + 6 * (i - round.getRound() / 2),
                                player.getY() + player.getR() - Bullet.getR(), player.getDir()));
                    }
                } else {
                    for (int i = 0; i < round.getRound(); i++) {
                        bullets.add(new Bullet(player.getX() + player.getR() - Bullet.getR(),
                                player.getY() + player.getR() - Bullet.getR() + 6 * (i - round.getRound() / 2), player.getDir()));
                    }
                }
            } else if (dir.equals(UP_RELEASE)) {
                keyW = false;
            } else if (dir.equals(DOWN_RELEASE)) {
                keyS = false;
            } else if (dir.equals(LEFT_RELEASE)) {
                keyA = false;
            } else if (dir.equals(RIGHT_RELEASE)) {
                keyD = false;
            } else if (dir.equals(SPACE_RELEASE)) {
                keySpace = false;
            }
            running = true;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        map.paint(g, laggedCamX, laggedCamY);

        for (Bullet b : bullets) {
            b.paint(g, laggedCamX, laggedCamY);
        }
        for (Zombie z : zombies) {
            if (z.getHealth() <= 0.0) {
                continue;
            }
            z.paint(g, laggedCamX, laggedCamY);
        }
        int i;
        player.paint(g, laggedCamX, laggedCamY);
        DecimalFormat df = new DecimalFormat("#.##");
        for (int q = 0; q < 2; q++) {
            if (q == 0) {
                i = 0;
                g.setColor(new Color(0, 0, 0));
            } else {
                i = 2;
                g.setColor(new Color(255, 80, 80));
            }
            g.setFont(new Font("SansSerif", Font.BOLD, 27));
            g.drawString("health: " + df.format(player.getHealth()) + " / " + player.MAX_HEALTH, 300 - i, 35);
            g.drawString("points: " + (int) (player.getPoints()) + " / "
                    + round.getReqPts(), 300 - i, 75);
            g.drawString("Round " + round.getRound(), nWidth - 175 - i, nHeight - 350);
            g.drawString("" + ((round.TOTAL_TIME - round.getTime()) / 1000), nWidth - 175 - i, nHeight - 325);
            if (player.getHealth() < 100) {
                g.setFont(new Font("SansSerif", Font.BOLD, 45));
                g.drawString("DANGER", 100 - i, 70);
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public void update() {
        if (!running) {
            return;
        }
        if (player.isDead()) {
            MyUI.showEnd();
            return;
        }

        deltaTimeUpdate = (int) (System.currentTimeMillis() - startTimeUpdate);
        startTimeUpdate = System.currentTimeMillis();

        if (player.getHealth() < player.MAX_HEALTH) {
            player.changeHealth(deltaTimeUpdate * 0.0025);
            if (player.getHealth() > player.MAX_HEALTH) {
                player.setHealth(player.MAX_HEALTH);
            }
        }

        if (round.getTime() >= round.TOTAL_TIME) {
            if (player.getPoints() > round.getReqPts()) {
                round.incRound();
                player.resetPoints();
                player.setHealth(player.MAX_HEALTH);
            } else {
                player.setHealth(0.0);
                round.setTimeOut();
            }
        }

        player.updateVel(keyW, keyA, keyS, keyD, deltaTimeUpdate);

        if (zombies.size() < NUM_OF_ZOMBIES) {
            zombies.add(new Zombie(Math.random() * (nWidth - 2 * Zombie.getR()) + player.getX() - nWidth / 2.0 + player.getR(), Math.random() * (nHeight - 2 * Zombie.getR()) + player.getY() - nHeight / 2.0 + player.getR()));
            //zombies.add(new Zombie(player.getX()+50, player.getY()+50));
        }

        map.update(nWidth, nHeight, laggedCamX, laggedCamY);
        ArrayList<ArrayList<Tile>> tiles = map.getTiles();

        for (Zombie z : zombies) {
            z.updateVel(player, laggedCamX, laggedCamY, tiles, deltaTimeUpdate);
            z.update();
        }

        player.update();
        //Circ playerCirc = new Circ(player.getX(), player.getY(), player.getR());

        /* put camera through a low-pass filter so that it doesn't make sudden movements cuz
         * that hurts my eyeballs.  */
        laggedCamX = (int) ((1 - drunkMode) * laggedCamX + drunkMode * (player.getX() + player.getR() - (nWidth / 2.0)));
        laggedCamY = (int) ((1 - drunkMode) * laggedCamY + drunkMode * (player.getY() + player.getR() - (nHeight / 2.0)));

        //handle bullet collisions with tiles
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext();) {
            boolean deleteThisBullet = false;
            Bullet b = iterator.next();
            for (ArrayList<Tile> i : tiles) {
                for (Tile j : i) {
                    if (j.getType() == Tile_t.WALL) {
                        if (Collisions.bulletTile(b, j)) {
                            deleteThisBullet = true;
                            map.deleteTilePt(new Point(j.getX(), j.getY()));
                        }
                    }
                }
            }
            if (deleteThisBullet) {
                iterator.remove();
            }
        }

        for (Iterator<Zombie> iterator = zombies.iterator(); iterator.hasNext();) {
            Zombie z = iterator.next();
            if (Math.abs(z.getX() - player.getX()) > nWidth * 0.66 || Math.abs(z.getY() - player.getY()) > nHeight * 0.66) {
                iterator.remove();
            }
        }
        //check zombie collision with bullets
        for (Iterator<Zombie> itZ = zombies.iterator(); itZ.hasNext();) {
            Zombie z = itZ.next();
            if (z.getHealth() <= 0.0) {
                if (z.getDeathCt() > 1500) {
                    itZ.remove();
                }
                continue;
            }
            Circ zCirc = new Circ(z.getX(), z.getY(), z.getR());
            for (Iterator<Bullet> itB = bullets.iterator(); itB.hasNext();) {
                Bullet b = itB.next();
                Circ bCirc = new Circ(b.getX(), b.getY(), b.getR());
                if (Collisions.circCirc(zCirc, bCirc)) {
                    itB.remove();
                    z.changeHealth(-b.getDamage());
                    if (z.getHealth() <= 0) {
                        z.resetDeathCtr();
                        player.addPoints(Math.abs(z.getX() * z.getY()) * 0.0001);
                    }
                    break;
                }
            }
        }
        //Check zombie collision with other zombies
        for (int i = 0; i < zombies.size(); i++) {
            Zombie z1 = zombies.get(i);
            if (z1.getHealth() <= 0.0) {
                continue;
            }
            Circ z1Circ = new Circ(z1.getX(), z1.getY(), z1.getR());
            for (int j = 0; j < zombies.size(); j++) {
                if (j == i) {
                    continue;
                }
                Zombie z2 = zombies.get(j);
                if (z2.getHealth() <= 0.0) {
                    continue;
                }
                Circ z2Circ = new Circ(z2.getX(), z2.getY(), z2.getR());
                Collisions.circCirc(z1Circ, z2Circ);
                z2.setX(z2Circ.x);
                z2.setY(z2Circ.y);
            }
        }

        Circ playerCirc = new Circ(player.getX(), player.getY(), player.getR());
        for (Zombie z : zombies) {
            if (z.getHealth() <= 0.0) {
                continue;
            }
            Circ zCirc = new Circ(z.getX(), z.getY(), z.getR());
            if (Collisions.circCirc(playerCirc, zCirc)) {
                player.changeHealth(-1.0);
            }
            z.setX(zCirc.x);
            z.setY(zCirc.y);
        }

        playerCirc = new Circ(player.getX(), player.getY(), player.getR());
        for (ArrayList<Tile> i : tiles) {
            for (Tile j : i) {
                if (j.getType() == Tile_t.WALL) {
                    Collisions.circTile(playerCirc, j);
                    for (Zombie z : zombies) {
                        if (z.getHealth() <= 0.0) {
                            continue;
                        }
                        Circ zombieCirc = new Circ(z.getX(), z.getY(), z.getR());
                        Collisions.circTile(zombieCirc, j);
                        z.setX(zombieCirc.x);
                        z.setY(zombieCirc.y);
                    }
                    player.setX(playerCirc.x);
                    player.setY(playerCirc.y);
                }
            }
        }
        for (Bullet b : bullets) {
            b.update();
        }
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext();) {
            Bullet b = iterator.next();
            //bullet gets deleted after 3 seconds on screen
            if (b.getDeltaTime() > 3000) {
                iterator.remove();
            }
        }
    }
}
