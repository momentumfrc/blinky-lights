package org.usfirst.frc.team4999.lights;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        boolean write(byte[] data);
    }

    private static class NeoPixelsI2C extends I2C implements NeoPixelsIO {
        private final ByteBuffer buffer;

        public NeoPixelsI2C(Port port, int deviceAddress) {
            super(port, deviceAddress);
            buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
        }

        public boolean write(byte[] data) {
            buffer.rewind();
            buffer.put(data);
            buffer.rewind();
            return writeBulk(buffer, data.length);
        }

    }

    private static class NeoPixelsSPI extends SPI implements NeoPixelsIO {
        private final ByteBuffer buffer;

        private static final int SPI_CLOCK_RATE = 4_000_000;

        public NeoPixelsSPI(Port port) {
            super(port);
            buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);

            setChipSelectActiveLow();
            setMode(SPI.Mode.kMode0);
            setClockRate(SPI_CLOCK_RATE);
        }

        @Override
        public boolean write(byte[] data) {
            buffer.rewind();
            for(int i = 0; i < data.length; i += 2) {
                int value = data[i] << 8;
                if(i < data.length - 1) {
                    value |= data[i+1];
                } else {
                    value |= 0xFF;
                }
                buffer.putShort((short) value);
            }

            write(buffer, (data.length + 1) / 2);

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
            Packet[] packets;
            int packetIdx = 0;
            // Send a sync packet every SYNC_FREQ frames
            if(++syncidx >= SYNC_FREQ) {
                packets = new Packet[commands.length + 1];
                packets[packetIdx++] = syncPacket;
                syncidx = 0;
            } else {
                packets = new Packet[commands.length];
            }

            for(Command command : commands) {
                packets[packetIdx++] = command.build();
            }

            int buffSize = 0;
            for(Packet packet : packets) {
                buffSize += packet.getSize();
            }

            // We coalesce all data to be transferred into a single buffer, since starting/stopping a transmission
            // can have overhead (for example, the SPI interface has to wait until chip select is asserted before it
            // can start writing data)
            byte[] buff = new byte[buffSize];
            int buffIdx = 0;
            for(Packet packet : packets) {
                System.arraycopy(packet.getData(), 0, buff, buffIdx, packet.getSize());
                buffIdx += packet.getSize();
            }

            strip.write(buff);

        } catch (Exception e) {
            // The generic try-catch prevents an error in the neopixels from killing the whole robot
            DriverStation.reportError("Error sending NeoPixels packet: " + e.getMessage(), e.getStackTrace());
        }

    }

}
