package com.momentum4999.lights.pi

import org.usfirst.frc.team4999.lights.BrightnessFilter
import org.usfirst.frc.team4999.lights.BufferDisplay
import org.usfirst.frc.team4999.lights.Color
import org.usfirst.frc.team4999.lights.Display
import org.usfirst.frc.team4999.lights.commands.Command

class TerminalDisplay(
    numPix : Int = 80
): Display {

    private val renderer = BufferDisplay(numPix)

    init {
        BrightnessFilter.setBrightness(1.0)
        renderer.addBufferListener { colors ->
            //return@addBufferListener
            val printColor: (Color) -> Unit = { color ->
                System.out.print("\u001B[38;2;")
                System.out.print(color.getRed().toString() + ";")
                System.out.print(color.getGreen().toString() + ";")
                System.out.print(color.getBlue().toString() + "m")
            }

            System.out.print("\u001B[2K\r")
            var currColor = colors[0]
            printColor(currColor)
            colors.forEach {
                if(it != currColor) {
                    currColor = it
                    printColor(it)
                }
                System.out.print("\u2588")
            }
        }
    }

    override fun show(commands: Array<out Command>?) {
        renderer.show(commands)
    }
}