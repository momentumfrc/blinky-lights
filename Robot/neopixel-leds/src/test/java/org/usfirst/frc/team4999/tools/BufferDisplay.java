package org.usfirst.frc.team4999.tools;

import java.util.ArrayList;
import java.util.Arrays;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Display;
import org.usfirst.frc.team4999.lights.Packet;

public class BufferDisplay implements Display {

    @FunctionalInterface
    public interface BufferUpdateListener {
        public void onBufferUpdated(Color[] buffer);
    }

    private ArrayList<BufferUpdateListener> listeners = new ArrayList<>();
    private Color[] buffer;

    /**
     * The BufferDisplay is a Display which will interpret received packets and draw the animations to a Color[] buffer.
     * The BufferDisplay implements the Observable design pattern. 
     * Upon receipt of a "show" packet, the BufferDisplay will notify all observers with a copy of the rendered Color[] buffer.
     * It is intended to keep all the code for parsing and interpreting packets in one file.
     * @param numPix
     */
    public BufferDisplay(int numPix) {
        buffer = new Color[numPix];
        for(int i = 0; i < buffer.length; i++) {
            buffer[i] = Color.BLACK;
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
            listener.onBufferUpdated(Arrays.copyOf(buffer, buffer.length));
        }
    }
    
    private int unsignedByteValue(byte b) {
        // See https://stackoverflow.com/questions/11380062/what-does-value-0xff-do-in-java
        // Basically, when the byte is cast to an int, it is sign-extended.
        // By masking only the last byte, we remove the sign-extended bits.
        return ((int) b) & 0xff;
    }

    private void interpretPacket(Packet packet) {
        /*
        This interpretation code should be identical to that of the arduino

        Packet format:
        - B0 : payload size
        - B1-15 : payload  (Max payload is 15 bytes)

        Command payload format:
        - B0 : command id
        - B1-14 : command data (varies by command)

        Commands:
        - 0x01 : <empty> : show current image
        - 0x02 : pixel address ; RGB : set single pixel at address with value RGB
        - 0x03 : start address ; RGB ; length : set "length" pixels starting at "start" with value RGB
        - 0x04 : start address ; RGB ; length ; stride : set "length" pixels starting at "start" with value RGB.  Repeat every "stride" pixels.
        - 0x05 : start address ; RGB ; length ; stride ; max length : same as 0x04, but will stop after max length pixels

        Error rejection/correction:
        - Invalid packets will be rejected and packet parsing will begin again with next byte in stream.

        Display protocol:
        - At beginning of frame, send >= 16 0xff bytes to force resync
        - Send paint commands to fill image buffer.  Unpainted pixels will retain prior contents.
        - Send "show current image" command

        */
        byte[] rawPacketBytes = packet.getData();
        int payloadLen = unsignedByteValue(rawPacketBytes[0]);
        if(rawPacketBytes.length - 1 != payloadLen) {
            System.err.format("Invalid packet: stated length %d does not match actual length %d\n", payloadLen, rawPacketBytes.length - 1);
            return;
        }
        int[] payload = new int[rawPacketBytes.length - 1];
        for(int i = 0; i < payload.length; i++) {
            payload[i] = unsignedByteValue(rawPacketBytes[i + 1]);
        }
        
        int len = payload.length;
        int command = payload[0];
        int address = payload[1];
        Color rgb = new Color(payload[2], payload[3], payload[4]);
        switch(command) {
            case 1:
                if(len == 1) {
                    notifyBufferListeners();
                } else {
                    System.err.format("Invalid display packet: expected length 1, actual length %d\n", len);
                }
                break;
            case 2:
                if(len == 5) {
                    paintPattern(address, rgb, 1, 0, 0);
                } else {
                    System.err.format("Invalid single packet: expected length 5, actual length %d\n", len);
                }
                break;
            case 3:
                if(len == 6) {
                    int count = payload[5];
                    paintPattern(address, rgb, count, 0, 0);
                } else {
                    System.err.format("Invalid run packet: expected length 5, actual length %d\n", len);
                }
                break;
            case 4:
                if(len == 7) {
                    int count = payload[5];
                    int stride = payload[6];
                    paintPattern(address, rgb, count, stride, 0);
                } else {
                    System.err.format("Invalid stride packet: expected length 7, actual length %d\n", len);
                }
                break;
            case 5:
                if(len == 8) {
                    int count = payload[5];
                    int stride = payload[6];
                    int maxlength = payload[7];
                    paintPattern(address, rgb, count, stride, address + maxlength);
                } else {
                    System.err.format("Invalid truncated stride packet: expected length 8, actual length %d\n", len);
                }
                break;
            default:
                System.err.format("Invalid packet: unknown command 0x%x\n", command);
        }
    }

    private void paintPattern(int address, Color rgb, int count, int stride, int limit) {
        if(limit == 0 || limit > buffer.length) {
            limit = buffer.length;
        }
        do {
            for(int i = 0; i < count; i++) {
                int pixel = address + i;
                if(pixel < limit) {
                    buffer[pixel] = rgb;
                }
            }
            address += stride;
        } while (address < limit && stride > 0);
    }

    @Override
    public void show(Packet[] commands) {
        for(Packet packet : commands) {
            interpretPacket(packet);
        }
        notifyBufferListeners();
    }
}
