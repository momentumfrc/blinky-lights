package com.momentum4999.lights.pi.audiostream

import com.momentum4999.lights.pi.App.Companion.rainbowcolors
import org.usfirst.frc.team4999.lights.Color
import org.usfirst.frc.team4999.lights.animations.Animation
import org.usfirst.frc.team4999.lights.commands.Command
import org.usfirst.frc.team4999.lights.commands.RunCommand
import org.usfirst.frc.team4999.lights.commands.StrideCommand
import kotlin.math.max
import kotlin.math.min

class AudioAnimation(
    private val infoSource: AudioInfoSource,
    private val options: AudioAnimationOptions = AudioAnimationOptions()
) : Animation {
    companion object {
        private val GAIN: Double = 3.0
        private val AVERAGE_WINDOW = 5
    }

    class AudioAnimationOptions {
        var barPoints: Array<Int> = arrayOf(40)
        var barSize = 40
        var colors: Array<Color> = rainbowcolors
        var background: Color = Color.BLACK
        var refreshDelay: Int = 1
    }

    private val info = AudioInfoSource.AudioInfo()
    private var currColorIdx = 0

    private val averageArray = Array<Short>(AVERAGE_WINDOW) { 0 }
    private var average: Short = 0
    private var averageIdx = 0

    private var debounce = false

    override fun getNextFrame(): Array<Command> {
        infoSource.getInfo(info)
        if(info.isBeat) {
            if(!debounce) {
                currColorIdx = (currColorIdx + 1) % options.colors.size
            }
            debounce = true
        } else {
            debounce = false
        }
        val currColor = options.colors[currColorIdx]

        val commands = mutableListOf<Command>(StrideCommand(0, options.background, 1, 1))

        val prevAvg = averageArray[averageIdx]
        averageArray[averageIdx] = info.averageAmplitude
        averageIdx = (averageIdx + 1) % AVERAGE_WINDOW
        average = (average + (info.averageAmplitude - prevAvg) / AVERAGE_WINDOW).toShort()

        val amplitude = (min((average * GAIN) / Short.MAX_VALUE, 1.0) * options.barSize).toInt()
        commands.addAll(options.barPoints.map {
            val start = max(it - amplitude, 0)
            val end = it + amplitude + 1
            val length = end - start
            RunCommand(start, currColor, length)
        })

        return commands.map{ it.dim() }.toTypedArray()
    }

    override fun getFrameDelayMilliseconds(): Int {
        return options.refreshDelay
    }
}