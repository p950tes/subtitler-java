package se.p950tes.subtitler.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import se.p950tes.subtitler.service.SubtitleScrubber;

@Command(name = "subtitler", 
		description = "Scrubs HI notations and other junk data from subtitle files")
public class SubtitlerCli implements Callable<Integer> {

	@Option(names = {"-i", "--in-place"}, 
			description = "Edit files in place. If a suffix parameter is specified then the original file will be kept behind with the specified suffix.", 
			paramLabel = "backupSuffix", 
			required = false, 
			arity = "0..1", 
			fallbackValue = "", 
			preprocessor = DisableSpaceSeparatorPreprocessor.class)
	private Optional<String> inPlaceEdit;
	
    @Option(names = {"-v", "--verbose"}, 
    		description = "Verbose mode", 
    		defaultValue = "false")
    private boolean verbose;
    
    @Parameters(paramLabel = "files", 
    		description = "File(s)", 
    		arity = "1..*")
    private List<Path> files;
    
    @Override
    public Integer call() {
        if (! validateFiles()) {
            return -1;
        }
        
        boolean inPlaceEditEnabled = inPlaceEdit.isPresent();
        String backupSuffix = null;
        if (inPlaceEditEnabled) {
        	backupSuffix = StringUtils.trimToNull(inPlaceEdit.get());
        }
        
        SubtitleScrubber scrubber = new SubtitleScrubber(inPlaceEditEnabled, backupSuffix);
        for (Path file : files) {
        	scrubber.processFile(file);
        }
        return 0;
    }

    private boolean validateFiles() {
        for (Path file : files) {
            if ((! Files.exists(file)) || (! Files.isRegularFile(file))) {
                System.err.println("File does not exist: " + file.toAbsolutePath());
                return false;
            }
            if ((! Files.isReadable(file)) || (! Files.isWritable(file))) {
                System.err.println("File is not accessible: " + file.toAbsolutePath());
                return false;
            }
        }
        return true;
    }
}
