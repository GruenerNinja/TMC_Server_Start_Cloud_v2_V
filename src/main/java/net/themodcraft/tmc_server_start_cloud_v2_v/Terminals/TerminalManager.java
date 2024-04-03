package net.themodcraft.tmcserverstartcloudv2.Terminals;

import java.io.IOException;

public class TerminalManager {

    public static long openTerminalAndGetId() {
        try {
            // Execute command to open a new Terminal window
            Process process = new ProcessBuilder("open", "-a", "Terminal").start();

            // Get the PID of the opened Terminal window
            return process.pid();
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Return -1 if an error occurs
        }
    }
}
