package com.jwcrain.sandcastle.appendonlylog;

import java.io.*;

public class Log {
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Log(String path) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path)));
            bufferedReader = new BufferedReader(new FileReader(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean append(String string) {
        try {
            bufferedWriter.write(string);
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BufferedReader getReader() {
        return bufferedReader;
    }
}
