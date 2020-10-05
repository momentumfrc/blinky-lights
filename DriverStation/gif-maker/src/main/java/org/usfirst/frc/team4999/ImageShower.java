package org.usfirst.frc.team4999;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class ImageShower extends JFrame {

    private class ImageComponent extends JComponent {

        private BufferedImage img;
    
        public ImageComponent() {
            super();
        }
    
        @Override
        public void paintComponent(Graphics gd) {
            Graphics2D g = (Graphics2D) gd;
            g.drawImage(img, 0, 0, null);
        }
    
        public void setImage(BufferedImage img) {
            this.img = img;
            setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
            revalidate();
            repaint();
        }
    }

    private ImageComponent imageComponent;

    public ImageShower() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageComponent = new ImageComponent();
        add(imageComponent);
        setVisible(true);
    }

   public void showImage(BufferedImage img) {
       imageComponent.setImage(img);
       pack();
   }
}
