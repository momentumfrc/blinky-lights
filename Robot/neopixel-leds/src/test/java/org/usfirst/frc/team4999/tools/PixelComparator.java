package org.usfirst.frc.team4999.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.junit.Assert.fail;

public class PixelComparator implements BufferDisplay.BufferUpdateListener {

    private static class DifferenceShower {

        private static Object lock = new Object();

        public static void showDifference(Color[] expected, Color[] actual) {
            final int pixel_size = 20;
            final int txt_offset = 4;
            JComponent component = new JComponent() {
                private static final long serialVersionUID = 2L;
                @Override
                public void paintComponent(Graphics gd) {
                    Graphics2D g = (Graphics2D) gd;
                    g.setPaint(Color.BLACK);
                    for(int i = 0; i < expected.length; i++) {
                        Rectangle rect = new Rectangle(i * pixel_size, 0, pixel_size, pixel_size);
                        String num = Integer.toString(i);
                        g.draw(rect);
                        g.drawString(num, i*pixel_size + txt_offset, pixel_size - txt_offset);
                    }
                    for(int i = 0; i < expected.length; i++) {
                        Rectangle rect = new Rectangle(i * pixel_size, pixel_size, pixel_size, pixel_size);
                        g.setPaint(expected[i]);
                        g.fill(rect);
                    }
                    for(int i = 0; i < actual.length; i++) {
                        Rectangle rect = new Rectangle(i * pixel_size, pixel_size * 2, pixel_size, pixel_size);
                        g.setPaint(actual[i]);
                        g.fill(rect);
                    }
                }
            };
            component.setPreferredSize(new Dimension(Math.max(expected.length, actual.length) * pixel_size, pixel_size * 3));

            JFrame frame = new JFrame();
            frame.add(component);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.pack();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent arg) {
                    synchronized(lock) {
                        frame.setVisible(false);
                        lock.notifyAll();
                    }
                }
            });

            frame.setVisible(true);

            synchronized(lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frame.dispose();
        }
    }

    private Vector<Color[]> displayHistory;

    private static final String animationFileLocation = "animationFiles";

    public PixelComparator() {
        displayHistory = new Vector<Color[]>();
    }

    @Override
    public void onBufferUpdated(org.usfirst.frc.team4999.lights.Color[] buffer) {
        Color[] awtBuffer = Arrays.stream(buffer).map(c -> new Color(c.getRed(), c.getGreen(), c.getBlue())).toArray(Color[]::new);
        displayHistory.add(awtBuffer);
    }

    public void writeToFile(String filename) {
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOut = null;
        try {
            fileOut = new FileOutputStream(Path.of(animationFileLocation,filename).toString());
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(displayHistory);
            objectOut.close();
            fileOut.close();
            System.out.println("The display history was succesfully saved");
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof FileNotFoundException) {
                throw new RuntimeException(String.format("File Not Found: %s", Path.of(animationFileLocation,filename).toString()));
            }
        } finally {
            if(objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
            if(fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
    }
    public void compareToFile(String filename) {
        compareToFile(filename, true);
    }

    @SuppressWarnings("unchecked")
    public void compareToFile(String filename, boolean headless) {
        FileInputStream fileIn = null;
        ObjectInputStream objectIn = null;
        Vector<Color[]> readHistory = null;
        try {
            fileIn = new FileInputStream(Path.of(animationFileLocation,filename).toString());
            objectIn = new ObjectInputStream(fileIn);
            
            Object obj = objectIn.readObject();
            readHistory = (Vector<Color[]>) obj;        
        } catch (Exception e) {
            if(e instanceof FileNotFoundException) {
                throw new RuntimeException(String.format("File Not Found: %s", Path.of(animationFileLocation,filename).toString()));
            }
            e.printStackTrace();
        } finally {
            if(objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
            if(fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }

        if(readHistory == null) {
            throw new RuntimeException("The display history could not be read");
        }

        if(displayHistory.size() != readHistory.size()) {
            fail(String.format("Buffer history lengths differ: expected %d, actual %d\n", readHistory.size(), displayHistory.size()));
            return;
        }

        for(int i = 0; i < displayHistory.size(); i++) {
            Color[] curr = displayHistory.get(i);
            Color[] currKey = readHistory.get(i);

            if(curr.length != currKey.length) {
                fail(String.format("Buffer sizes differ: expected %d, actual %d\n", currKey, curr));
                if(!headless) DifferenceShower.showDifference(currKey, curr);
                return;
            }

            for(int j = 0; j < curr.length; j++) {
                if(!curr[j].equals(currKey[j])) {
                    if(!headless) DifferenceShower.showDifference(currKey, curr);
                    fail(String.format("Buffer pixels differ: (frame:%d color:%d) %s != %s\n", i, j, curr[j], currKey[j]));
                    return;
                }
            }
        }

        return;
    }

}
