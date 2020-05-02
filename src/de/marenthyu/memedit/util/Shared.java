package de.marenthyu.memedit.util;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shared {
    static Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
    static User32 user32 = Native.load("user32", User32.class);

    public static int PROCESS_VM_READ = 0x0010;
    public static int PROCESS_VM_WRITE = 0x0020;
    public static int PROCESS_VM_OPERATION = 0x0008;

    public static int getProcessId(String window) {
        IntByReference pid = new IntByReference(0);
        user32.GetWindowThreadProcessId(user32.FindWindowA(null, window), pid);

        return pid.getValue();
    }

    public static Pointer openProcess(int permissions, int pid) {
        Pointer process = kernel32.OpenProcess(permissions, true, pid);
        return process;
    }

    public static long findDynAddress(Pointer process, int[] offsets, long baseAddress) {

        long pointer = baseAddress;

        int size = 4;
        Memory pTemp = new Memory(size);
        long pointerAddress = 0;
        // System.out.println("initial pointerAddress: " + String.format("0x%X", pointer));
        for (int i = 0; i < offsets.length; i++) {
            if (i == 0) {
                kernel32.ReadProcessMemory(process, pointer, pTemp, size, null);
            }

            pointerAddress = ((pTemp.getInt(0) + offsets[i]));

            // System.out.println("Current pointerAddress: " + String.format("0x%X", pointerAddress));

            if (i != offsets.length - 1)
                kernel32.ReadProcessMemory(process, pointerAddress, pTemp, size, null);


        }

        return pointerAddress;
    }

    public static Memory readMemory(Pointer process, long address, int bytesToRead) {
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);

        kernel32.ReadProcessMemory(process, address, output, bytesToRead, read);
        return output;
    }

    public static void writeMemory(Pointer process, long address, byte[] data) {
        int size = data.length;
        Memory toWrite = new Memory(size);

        for (int i = 0; i < size; i++) {
            toWrite.setByte(i, data[i]);
        }

        boolean b = kernel32.WriteProcessMemory(process, address, toWrite, size, null);
    }

    public static byte[] intToBytes(final int data) {
        return new byte[]{
                (byte) ((data >> 0) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 24) & 0xff)
        };
    }

    public static int getBaseAddress(String executableName) throws IOException {
        String command = "powershell.exe  \"$modules = Get-Process " + executableName.split("\\.")[0] + " -Module; $modules[0].BaseAddress;\"";
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        while ((line = stdout.readLine()) != null) {
            output.append(line);
        }
        stdout.close();
        return Integer.parseInt(output.toString());
    }

}
