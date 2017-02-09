package car_game_1_25_17;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

public class StartScreen extends JPanel implements ActionListener {

    private int nWidth, nHeight;
    private MyKeyListener myKeyListener;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("This is the starting screen...", 200, 200);
        Toolkit.getDefaultToolkit().sync();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        repaint();
    }
    
    public StartScreen() {
        myKeyListener = new MyKeyListener();
        addKeyListener(myKeyListener);
        setFocusable(true);
        setBackground(Color.GRAY);
        setDoubleBuffered(true);
        nWidth = MyUI.nWidth;
        nHeight = MyUI.nHeight;
    }
}