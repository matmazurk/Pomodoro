package com.mat.pomodoro

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Timer(
    private val configuration: Configuration,
    private val handler: Callbacks
) {
    var timer = 0L
    private var state: App.State = App.State.WORK
    var totalTimer = 0L

    fun start() {
        running = true
        timerCoroutine()
    }

    fun reset() {
        timer = 0
    }

    fun stop() {
        running = false
    }

    fun getStats(): String {
        val totalPhaseTime = if (state == App.State.WORK) configuration.interval else configuration.breakDuration
        return "Current timer: ${formatSeconds(timer)}, total timer:${formatSeconds(totalTimer)}.\n" +
            "Remaining to end current $state:${formatSeconds(totalPhaseTime * MINUTE - timer)}, " +
            "to end whole work:${formatSeconds(configuration.totalWorkTime * MINUTE - totalTimer)}."
    }

    fun stateTransition(newState: App.State) {
        state = newState
    }

    private fun timerCoroutine() =
        GlobalScope.launch {
            while (running) {
                delay(1000)
                timer++
                totalTimer++
                when (state) {
                    App.State.WORK -> when {
                        (totalTimer >= configuration.totalWorkTime * MINUTE) -> {
                            handler.wholeWorkEnded()
                        }
                        (
                            timer >= configuration.interval * MINUTE &&
                                (timer - configuration.interval * MINUTE) % REMAINDER_INTERVAL == 0L
                            ) -> {
                            handler.intervalEnded()
                        }
                    }
                    App.State.BREAK -> when {
                        (totalTimer >= configuration.totalWorkTime * MINUTE) -> {
                            handler.wholeWorkEnded()
                        }
                        (
                            timer >= configuration.breakDuration * MINUTE &&
                                (timer - configuration.breakDuration * MINUTE) % REMAINDER_INTERVAL == 0L
                            ) -> {
                            handler.breakEnded()
                        }
                    }
                    App.State.LONG_BREAK -> {
                    }
                }
            }
            println("exiting coroutine")
        }


    interface Callbacks {
        fun intervalEnded()
        fun breakEnded()
        fun wholeWorkEnded()
    }

    companion object {
        const val MINUTE = 60
        const val REMAINDER_INTERVAL = 15L
        private var running = false
        fun formatSeconds(seconds: Long): String {
            val absSeconds = Math.abs(seconds)
            val positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                absSeconds % 3600 / 60,
                absSeconds % 60
            )
            return if (seconds < 0) "-$positive" else positive
        }
    }
}
