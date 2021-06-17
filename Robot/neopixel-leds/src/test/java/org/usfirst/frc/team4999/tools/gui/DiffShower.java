package org.usfirst.frc.team4999.tools.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class DiffShower {
    
    private static Object waitForButtonPressLock = new Object();

    private static Color[] convertMomentumColorToAwt(org.usfirst.frc.team4999.lights.Color[] in) {
        return Arrays.stream(in).map(color -> new Color(color.getRed(), color.getGreen(), color.getBlue())).toArray(Color[]::new);
    }

    public static void showDifference(org.usfirst.frc.team4999.lights.Color[] momentumExpected, org.usfirst.frc.team4999.lights.Color[] momentumActual) {
        Color[] expected = convertMomentumColorToAwt(momentumExpected);
        Color[] actual = convertMomentumColorToAwt(momentumActual);
        final int pixel_size = 20;
        final int txt_offset = 4;
        final int length = Math.max(expected.length, actual.length);
        JComponent component = new JComponent() {
            private static final long serialVersionUID = 2L;
            @Override
            public void paintComponent(Graphics gd) {
                Graphics2D g = (Graphics2D) gd;
                g.setPaint(Color.BLACK);
                for(int i = 0; i < length; i++) {
                    Rectangle rect = new Rectangle(i * pixel_size, 0, pixel_size, pixel_size);
                    String num = Integer.toString(i);
                    g.draw(rect);
                    g.drawString(num, i * pixel_size + txt_offset, pixel_size - txt_offset);
                }
                for(int i = 0; i < length; i++) {
                    Rectangle rect = new Rectangle(i * pixel_size, pixel_size, pixel_size, pixel_size);
                    g.setPaint((i < expected.length) ? expected[i] : Color.BLACK);
                    g.fill(rect);
                }
                for(int i = 0; i < length; i++) {
                    Rectangle rect = new Rectangle(i * pixel_size, pixel_size * 2, pixel_size, pixel_size);
                    g.setPaint((i < actual.length) ? actual[i] : Color.BLACK);
                    g.fill(rect);
                }
                for(int i = 0; i < length; i++) {
                    boolean isSame = true;
                    isSame = isSame && (i < expected.length);
                    isSame = isSame && (i < actual.length);
                    isSame = isSame && expected[i].equals(actual[i]);
                    if(isSame) {
                        g.setPaint(Color.WHITE);
                    } else {
                        g.setPaint(Color.RED);
                    }

                    Rectangle rect = new Rectangle(i * pixel_size, pixel_size * 3, pixel_size, pixel_size);
                    g.fill(rect);
                }
            }
        };
        component.setPreferredSize(new Dimension(length * pixel_size, pixel_size * 4));

        JFrame frame = new JFrame();
        frame.add(component);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg) {
                synchronized(waitForButtonPressLock) {
                    frame.setVisible(false);
                    waitForButtonPressLock.notifyAll();
                }
            }
        });

        frame.setVisible(true);

        synchronized(waitForButtonPressLock) {
            try {
                waitForButtonPressLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        frame.dispose();
    }
}
