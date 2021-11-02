package me.ztowne13.customcrates.utils

import java.util.logging.Level

class Logging {
    companion object {
        fun log(level: Level, msg: String) {
            java.util.logging.Logger.getLogger("SpecialisedCrates").log(level, msg)
        }

        fun info(msg: String) {
            log(Level.INFO, msg)
        }

        fun warn(msg: String) {
            log(Level.WARNING, msg)
        }

        fun severe(msg: String) {
            log(Level.SEVERE, msg)
        }
    }
}