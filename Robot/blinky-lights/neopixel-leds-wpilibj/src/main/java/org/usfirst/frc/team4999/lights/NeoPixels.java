package org.usfirst.frc.team4999.lights;


import java.nio.ByteBuffer;
import java.util.EnumMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import org.usfirst.frc.team4999.lights.commands.Command;
import org.usfirst.frc.team4999.lights.commands.SyncCommand;

import edu.wpi.first.wpilibj.I2C;

/**
 * Class to communicate with an arduino driving a strip of NeoPixel LEDs over I2C or SPI
 * @author Jordan
 *
 */
public class NeoPixels implements Display {
    private static final int MAX_PACKET_SIZE = 16;

    private interface NeoPixelsIO {
        public boolean writePacket(Packet packet);
    }

    private static class NeoPixelsI2C extends I2C implements NeoPixelsIO {
        private final ByteBuffer buffer;

        public NeoPixelsI2C(Port port, int deviceAddress) {
            super(port, deviceAddress);
            buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
        }

        public boolean writePacket(Packet packet) {
            buffer.rewind();
            buffer.put(packet.getData());
            buffer.rewind();
            return writeBulk(buffer, packet.getSize());
        }

    }

    private static class NeoPixelsSPI extends SPI implements NeoPixelsIO {
        private final ByteBuffer buffer;

        private static final int SPI_CLOCK_RATE = 100;

        public NeoPixelsSPI(Port port) {
            super(port);
            buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

            setChipSelectActiveLow();
            setMode(SPI.Mode.kMode0);
            setClockRate(SPI_CLOCK_RATE);
        }

        @Override
        public boolean writePacket(Packet packet) {
            var data = packet.getData();
            for(byte b : data) {
                buffer.rewind();
                buffer.put(b);
                write(buffer, 1);
            }
            return true;
        }
    }

    private NeoPixelsIO strip;

    private static EnumMap<I2C.Port, NeoPixels> i2cInstances
        = new EnumMap<>(I2C.Port.class);

    private static EnumMap<SPI.Port, NeoPixels> spiInstances
        = new EnumMap<SPI.Port, NeoPixels>(SPI.Port.class);

    private static final int I2C_ADDRESS = 16;

    private static final int SYNC_FREQ = 1000;
    private int syncidx = 0;

    Packet syncPacket;

    /**
     * Gets an instance of NeoPixels, defaulting to onboard I2C port
     * @deprecated Use {@link #getInstance(I2C.Port)} or {@link #getInstance(SPI.Port)}
     * @return an instance of NeoPixels
     */
    @Deprecated(forRemoval=true, since="3.0")
    public static NeoPixels getInstance() {
        return getInstance(I2C.Port.kOnboard);
    }

    /**
     * Gets an instance of NeoPixels that will communicate over the specified
     * I2C port.
     * @param port the I2C port to use to communicate with the NeoPixels
     * @return an instance of NeoPixels
     */
    public static NeoPixels getInstance(I2C.Port port) {
        if(i2cInstances.containsKey(port)) {
            return i2cInstances.get(port);
        } else {
            NeoPixels instance = new NeoPixels(new NeoPixelsI2C(port, I2C_ADDRESS));
            i2cInstances.put(port, instance);
            return instance;
        }
    }

    /**
     * Gets an instance of NeoPixels that will communicate over the specified
     * SPI port.
     * @param port the SPI port to use to communicate with the NeoPixels
     * @return an instance of NeoPixels
     */
    public static NeoPixels getInstance(SPI.Port port) {
        if(spiInstances.containsKey(port)) {
            return spiInstances.get(port);
        } else {
            NeoPixels instance = new NeoPixels(new NeoPixelsSPI(port));
            spiInstances.put(port, instance);
            return instance;
        }
    }

    private NeoPixels(NeoPixelsIO io) {
        strip = io;
        syncPacket = new SyncCommand().build();
    }

    synchronized public void show(Command[] commands) {
        try {
            // Send a sync packet every SYNC_FREQ frames
            if(++syncidx >= SYNC_FREQ) {
                strip.writePacket(syncPacket);
                syncidx = 0;
            }

            for(Command packet : commands) {
                strip.writePacket(packet.build());
            }

        } catch (Exception e) {
            // The generic try-catch prevents an error in the neopixels from killing the whole robot
            DriverStation.reportError("Error sending NeoPixels packet: " + e.getMessage(), e.getStackTrace());
        }

    }

}
