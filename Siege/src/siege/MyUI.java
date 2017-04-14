package siege;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static siege.MyWorld.player;

public class MyUI extends JFrame {

    final static String START_PANEL = "start panel";
    final static String WORLD_PANEL = "world panel";
    final static String END_PANEL = "end panel";

    public final static int nWidth = 900;
    public final static int nHeight = 700;

    private static CardLayout layout = new CardLayout();

    static JPanel panel = new JPanel(); //a panel that uses CardLayout

    private StartPanel startPanel = new StartPanel();
    private static MyWorld myWorld;

    public static final boolean DEBUG_GRAPHICS = false;
    public static final boolean DEBUG_TEXT = false;
    
    public static void writeLog(String str) {
        if(DEBUG_TEXT) System.out.println(str);
    }
    
    public static MyWorld getWorld() {
        return myWorld;
    }
    private static EndPanel endPanel = new EndPanel();

    public static void showEnd() {
        layout.show(panel, END_PANEL);
    }

    public static void showWorld() {
        layout.show(panel, WORLD_PANEL);
    }

    public MyUI() {
        panel.setLayout(layout);

        startPanel.setBackground(new Color(40, 180, 90));

        myWorld = new MyWorld();
        myWorld.setRunning(false);

        panel.add(startPanel, START_PANEL);
        panel.add(myWorld, WORLD_PANEL);
        panel.add(endPanel, END_PANEL);

        add(panel);
        layout.show(panel, START_PANEL);

        super.setSize(900, 700);
        super.setResizable(false);
        super.setTitle("My Awesome/Awful Game");
        super.setLocationRelativeTo(null);
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.requestFocus();
    }

    public static void main(String[] args) {
        MyUI gui = new MyUI();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.setVisible(true);
            }
        });
    }
}
