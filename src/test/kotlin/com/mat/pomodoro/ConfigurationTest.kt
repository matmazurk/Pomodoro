package com.mat.pomodoro

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ConfigurationTest {

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }

    @Test
    fun testConfigurationArgsWithoutInterval() {
        val args = arrayOf(
            "-$BREAK_STRING_SHORT=10"
        )
        val configuration = Configuration.getConfigurationFromArgs(args)
        val out = String(errContent.toByteArray())
        assert(out == "$NO_WORK_INTERVAL_STRING\n")
        assert(configuration == null)
    }

    @Test
    fun testConfigurationArgsWithoutBreak() {
        val args = arrayOf(
            "--$WORK_INTERVAL_STRING=5"
        )
        val configuration = Configuration.getConfigurationFromArgs(args)
        val out = String(errContent.toByteArray())
        assert(out == "$NO_BREAK_STRING\n")
        assert(configuration == null)
    }

    @Test
    fun testConfigurationArgsWithWrongInterval() {
        val args = arrayOf(
            "--$WORK_INTERVAL_STRING=ads",
            "-$BREAK_STRING_SHORT=32"
        )
        val configuration = Configuration.getConfigurationFromArgs(args)
        val out = String(errContent.toByteArray())
        assert(out == "$WRONG_WORK_INTERVAL_VALUE_STRING\n")
        assert(configuration == null)
    }

    @Test
    fun testConfigurationArgsWithWrongBreak() {
        val args = arrayOf(
            "--$WORK_INTERVAL_STRING=123",
            "-$BREAK_STRING_SHORT=-43"
        )
        val configuration = Configuration.getConfigurationFromArgs(args)
        val out = String(errContent.toByteArray())
        assert(out == "$WRONG_BREAK_VALUE_STRING\n")
        assert(configuration == null)
    }

    @Test
    fun testConfigurationArgsWithoutTotalTime() {
        val args = arrayOf(
            "--$WORK_INTERVAL_STRING=123",
            "-$BREAK_STRING_SHORT=43"
        )
        val configuration = Configuration.getConfigurationFromArgs(args)
        assert(configuration != null)
        assert(configuration!!.totalWorkTime == 0L)
    }
}
