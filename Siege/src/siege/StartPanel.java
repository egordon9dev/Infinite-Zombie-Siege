package siege;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartPanel extends JPanel implements ActionListener {
    private JButton playButton = new JButton("PLAY");
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawString("This is the starting screen...", 200, 200);
        Toolkit.getDefaultToolkit().sync();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    public StartPanel() {
        super(new BorderLayout());
        setFocusable(true);
        setBackground(Color.GRAY);
        setDoubleBuffered(true);
        
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MyUI.showWorld();
            }
        });
        add(playButton, BorderLayout.NORTH);
    }
}