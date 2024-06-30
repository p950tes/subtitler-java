package se.p950tes.subtitler;

import picocli.CommandLine;
import se.p950tes.subtitler.cli.SubtitlerCli;


public class SubtitlerApplication {

    public static void main(String[] args) {
        int exitCode = new SubtitlerApplication().execute(args);
        System.exit(exitCode);
    }
    
    private int execute(String[] args) {
    	CommandLine commandLine = new CommandLine(new SubtitlerCli());
//      return commandLine.execute(args);
    	return commandLine.execute(new String[]{"-x", "/home/user/Downloads/media/3_English.srt"});    	
    }
}
