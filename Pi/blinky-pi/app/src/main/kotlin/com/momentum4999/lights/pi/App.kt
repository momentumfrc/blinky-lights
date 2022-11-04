package com.momentum4999.lights.pi

import org.usfirst.frc.team4999.lights.BlockingAnimator
import org.usfirst.frc.team4999.lights.Color
import org.usfirst.frc.team4999.lights.ColorTools
import org.usfirst.frc.team4999.lights.animations.Animation
import org.usfirst.frc.team4999.lights.animations.AnimationSequence
import org.usfirst.frc.team4999.lights.animations.AnimationSequence.AnimationSequenceMember
import org.usfirst.frc.team4999.lights.animations.Snake
import org.usfirst.frc.team4999.lights.animations.Stack


fun getDefaultAnimation(): Animation {
    val rainbowcolors = arrayOf(
        Color(72, 21, 170),
        Color(55, 131, 255),
        Color(77, 233, 76),
        Color(255, 238, 0),
        Color(255, 140, 0),
        Color(246, 0, 0)
    )
    val rainbowTails = ColorTools.getColorTails(rainbowcolors, Color.BLACK, 12, 20)
    val momentumTails = ColorTools.getColorTails(
        arrayOf(Color.MOMENTUM_BLUE, Color.MOMENTUM_PURPLE),
        Color.BLACK, 24, 32
    )

    return AnimationSequence(
        arrayOf(
            AnimationSequenceMember(
                Snake(rainbowTails, 7),
                5000
            ),
            AnimationSequenceMember(
                Snake(ColorTools.getSmearedColors(rainbowcolors, 16), 5),
                1500
            ),
            AnimationSequenceMember(
                Snake(momentumTails, 7),
                5000
            ),
            AnimationSequenceMember(
                Stack(rainbowcolors, 20, 20),
                1500
            )
        )
    )
}

fun main() {
    I2CDisplay().use {
        val animation = getDefaultAnimation()
        val animator = BlockingAnimator(it, animation)

        animator.animate()
    }


}
