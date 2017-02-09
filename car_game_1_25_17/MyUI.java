package car_game_1_25_17;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyUI extends JFrame implements ActionListener {
     
    final static String STARTPANEL = "JPanel with start screen";
    final static String WORLDPANEL = "JPanel with world";
    
    public final static int nWidth = 900;
    public final static int nHeight = 700;
    
    CardLayout layout = new CardLayout();
    
    static JPanel panel = new JPanel(); //a panel that uses CardLayout
    JButton playButton = new JButton("PLAY");
    
    JPanel startPanel = new JPanel();
    MyWorld myWorld;
    
    public MyUI() {
        panel.setLayout(layout);
        
        playButton.addActionListener(this);
        
        startPanel.add(playButton);
        startPanel.setBackground(new Color(40, 180, 90));
        
        myWorld = new MyWorld();
        myWorld.setRunning(false);
        
        panel.add(startPanel, STARTPANEL);
        panel.add(myWorld, WORLDPANEL);
        
        add(panel);
        layout.show(panel, STARTPANEL);
        
        super.setSize(900, 700);
        super.setResizable(false);
        super.setTitle("My Awesome/Awful Game");
        super.setLocationRelativeTo(null);
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.requestFocus();
    }
    public void actionPerformed(ActionEvent event) {

        Object source = event.getSource();

        if(source == playButton)
        {
            layout.show(panel, WORLDPANEL);
        }
        else
        {
            //layout.show(panel, STARTPANEL);
        }
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