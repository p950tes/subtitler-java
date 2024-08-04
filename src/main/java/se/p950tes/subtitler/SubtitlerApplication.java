package se.p950tes.subtitler;

import picocli.CommandLine;
import se.p950tes.subtitler.cli.SubtitlerCli;
import se.p950tes.subtitler.cli.SubtitlerCliExecutor;

public class SubtitlerApplication {

	public static void main(String[] args) {
		int exitCode = new SubtitlerApplication().execute(args);
		System.exit(exitCode);
	}

	
	private int execute(String[] args) {
		SubtitlerCli cli = new SubtitlerCli(new SubtitlerCliExecutor());
		
		CommandLine commandLine = new CommandLine(cli);
		return commandLine.execute(args);
	}
}
