package com.momentum4999.lights.pi.audiostream

import java.lang.ProcessBuilder
import java.lang.Process
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioInfoSource : AutoCloseable {
    class AudioInfo {
        var averageAmplitude: Short = 0
        var isBeat: Boolean = false
    }

    private val subprocess: Process
    private val dataStream: InputStream

    private var dataBuff = byteArrayOf(0, 0, 0)
    private var dataByteBuff = ByteBuffer.wrap(dataBuff).order(ByteOrder.LITTLE_ENDIAN)

    init {
        val builder = ProcessBuilder("/home/jordan/blinky-lights/Pi/audio-shim/shim")
        builder.redirectOutput(ProcessBuilder.Redirect.PIPE)
        builder.redirectError(ProcessBuilder.Redirect.DISCARD)
        subprocess = builder.start()
        dataStream = subprocess.getInputStream()
    }

    override fun close() {
        subprocess.destroy()
    }

    fun getInfo(info: AudioInfo): Unit {
        if(dataStream.available() < 3) {
            return
        }
        dataStream.readNBytes(dataBuff, 0, 3)

        dataByteBuff.rewind()
        info.averageAmplitude = dataByteBuff.getShort()
        info.isBeat = dataByteBuff.get() != 0.toByte()
    }
}
