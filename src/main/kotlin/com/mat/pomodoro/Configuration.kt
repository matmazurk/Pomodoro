package com.mat.pomodoro

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.UnrecognizedOptionException

const val WORK_INTERVAL_STRING_SHORT = "i"
const val WORK_INTERVAL_STRING = "interval"
const val WORK_INTERVAL_DESCRIPTION = "specify working interval duration in min"
const val WRONG_WORK_INTERVAL_VALUE_STRING = "Work interval must be a positive integer number."
const val NO_WORK_INTERVAL_STRING = "You must provide work interval parameter."

const val BREAK_STRING_SHORT = "b"
const val BREAK_STRING = "break"
const val BREAK_DESCRIPTION = "specify break duration in min"
const val WRONG_BREAK_VALUE_STRING = "Break duration must be a positive integer number."
const val NO_BREAK_STRING = "You must provide break duration parameter."

const val TOTAL_WORK_DURATION_SHORT = "d"
const val TOTAL_WORK_DURATION = "duration"
const val TOTAL_WORK_DURATION_DESCRIPTION = "specify total work time in min"
const val WRONG_WORK_DURATION_VALUE_STRING = "Total working time must be a positive integer number."

class Configuration private constructor(
    val interval: Long,
    val breakDuration: Long,
    val totalWorkTime: Long
) {
    companion object {
        fun getConfigurationFromArgs(args: Array<String>): Configuration? {
            val commandLineParser = DefaultParser()
            val options = Options()
            options.addOption(
                WORK_INTERVAL_STRING_SHORT,
                WORK_INTERVAL_STRING,
                true,
                WORK_INTERVAL_DESCRIPTION
            )
            options.addOption(
                BREAK_STRING_SHORT,
                BREAK_STRING,
                true,
                BREAK_DESCRIPTION
            )
            options.addOption(
                TOTAL_WORK_DURATION_SHORT,
                TOTAL_WORK_DURATION,
                true,
                TOTAL_WORK_DURATION_DESCRIPTION
            )
            var interval = 0L
            var breakDurat = 0L
            var totalTime = 0L

            val commandLine: CommandLine
            try {
                commandLine = commandLineParser.parse(options, args)
            } catch (e: UnrecognizedOptionException) {
                val helpFormatter = HelpFormatter()
                helpFormatter.printHelp("Available options:", options)
                return null
            }
            if (commandLine.hasOption(WORK_INTERVAL_STRING)) {
                val workInterval = commandLine.getOptionValue(WORK_INTERVAL_STRING).toIntOrNull()
                if (workInterval == null) {
                    System.err.println(WRONG_WORK_INTERVAL_VALUE_STRING)
                    return null
                } else {
                    if (workInterval < 1) {
                        System.err.println(WRONG_WORK_INTERVAL_VALUE_STRING)
                        return null
                    } else {
                        interval = workInterval.toLong()
                    }
                }
            } else {
                System.err.println(NO_WORK_INTERVAL_STRING)
                return null
            }

            if (commandLine.hasOption(BREAK_STRING)) {
                val breakDur = commandLine.getOptionValue(BREAK_STRING_SHORT).toIntOrNull()
                if (breakDur == null) {
                    System.err.println(WRONG_BREAK_VALUE_STRING)
                    return null
                } else {
                    if (breakDur < 1) {
                        System.err.println(WRONG_BREAK_VALUE_STRING)
                        return null
                    } else {
                        breakDurat = breakDur.toLong()
                    }
                }
            } else {
                System.err.println(NO_BREAK_STRING)
                return null
            }

            if (commandLine.hasOption(TOTAL_WORK_DURATION)) {
                val total = commandLine.getOptionValue(TOTAL_WORK_DURATION).toIntOrNull()
                if (total == null) {
                    System.err.println(WRONG_WORK_DURATION_VALUE_STRING)
                    return null
                } else {
                    if (total < 1) {
                        System.err.println(WRONG_WORK_DURATION_VALUE_STRING)
                        return null
                    } else {
                        totalTime = total.toLong()
                    }
                }
            }
            return Configuration(
                interval,
                breakDurat,
                totalTime
            )
        }
    }
}
