package org.usfirst.frc.team4999.tools.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.usfirst.frc.team4999.tools.*;

public class BufferShower implements BufferDisplay.BufferUpdateListener {
    
    // The actual component displayed in the JFrame
    public class BufferShowerComponent extends JComponent implements BufferDisplay.BufferUpdateListener {

        private static final long serialVersionUID = -6364055200927296526L;

        private static final int PIXEL_SIZE = 20;
        private static final int TXT_OFFSET = 4;
        private static final int DEFAULT_NUMPIXELS = 20;

        public Color[] pixels;

        private void showTestPattern() {
            Color[] testPattern = {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE};

            pixels = new Color[DEFAULT_NUMPIXELS];
            for(int i = 0; i < pixels.length; i++) {
                pixels[i] = testPattern[i % testPattern.length];
            }
            resize();
            revalidate();
            repaint();
        }

        public BufferShowerComponent() {
            super();
            showTestPattern();
        }
        
        @Override
        public void paintComponent(Graphics gd) {
            Graphics2D g = (Graphics2D) gd;
            g.setPaint(Color.BLACK);
            for(int i = 0; i < pixels.length; i++) {
                Rectangle rect = new Rectangle(i * PIXEL_SIZE, 0, PIXEL_SIZE, PIXEL_SIZE);
                String num = Integer.toString(i);
                g.draw(rect);
                g.drawString(num, i * PIXEL_SIZE + TXT_OFFSET, PIXEL_SIZE - TXT_OFFSET);
            }
            for(int i = 0; i < pixels.length; i++) {
                Rectangle rect = new Rectangle(i * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                g.setPaint(pixels[i]);
                g.fill(rect);
            }
        }
        
        public void resize() {
            setPreferredSize(new Dimension(pixels.length * PIXEL_SIZE, PIXEL_SIZE * 2));
        }
        
        @Override
        public void onBufferUpdated(org.usfirst.frc.team4999.lights.Color[] buffer) {

            pixels = Arrays.stream(buffer).map(c -> new Color(c.getRed(), c.getGreen(), c.getBlue())).toArray(Color[]::new);
            
            resize();
            
            revalidate();
            repaint();
        }
        
    }


    private final JFrame frame;
    private final BufferShowerComponent shower;

    private boolean windowIsClosed = false;

    private boolean shouldStepFrames = false;
    private Object framesLock = new Object();

    public BufferShower() {

        frame = new JFrame();
        shower = new BufferShowerComponent();
        frame.add(shower);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        frame.pack();
        frame.setVisible(true);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg) {
                windowIsClosed = true;
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
        
            @Override
            public void keyReleased(KeyEvent e) {}
        
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shouldStepFrames = !shouldStepFrames;
                } else {
                    synchronized(framesLock) {
                        framesLock.notifyAll();
                    }
                }
            }
        });
    }

    public boolean getShouldStepFrames() {
        return shouldStepFrames;
    }

    public Object getFramesLock() {
        return framesLock;
    }

    public boolean getWindowIsClosed() {
        return windowIsClosed;
    }

    public void close() {
        frame.dispose();
    }

    public void addWindowListener(WindowAdapter adapter) {
        frame.addWindowListener(adapter);
    }

    public void addKeyListener(KeyListener listener) {
        frame.addKeyListener(listener);
    }

    @Override
    public void onBufferUpdated(org.usfirst.frc.team4999.lights.Color[] buffer) {
        // No reason to repaint a non-visible component
        if(!windowIsClosed) {
            shower.onBufferUpdated(buffer);
            frame.pack();
        }
    }
    
}
