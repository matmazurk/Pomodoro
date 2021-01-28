package com.mat.pomodoro

import java.io.IOException
import kotlin.properties.Delegates

class App(
    private val configuration: Configuration
) : Timer.Callbacks {
    private var running = false
    private var state: State by Delegates.observable(State.WORK) { _, _, newState: State ->
        timer.stateTransition(newState)
    }
    private var intervals = 0
    private var breaks = 0
    private var longBreaks = 0
    private val timer = Timer(configuration, this)
    private val commands = mapOf(
        "stats" to {
            println("Intervals:$intervals, breaks:$breaks, long breaks:$longBreaks.")
            println(timer.getStats())
        },
        "break" to {
            if (state != State.BREAK) {
                intervals++
                state = State.BREAK
                timer.reset()
                println("Break duration:${Timer.formatSeconds(configuration.breakDuration * Timer.MINUTE)}")
            } else {
                println("You are currently in break state.")
            }
        },
        "work" to {
            if (state != State.WORK) {
                breaks++
                state = State.WORK
                timer.reset()
                println("Work duration:${Timer.formatSeconds(configuration.interval * Timer.MINUTE)}")
            } else {
                println("You are currently in work state.")
            }
        },
        "resume" to {
            timer.start()
        },
        "pause" to {
            timer.stop()
        },
        "finish" to {
            running = false
            timer.stop()
            println("Intervals:$intervals, breaks:$breaks, long breaks:$longBreaks.")
            println("Exiting...")
        }
    )

    fun start() {
        running = true
        timer.reset()
        timer.start()
        while (running) {
            val read: String?
            try {
                read = readLine()
            } catch (e: IOException) {
                break
            }
            if (read in commands.keys) {
                commands.getValue(read!!).invoke()
                println()
            } else if (read == null) {
                break
            } else {
                println("Wrong command. Available:${commands.keys}")
            }
        }
    }

    private fun popSystemNotify(text: String) =
        ProcessBuilder("notify", "Pomodoro", text).start()

    enum class State {
        WORK,
        BREAK,
        LONG_BREAK
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val configuration = Configuration.getConfigurationFromArgs(args) ?: return
            val app = App(configuration)
            app.start()
        }
    }

    override fun intervalEnded() {
        println("Working block no.$intervals ended. Switch to break.")
        popSystemNotify("Working interval ended. Switch to break.")
    }

    override fun breakEnded() {
        println("Short break no.$breaks has ended. Switch to working block.")
        popSystemNotify("Short break ended. Switch to working interval.")
    }

    override fun wholeWorkEnded() {
        popSystemNotify("You have finished ${Timer.formatSeconds(configuration.totalWorkTime * Timer.MINUTE)} of work.")
        commands.getValue("finish").invoke()
        System.`in`.close()
    }
}
