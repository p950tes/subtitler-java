package se.p950tes.subtitler.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import se.p950tes.subtitler.service.SubtitleScrubber;
import se.p950tes.subtitler.util.FileManager;

public class SubtitlerCliExecutor {

	private List<Path> files;
	private boolean inPlaceEditEnabled;
	private Optional<String> backupSuffix;
	private boolean verbose;
	
	int execute() {
    	FileManager fileManager = new FileManager(backupSuffix, verbose);
    	
        if (! validateFiles(fileManager)) {
            return -1;
        }
        
        boolean backupOriginalFile = backupSuffix.isPresent();
        SubtitleScrubber scrubber = new SubtitleScrubber(fileManager, inPlaceEditEnabled, backupOriginalFile, verbose);
        for (Path file : files) {
        	scrubber.processFile(file);
        }
        return 0;
    }	
    
    private boolean validateFiles(FileManager fileManager) {
    	boolean success = true;
    	
        for (Path file : files) {
        	if (! fileManager.validateInputFile(file, inPlaceEditEnabled)) {
        		success = false;
        	}
        }
        return success;
    }

	void setFiles(List<Path> files) {
		this.files = files;
	}
	void setInPlaceEditEnabled(boolean inPlaceEditEnabled) {
		this.inPlaceEditEnabled = inPlaceEditEnabled;
	}
	void setBackupSuffix(Optional<String> backupSuffix) {
		this.backupSuffix = backupSuffix;
	}
	void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
