package com.momentum4999.lights.pi

object BluetoothManager {

    private fun runRfkillCmd(cmd: String) {
        val builder = ProcessBuilder("/usr/sbin/rfkill", cmd, "bluetooth").also{
            it.inheritIO()
        }
        val process = builder.start()
        val termination = process.waitFor()
        if(termination != 0) {
            throw Exception("Non-zero return code!")
        }
    }

    fun enable() {
        runRfkillCmd("unblock")
    }

    fun disable() {
        runRfkillCmd("block")
    }
}