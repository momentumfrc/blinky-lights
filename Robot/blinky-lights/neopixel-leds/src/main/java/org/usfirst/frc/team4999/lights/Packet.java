package org.usfirst.frc.team4999.lights;

/**
 * A binary representation of a {@link org.usfirst.frc.team4999.lights.commands.Command}
 * <p>
 * Commands are represented according to the protocol specified
 * <a href="https://github.com/momentumfrc/2017Steamworks/wiki/Light-strings-on-the-Robot">here</a>
 * @author Jordan
 */
public class Packet {

    private static final int MAX_PACKET_SIZE = 16;

    private byte[] data;

    public Packet(byte[] data) {
        if(data.length > MAX_PACKET_SIZE) throw new IllegalArgumentException("Packets are no longer than " + MAX_PACKET_SIZE + "bytes");
        this.data = data.clone();
    }

    public int getSize() {
        return data.length;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Packet) {
            Packet other = (Packet) o;
            byte[] otherData = other.getData();
            if(data.length != otherData.length)
                return false;

            for(int i = 0; i < data.length; i++) {
                if(data[i] != otherData[i])
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String out = "Packet [";
        out += String.format("l:%d,", data[0]&0xFF);
        switch(data[1]) {
        case 0x01:
            out += "c:display";
            break;
        case 0x02:
            out += "c:single,";
            out += String.format("start:%d,r:%d,g:%d,b:%d",data[2]&0xFF, data[3]&0xFF,data[4]&0xFF,data[5]&0xFF);
            break;
        case 0x03:
            out += "c:run,";
            out += String.format("start:%d,length:%d,r:%d,g:%,b:%d",data[2]&0xFF, data[6]&0xFF,data[3]&0xFF,data[4]&0xFF,data[5]&0xFF);
            break;
        case 0x04:
            out += "c:stride,";
            out += String.format("start:%d,length:%d,stride:%d,r:%d,g:%d,b:%d",data[2]&0xFF,data[6]&0xFF,data[7]&0xFF,data[3]&0xFF,data[4]&0xFF,data[5]&0xFF);
            break;
        case 0x05:
            out += "c:stride_with_end,";
            out += String.format("start:%d,length:%d,stride:%d,totallength:%d,r:%d,g:%d,b:%d",data[2]&0xFF,data[6]&0xFF,data[7]&0xFF,data[8]&0xFF,data[3]&0xFF,data[4]&0xFF,data[5]&0xFF);
            break;
        }
        out += "]";
        return out;
    }
}
