package car_game_1_25_17;
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

public class MyWorld extends JPanel implements ActionListener {
    public static Player player;
    private Map map;

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Zombie> zombies = new ArrayList<Zombie>();

    //private boolean keyW = false, keyS = false, keyA = false, keyD = false, keySpace;

    private int nWidth, nHeight;
    private int camX = 0, camY = 0;
    private int laggedCamX = 0, laggedCamY = 0;
    private long startTime = System.currentTimeMillis();
    private int deltaTime = 20;
    private final int NUM_OF_ZOMBIES = 20;
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
    
    public void setRunning(boolean b) { running = b; }
    
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

        player = new Player(400, nHeight/2.0);

        map = new Map(nWidth, nHeight);
        map.update(nWidth, nHeight, 0, 0);
        
        Timer timer = new Timer(20, this);
        timer.start();
        
        //setPreferredSize(new Dimension(600,600));
    }
    private class ActionMove extends AbstractAction {

        String dir;

        ActionMove(String dir) {

            this.dir = dir;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(dir.equals(UP))  keyW = true;
            else if(dir.equals(DOWN))   keyS = true;
            else if(dir.equals(LEFT))   keyA = true;
            else if(dir.equals(RIGHT))  keyD = true;
            else if(dir.equals(SPACE))  {
                keySpace = true;
                bullets.add(new Bullet(player.getX() + player.getR() - Bullet.getR(), player.getY() + player.getR() - Bullet.getR(), player.getDir()));
            }
            else if(dir.equals(UP_RELEASE))  keyW = false;
            else if(dir.equals(DOWN_RELEASE))   keyS = false;
            else if(dir.equals(LEFT_RELEASE))   keyA = false;
            else if(dir.equals(RIGHT_RELEASE))  keyD = false;
            else if(dir.equals(SPACE_RELEASE))  keySpace = false;
            running = true;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(player.isDead()) {
                g.setColor(new Color(255, 100, 100));
                g.drawString("health: " + player.getHealth() + " / 1000", 400, 100);
                g.drawString("points: " + (int)(player.getPoints()), 200, 100);
        } else {
            deltaTime = (int)(System.currentTimeMillis() - startTime);
            startTime = System.currentTimeMillis();

            /*
            g.setColor(Color.PINK);
            g.fillRect(10 - laggedCamX, 400 - laggedCamY, 600, 3600);
            g.setColor(Color.ORANGE);
            g.fillRect(20 - laggedCamX, 300 - laggedCamY, 1200, 700);
            g.setColor(Color.GRAY);
            g.fillRect(300 - laggedCamX, 200 - laggedCamY, 2400, 1707);
            g.setColor(Color.GREEN);
            g.fillRect(400 - laggedCamX, 100 - laggedCamY, 2060, 901);
             */
            map.paint(g, laggedCamX, laggedCamY);

            for(Bullet b : bullets) {
                b.paint(g, laggedCamX, laggedCamY);
            }
            for(Zombie z : zombies) {
                if(z.getDead()) continue;
                z.paint(g, laggedCamX, laggedCamY);
            }
            player.paint(g, laggedCamX, laggedCamY);

            for(int i = 0; i <= 10; i++) {
                g.setColor(new Color(100, i*25, 100 + (i*5)));
                g.drawString("health: " + player.getHealth() + " / 1000", 400, 100 + (20*i));
                g.drawString("points: " + (int)(player.getPoints()), 200, 100 + (20*i));
            }
            if(player.getHealth() < 100) {
                for(int i = 0; i < 100; i++){
                    g.setColor(new Color(100 + i, i/2, i/2));
                    g.drawString("DANGER", 300, 100 + 10*i);
                }
            }
            /*
            g.setColor(Color.GRAY);
            g.fillRect(0, 155, 100, 30);
            g.setColor(Color.GREEN);
            g.drawString("bullets: " + bullets.size(), 0, 175);
            int dt = (int)(System.currentTimeMillis() - startTime);
            g.setColor(Color.WHITE);
            g.fillRect(0, 340, 140, 350);
            g.setColor(Color.BLACK);
            g.drawString("max draw time: " + maxDrawTime, 0, 360);
            g.drawString("current draw time: " + dt, 0, 380);
            g.drawString("max update time: " + maxUpdateTime, 0, 420);
            g.drawString("current update time: " + updateTime, 0, 440);
            g.drawString("calc max: " + maxCalcTime, 0, 480);
            g.drawString("calc: " + calcTime, 0, 500);
            g.drawString("loop 1 max: " + dt_loop1Max, 0, 540);
            g.drawString("loop 1: " + dt_loop1, 0, 560);
            g.drawString("loop 2 max: " + dt_loop2Max, 0, 600);
            g.drawString("loop 2: " + dt_loop2, 0, 620);
            g.drawString("loop 3 max: " + dt_loop3Max, 0, 660);
            g.drawString("loop 3: " + dt_loop3, 0, 680);*/
        }
        Toolkit.getDefaultToolkit().sync();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(player.isDead() || !running) return;
        
        player.updateVel(keyW, keyA, keyS, keyD, deltaTime);
        
        
        
        if(zombies.size() < NUM_OF_ZOMBIES) {
            zombies.add(new Zombie( Math.random() * (nWidth - 2*Zombie.getR()) + player.getX() - nWidth/2.0 + player.getR(), Math.random() * (nHeight - 2*Zombie.getR()) + player.getY() - nHeight/2.0 + player.getR() ));
        }

        if(keySpace) {
            bullets.add(new Bullet(player.getX() + player.getR() - Bullet.getR(), player.getY() + player.getR() - Bullet.getR(), player.getDir()));
        }

        for(Zombie z : zombies) {
            z.updateVel(player, deltaTime);
            z.update();
        }

        player.update();
        //Circ playerCirc = new Circ(player.getX(), player.getY(), player.getR());

        camX = (int)(player.getX() + player.getR() - (nWidth/2.0));
        camY = (int)(player.getY() + player.getR() - (nHeight/2.0));

        /* put camera through a low-pass filter so that it doesn't make sudden movements cuz
         * that hurts my eyeballs.  */
        laggedCamX = (int)((1-drunkMode)*laggedCamX + drunkMode*camX);
        laggedCamY = (int)((1-drunkMode)*laggedCamY + drunkMode*camY);

        //L O N G
        map.update(nWidth, nHeight, laggedCamX, laggedCamY);
        ArrayList<ArrayList<Tile>> tiles = map.getTiles();

        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {
            boolean deleteThisBullet = false;
            Bullet b = iterator.next();
            for(ArrayList<Tile> i : tiles) {
                for(Tile j : i) {
                    if(j.getType() == Tile_t.WALL) {
                        if(Collisions.bulletTile(b, j)) {
                            deleteThisBullet = true;
                            map.deleteTilePt(new Point(j.getX(), j.getY()));
                        }
                    }
                }
            }
            if(deleteThisBullet) iterator.remove();
        }

        for (Iterator<Zombie> iterator = zombies.iterator(); iterator.hasNext(); ) {
            Zombie z = iterator.next();
            if(Math.abs(z.getX() - player.getX()) > nWidth*0.66 || Math.abs(z.getY() - player.getY()) > nHeight*0.66) {
                iterator.remove();
            }
        }
        for (Iterator<Zombie> itZ = zombies.iterator(); itZ.hasNext(); ) {
            Zombie z = itZ.next();
            if(z.getDead()) continue;
            Circ zCirc = new Circ(z.getX(), z.getY(), z.getR());
            for (Iterator<Bullet> itB = bullets.iterator(); itB.hasNext(); ) {
                Bullet b = itB.next();
                Circ bCirc = new Circ(b.getX(), b.getY(), b.getR());
                if(Collisions.circCirc(zCirc, bCirc)) {
                    player.addPoints(z.getX() * z.getY() * 0.0001);
                    itB.remove();
                    z.setDead(true);
                    z.resetDeathCtr();
                    break;
                }
            }
        }

        for(int i = 0; i < zombies.size(); i++) {
            Zombie z1 = zombies.get(i);
            if(z1.getDead()) continue;
            Circ z1Circ = new Circ(z1.getX(), z1.getY(), z1.getR());
            for(int j = 0; j < zombies.size(); j++) {
                if(j == i) continue;
                Zombie z2 = zombies.get(j);
                if(z2.getDead()) continue;
                Circ z2Circ = new Circ(z2.getX(), z2.getY(), z2.getR());
                Collisions.circCirc(z1Circ, z2Circ);
                z2.setX(z2Circ.x);
                z2.setY(z2Circ.y);
            }
        }

        Circ playerCirc = new Circ(player.getX(), player.getY(), player.getR());
        for(Zombie z : zombies) {
            if(z.getDead()) continue;
            Circ zCirc = new Circ(z.getX(), z.getY(), z.getR());
            if(Collisions.circCirc(playerCirc, zCirc)) {
                player.hurt();
            }
            z.setX(zCirc.x);
            z.setY(zCirc.y);
        }

        playerCirc = new Circ(player.getX(), player.getY(), player.getR());
        for(ArrayList<Tile> i : tiles) {
            for(Tile j : i) {
                if(j.getType() == Tile_t.WALL) {
                    Collisions.circTile(playerCirc, j);
                    for(Zombie z : zombies) {
                        if(z.getDead()) continue;
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
        for(Bullet b : bullets) {
            b.update();
        }
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {
            Bullet b = iterator.next();
            //bullet gets deleted after 3 seconds on screen
            if(b.getDeltaTime() > 3000) iterator.remove();
        }
        repaint();
    }
}