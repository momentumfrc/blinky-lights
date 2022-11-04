package org.usfirst.frc.team4999.lights;

import java.util.ArrayList;
import java.util.Arrays;

import org.usfirst.frc.team4999.lights.commands.*;

/**
 * A double-buffered Display which interprets received commands and draws the animations to
 * a Color[] buffer.
 * <p>
 * The frontBuffer can be accessed using {@link #getFrontBuffer()}.
 * <p>
 * The BufferDisplay implements the Observable design pattern.
 * Any {@link BufferUpdateListener} can be registered with this class so that when a new frame
 * is blitzed to the front buffer, BufferDisplay will notify all observers with a copy of the
 * front buffer.
 * <p>
 * This class is intended to keep all the code for interpreting commands in one file.
*/
public class BufferDisplay implements Display {

    @FunctionalInterface
    public interface BufferUpdateListener {
        public void onBufferUpdated(Color[] buffer);
    }

    private ArrayList<BufferUpdateListener> listeners = new ArrayList<>();
    private Color[] backBuffer;
    private Color[] frontBuffer;

    /**
     * @param numPix the size of the buffer
     */
    public BufferDisplay(final int numPix) {
        backBuffer = new Color[numPix];
        frontBuffer = new Color[numPix];
        for(int i = 0; i < numPix; i++) {
            backBuffer[i] = Color.BLACK;
            frontBuffer[i] = Color.BLACK;
        }
    }

    public void addBufferListener(BufferUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeBufferListener(BufferUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyBufferListeners() {
        for(BufferUpdateListener listener : listeners) {
            listener.onBufferUpdated(Arrays.copyOf(frontBuffer, frontBuffer.length));
        }
    }

    private void paintPattern(int address, Color rgb, int count, int stride, int limit) {
        if(limit == 0 || limit > backBuffer.length) {
            limit = backBuffer.length;
        }
        do {
            for(int i = 0; i < count; i++) {
                int pixel = address + i;
                if(pixel < limit) {
                    backBuffer[pixel] = rgb;
                }
            }
            address += stride;
        } while (address < limit && stride > 0);
    }

    /**
     * Gets a copy of the front buffer
     * @return a copy of the front buffer
     */
    public Color[] getFrontBuffer() {
        return Arrays.copyOf(frontBuffer, frontBuffer.length);
    }

    @Override
    public void show(Command[] commands) {
        for(Command command : commands) {
            // FIXME: There is probably a better way to use polymorphism here instead of direct
            //        instanceof checks. Maybe create some sort of PaintPatternArgs class, and
            //        have every Command be able to return an instance of it?
            if(command instanceof SyncCommand) {
                // Ignore sync commands: since we're getting an array of discrete commands
                // (not a stream of raw packet bytes), there's no possibility of getting
                // out-of-sync, and so the sync packets are not necessary.
            }
            else if(command instanceof ShowCommand) {
                // Blitz the back buffer to the front buffer
                System.arraycopy(
                    backBuffer, 0,
                    frontBuffer, 0,
                    backBuffer.length
                );
                notifyBufferListeners();
            }
            else if(command instanceof SingleCommand) {
                SingleCommand castCommand = (SingleCommand) command;
                paintPattern(
                    castCommand.getAddress(),
                    castCommand.getColor(),
                    1,
                    0,
                    0
                );
            }
            else if(command instanceof RunCommand) {
                RunCommand castCommand = (RunCommand) command;
                paintPattern(
                    castCommand.getAddress(),
                    castCommand.getColor(),
                    castCommand.getLength(),
                    0,
                    0
                );
            }
            else if(command instanceof StrideCommand) {
                StrideCommand castCommand = (StrideCommand) command;
                paintPattern(
                    castCommand.getAddress(),
                    castCommand.getColor(),
                    castCommand.getLength(),
                    castCommand.getStride(),
                    0
                );
            }
            else if(command instanceof FiniteStrideCommand) {
                FiniteStrideCommand castCommand = (FiniteStrideCommand) command;
                paintPattern(
                    castCommand.getAddress(),
                    castCommand.getColor(),
                    castCommand.getLength(),
                    castCommand.getStride(),
                    castCommand.getAddress() + castCommand.getTotalLength()
                );
            }
            else {
                throw new UnsupportedOperationException("Unknown command");
            }
        }
    }
}
