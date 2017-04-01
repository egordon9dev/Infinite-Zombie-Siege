/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siege;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import static siege.MyWorld.player;

/**
 *
 * @author ethan
 */
public class EndPanel extends JPanel implements ActionListener{
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i = 2;
        for(int q = 0; q < 3; q++) {
            
            if(q == 0){
                g.setColor(new Color(20, 0, 0));
                i = 0;
            }
            else if (q == 1) {
                g.setColor(new Color(100, 60, 60));
                i = 1;
            }
            else {
                g.setColor(new Color(255, 100, 100));
                i = 2;
            }
            
            g.setFont(new Font("SansSerif", Font.BOLD, 40)); 
            
            g.drawString("GAME OVER", MyUI.nWidth/2-150-i, 120);
            if(MyWorld.round.isTimeOut()) g.drawString("YOU RAN OUT OF TIME", MyUI.nWidth/2-150-i, 200);
            else g.drawString("YOU DIED", MyUI.nWidth/2-150-i, 160);
            
            g.drawString("ROUND: " + MyWorld.round.getRound(), MyUI.nWidth/2-150-i, 300);
            g.drawString("POINTS: " + (int)(player.getPoints()) + " / " +
                    MyWorld.round.getReqPts(), MyUI.nWidth/2-150-i, 340);
        }
        Toolkit.getDefaultToolkit().sync();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }
    
    public EndPanel() {
        setFocusable(true);
        setBackground(Color.GRAY);
        setDoubleBuffered(true);
    }
}
