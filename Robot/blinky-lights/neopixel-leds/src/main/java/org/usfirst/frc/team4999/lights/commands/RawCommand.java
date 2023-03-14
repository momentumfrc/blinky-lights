package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.Packet;

@Deprecated(forRemoval = true, since = "3.0")
public class RawCommand extends Command {

    private final Packet packet;

    /**
     * A command that wraps a raw packet.
     *
     * @deprecated This class should not be used. It exists only for backwards
     * compatibility with code which uses raw packets.
     *
     * @param packet the raw packet to be wrapped
     */
    @Deprecated
    public RawCommand(Packet packet) {
        this.packet = packet;
    }

    @Override
    public Packet build() {
        return packet;
    }

    @Override
    public Command dim() {
        throw new UnsupportedOperationException("RawCommand cannot be dimmed");
    }

    @Override
    public Command[] clip(int clipaddress, int cliplength) {
        throw new UnsupportedOperationException("RawCommand cannot be clipped");
    }

}
