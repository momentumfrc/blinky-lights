package com.momentum4999.lights.pi

import com.pi4j.Pi4J
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CProvider
import org.usfirst.frc.team4999.lights.Display
import org.usfirst.frc.team4999.lights.Packet
import org.usfirst.frc.team4999.lights.commands.Command
import org.usfirst.frc.team4999.lights.commands.SyncCommand

class I2CDisplay : AutoCloseable, Display {
    companion object {
        val I2C_ADDRESS = 16
        val SYNC_FREQ = 1000
    }

    private class I2CImpl : AutoCloseable {
        private val context = Pi4J.newAutoContext()
        private val i2c: I2C

        init {
            val provider = context.provider<I2CProvider>("pigpio-i2c")
            val config = I2C.newConfigBuilder(context)
                // id - some unique identifier for this resource
                .id("NeoPixels")
                .bus(1)
                // device - the i2c address of the target chip
                .device(I2C_ADDRESS).build()
            i2c = provider.create(config)
        }

        fun writePacket(packet: Packet): Int {
            return i2c.write(packet.data)
        }

        override fun close() {
            i2c.close()
            context.shutdown()
        }
    }

    private val i2c = I2CImpl()
    private val syncPacket = SyncCommand().build()

    private var sync_idx = 0;

    override fun close() {
        i2c.close()
    }

    override fun show(commands: Array<out Command>?) {
        if(++sync_idx > SYNC_FREQ) {
            i2c.writePacket(syncPacket)
            sync_idx = 0
        }

        if(commands == null) {
            return
        }

        commands.forEach {
            i2c.writePacket(it.build())
        }
    }


}