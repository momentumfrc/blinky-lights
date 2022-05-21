package org.usfirst.frc.team4999.tools;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.usfirst.frc.team4999.tools.gui.DiffShower;
import org.usfirst.frc.team4999.lights.BufferDisplay;
import org.usfirst.frc.team4999.lights.Color;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.fail;

public class PixelComparator implements BufferDisplay.BufferUpdateListener {

    private Vector<Color[]> displayHistory;

    public PixelComparator() {
        displayHistory = new Vector<Color[]>();
    }

    @Override
    public void onBufferUpdated(Color[] buffer) {

        displayHistory.add(buffer);
    }

    public void writeToFile(String name) {
        try {
            AnimationFileManager.saveFile(displayHistory, name);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Failed to save %s", name));
        }
    }
    public void compareToFile(String filename) {
        compareToFile(filename, GraphicsEnvironment.isHeadless());
    }

    public void compareToFile(String name, boolean headless) {
        List<Color[]> readHistory;
        try {
            readHistory = AnimationFileManager.loadFile(name);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Failed to load %s", name));
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
