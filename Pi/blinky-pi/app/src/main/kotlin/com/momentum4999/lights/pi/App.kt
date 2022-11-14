package com.momentum4999.lights.pi

import com.momentum4999.lights.pi.audiostream.AudioAnimation
import com.momentum4999.lights.pi.audiostream.AudioInfoSource
import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.networktables.NetworkTableInstance
import org.usfirst.frc.team4999.lights.Animator
import org.usfirst.frc.team4999.lights.BlockingAnimator
import org.usfirst.frc.team4999.lights.Color
import org.usfirst.frc.team4999.lights.ColorTools
import org.usfirst.frc.team4999.lights.animations.AnimationSequence
import org.usfirst.frc.team4999.lights.animations.Snake
import org.usfirst.frc.team4999.lights.animations.Solid

class App {
    companion object {
        val rainbowcolors = arrayOf(
            Color(72, 21, 170),
            Color(55, 131, 255),
            Color(77, 233, 76),
            Color(255, 238, 0),
            Color(255, 140, 0),
            Color(246, 0, 0)
        )

        val TEAM = 4999
    }

    private interface RunMode {
        fun setUp(animator: Animator): Unit
        fun tearDown(): Unit
    }

    private val modes = mapOf<String, RunMode>(
        "default" to object: RunMode {
            private val rainbowTails = ColorTools.getColorTails(rainbowcolors, Color.BLACK, 12, 20)
            private val momentumTails = ColorTools.getColorTails(
                arrayOf(Color.MOMENTUM_BLUE, Color.MOMENTUM_PURPLE),
                Color.BLACK, 24, 32
            )

            private val animation = AnimationSequence(arrayOf(
                AnimationSequence.AnimationSequenceMember(
                    Snake(rainbowTails, 15),
                    60000
                ),
                AnimationSequence.AnimationSequenceMember(
                    Snake(momentumTails, 15),
                    60000
                )
            ))
            override fun setUp(animator: Animator) {
                animator.setAnimation(animation)
            }

            override fun tearDown() {}
        },
        "audio" to object: RunMode {
            private var infoSource: AudioInfoSource? = null
            private var animation: AudioAnimation? = null

            override fun setUp(animator: Animator) {
                infoSource = AudioInfoSource()
                animation = AudioAnimation(infoSource!!)
                animator.setAnimation(animation)
            }

            override fun tearDown() {
                infoSource?.close()
                infoSource = null
                animation = null
            }
        },
        "measure" to object: RunMode {
            private val animation = Solid(arrayOf(
                Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE,
                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK
            ))

            override fun setUp(animator: Animator) {
                animator.setAnimation(animation)
            }

            override fun tearDown() {}
        }
    )

    private lateinit var animator: Animator
    private var currentMode: RunMode? = null

    fun setRunMode(modeName: String) {
        currentMode?.tearDown()
        currentMode = modes[modeName] ?: modes["default"]
        currentMode?.setUp(animator)
    }

    private fun setupNetworkTables() {
        val instance = NetworkTableInstance.getDefault()
        val table = instance.getTable("leds")
        val modeEntry = table.getEntry("run_mode")
        instance.startClientTeam(TEAM)
        val btEntry = table.getEntry("enable_bluetooth")

        if(!modeEntry.exists()) {
            modeEntry.setString("default")
        }

        if(!btEntry.exists()) {
            btEntry.setBoolean(true)
        }

        modeEntry.addListener({ notification ->
            setRunMode(notification.value.string)
        }, EntryListenerFlags.kUpdate or EntryListenerFlags.kNew or EntryListenerFlags.kImmediate)

        btEntry.addListener({ notification ->
            if(notification.value.boolean) {
                BluetoothManager.enable()
            } else {
                BluetoothManager.disable()
            }
        }, EntryListenerFlags.kUpdate or EntryListenerFlags.kNew or EntryListenerFlags.kImmediate)
    }

    fun main() {
        // I2CDisplay().use { display ->
        TerminalDisplay().also { display ->
            val animator = BlockingAnimator(display, Solid(Color.BLACK))
            this.animator = animator

            setRunMode("default")
            setupNetworkTables()

            animator.animate()
        }
    }
}