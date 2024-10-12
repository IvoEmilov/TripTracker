package com.example.triptracker;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class LogWriter {

    private final static String logLocation = "storage/emulated/0/Download/";
    private final static String logFileName = "OBD_Log.txt";
    private final static String errorFileName = "OBD_Errors.txt";

    public static void write(String TAG, String logMessage){
        Log.d(TAG, logMessage);
        appendFileLog(TAG.concat(logMessage), logFileName);
    }

    public static void writeError(String TAG, String logMessage){
        Log.d(TAG, logMessage);
        appendFileLog(TAG.concat(logMessage), errorFileName);
    }

    private static void appendFileLog(String text, String filename)
    {
        File logFile = new File(logLocation+filename);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append("["+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())+"]: ");
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
