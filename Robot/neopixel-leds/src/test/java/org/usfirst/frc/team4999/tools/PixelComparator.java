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

import org.usfirst.frc.team4999.tools.gui.DiffShower;

import java.awt.Color;

import static org.junit.Assert.fail;

public class PixelComparator implements BufferDisplay.BufferUpdateListener {

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
                if(!headless) DiffShower.showDifference(currKey, curr);
                return;
            }

            for(int j = 0; j < curr.length; j++) {
                if(!curr[j].equals(currKey[j])) {
                    if(!headless) DiffShower.showDifference(currKey, curr);
                    fail(String.format("Buffer pixels differ: (frame:%d color:%d) %s != %s\n", i, j, curr[j], currKey[j]));
                    return;
                }
            }
        }

        return;
    }

}
