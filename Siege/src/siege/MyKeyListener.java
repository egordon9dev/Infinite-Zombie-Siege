package siege;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter {
        boolean keyW = false, keyA = false, keyS = false, keyD = false, keySpace = false;
        boolean getW() { return keyW; }
        boolean getA() { return keyA; }
        boolean getS() { return keyS; }
        boolean getD() { return keyD; }
        boolean getSpace() { return keySpace; }
        
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                keyW = false;
            }
            else if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                keyS = false;
            }
            else if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                keyA = false;
            }
            else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                keyD = false;
            }
            else if(key == KeyEvent.VK_SPACE) {
                keySpace = false;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                keyW = true;
            }
            else if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                keyS = true;
            }
            else if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                keyA = true;
            }
            else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                keyD = true;
            }
            else if(key == KeyEvent.VK_SPACE) {
                keySpace = true;
            }
        }
    }