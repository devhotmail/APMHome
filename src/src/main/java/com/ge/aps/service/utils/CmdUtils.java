package com.ge.aps.service.utils;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

import org.apache.log4j.Logger;

public class CmdUtils {
    private static final Logger logger = Logger.getLogger(CmdUtils.class.getName());

    public static String os = null;

    public static final String OS_LINUX = "linux";
    public static final String OS_WINDOWS = "windows";
    public static final String OS_OTHERS = "others";

    public static final String WINFILE_SUFFIX = ".bat";
    public static final String LINFILE_SUFFIX = "";
    public static final String OTHFILE_SUFFIX = "";

    public static class CollectingLogOutputStream extends LogOutputStream {

        private final List<String> lines = new LinkedList<String>();

        @Override
        protected void processLine(String line, int level) {
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }
    }
    public static void main(String[] args) {
        try {
            CmdUtils.execCommand("C:/GE/dcmdump.exe C:/GE/1.dcm");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int execCommand(String command) {
        try{
            logger.error("***CMD=" + command);
            CollectingLogOutputStream stdout = new CollectingLogOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler(stdout);
            CommandLine cl = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setStreamHandler(psh);
            exec.execute(cl);

            for (String str : stdout.lines) {
                String result = str.toLowerCase();
                if (result.contains("error") || result.contains("cannot") || result.contains("failed")) {
                    logger.error("***CMD Failed: " + command + ", Msg="+str);
                    return -1;
                }
            }
            
            return 0;
        }
        catch(Exception ex){
            logger.error("***CMD ERR: " + command, ex);
            return -1;
        }
    }

}
