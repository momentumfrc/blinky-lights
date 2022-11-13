package com.momentum4999.lights.pi

import com.momentum4999.lights.pi.audiostream.AudioAnimation
import com.momentum4999.lights.pi.audiostream.AudioInfoSource
import org.usfirst.frc.team4999.lights.BlockingAnimator
import org.usfirst.frc.team4999.lights.Color
import org.usfirst.frc.team4999.lights.ColorTools
import org.usfirst.frc.team4999.lights.animations.*
import org.usfirst.frc.team4999.lights.animations.AnimationSequence.AnimationSequenceMember

val rainbowcolors = arrayOf(
    Color(72, 21, 170),
    Color(55, 131, 255),
    Color(77, 233, 76),
    Color(255, 238, 0),
    Color(255, 140, 0),
    Color(246, 0, 0)
)

fun getDefaultAnimation(): Animation {
    val rainbowTails = ColorTools.getColorTails(rainbowcolors, Color.BLACK, 12, 20)
    val momentumTails = ColorTools.getColorTails(
        arrayOf(Color.MOMENTUM_BLUE, Color.MOMENTUM_PURPLE),
        Color.BLACK, 24, 32
    )

    return AnimationSequence(
        arrayOf(
            AnimationSequenceMember(
                Snake(rainbowTails, 15),
                60000
            ),
            AnimationSequenceMember(
                Snake(momentumTails, 15),
                60000
            )
        )
    )
}

fun main() {
    // I2CDisplay().use {
    TerminalDisplay().also { display ->
        //val animation = getDefaultAnimation()
        AudioInfoSource().use { infoSource ->
            val animation = AudioAnimation(infoSource)
            val animator = BlockingAnimator(display, animation)

            animator.animate()
        }

    }


}
