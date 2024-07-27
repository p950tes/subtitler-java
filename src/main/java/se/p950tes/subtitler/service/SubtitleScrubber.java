package se.p950tes.subtitler.service;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import se.p950tes.subtitler.model.Subtitle;
import se.p950tes.subtitler.model.SubtitleEntry;

public class SubtitleScrubber {

	private final boolean inPlaceEdit;
	private final Optional<String> backupFileSuffix;
	private final boolean verbose;
	
    public SubtitleScrubber(boolean inPlaceEdit, String backupFileSuffix, boolean verbose) {
		this.inPlaceEdit = inPlaceEdit;
		this.backupFileSuffix = Optional.ofNullable(backupFileSuffix);
		this.verbose = verbose;
	}

	public void processFile(Path file) {
        print("Processing: " + file);

        SubtitleParser parser = new SubtitleParser();
        Subtitle subtitle = parser.parse(file);

        EntryScrubber scrubber = new EntryScrubber();
        
        var newEntries = subtitle.getEntries().stream()
        		.map(scrubber::scrub)
        		.filter(SubtitleEntry::isNotEmpty)
        		.toList();

        correctIndexes(newEntries);
        
        if (inPlaceEdit) {
        	if (backupFileSuffix.isPresent()) {
        		backupInputFile(file);
        	}
        	replaceInputFile(file, newEntries);
        } else {
        	writeSubtitleContents(newEntries, System.out);
        }
        
        printSummary(subtitle.getEntries(), newEntries);
    }

    private void printSummary(List<SubtitleEntry> oldEntries, List<SubtitleEntry> newEntries) {
    	print(" * Entries modified: " + newEntries.stream().filter(SubtitleEntry::isModified).count());
    	print(" * Entries removed: " + (oldEntries.size() - newEntries.size()));
    	print(" * Entries remaining: " + newEntries.size());
	}

	private void backupInputFile(Path file) {
    	try {
	    	String originalFileName = file.getFileName().toString();
	    	String backupFileName = originalFileName + backupFileSuffix.get();
    		printVerbose("Backing up original file to " + backupFileName);
	    	
			Path directory = file.toAbsolutePath().getParent();
			
			File backupFile = new File(directory.toFile(), backupFileName);
			Files.copy(file, backupFile.toPath());
    	} catch (Exception e) {
    		throw new IllegalStateException("Failed to backup original file", e);
    	}
	}

	private void replaceInputFile(Path originalPath, List<SubtitleEntry> entries) {
		printVerbose("Overwriting original file to " + originalPath);
		try (PrintStream fileOutputStream = new PrintStream(Files.newOutputStream(originalPath))){
			
			writeSubtitleContents(entries, fileOutputStream);
			
		} catch (Exception e) {
			throw new IllegalStateException("Failed to overwrite original file", e);
		}
	}
    
    private void writeSubtitleContents(List<SubtitleEntry> entries, PrintStream outputStream) {
    	for (SubtitleEntry entry : entries) {
    		outputStream.println(entry.toFormattedEntry());
    		outputStream.println();
    	}
    }

    private void correctIndexes(List<SubtitleEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setIndex(i + 1);
        }
    }
    
    private void print(String line) {
    	System.out.println(line);
    }
    private void printVerbose(String line) {
    	if (verbose) {
    		print(line);
    	}
    }
}
