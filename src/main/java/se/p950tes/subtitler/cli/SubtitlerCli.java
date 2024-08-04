package se.p950tes.subtitler.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "subtitler", 
		description = "Scrubs HI notations and other junk data from subtitle files")
public class SubtitlerCli implements Callable<Integer> {

	private final SubtitlerCliExecutor executor;
	
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
    
    public SubtitlerCli(SubtitlerCliExecutor executor) {
		this.executor = executor;
	}
    
    @Override
    public Integer call() {
    	executor.setFiles(files);
    	executor.setInPlaceEditEnabled(inPlaceEdit.isPresent());
    	executor.setBackupSuffix(resolveBackupFileSuffix(inPlaceEdit));
    	executor.setVerbose(verbose);
    	return executor.execute();
    }
    
    private static Optional<String> resolveBackupFileSuffix(Optional<String> inPlaceEdit) {
    	if (inPlaceEdit.isEmpty()) {
    		return Optional.empty();
    	}
    	String backupSuffix = StringUtils.trimToNull(inPlaceEdit.get());
    	return Optional.ofNullable(backupSuffix);
    }
}
