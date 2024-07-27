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
	
    public SubtitleScrubber(boolean inPlaceEdit, String backupFileSuffix) {
		this.inPlaceEdit = inPlaceEdit;
		this.backupFileSuffix = Optional.ofNullable(backupFileSuffix);
	}

	public void processFile(Path file) {
        System.out.println("Processing: " + file);

        SubtitleParser parser = new SubtitleParser();
        Subtitle subtitle = parser.parse(file);

        EntryScrubber scrubber = new EntryScrubber();
        
        var entries = subtitle.getEntries().stream()
        		.map(scrubber::scrub)
        		.filter(SubtitleEntry::isNotEmpty)
        		.toList();

        correctIndexes(entries);
        subtitle.setEntries(entries);
        
        if (inPlaceEdit) {
        	if (backupFileSuffix.isPresent()) {
        		backupInputFile(file);
        	}
        	replaceInputFile(subtitle);
        } else {
        	writeSubtitleContents(subtitle, System.out);
        }
    }

    private void backupInputFile(Path file) {
    	try {
	    	String originalFileName = file.getFileName().toString();
	    	String backupFileName = originalFileName + backupFileSuffix.get();
	    	System.out.println("Backing up original file to " + backupFileName);
	    	
			Path directory = file.toAbsolutePath().getParent();
			
			File backupFile = new File(directory.toFile(), backupFileName);
			Files.copy(file, backupFile.toPath());
    	} catch (Exception e) {
    		throw new IllegalStateException("Failed to backup original file", e);
    	}
	}

	private void replaceInputFile(Subtitle subtitle) {
		System.out.println("Overwriting original file to " + subtitle.getPath());
		try (PrintStream fileOutputStream = new PrintStream(Files.newOutputStream(subtitle.getPath()))){
			
			writeSubtitleContents(subtitle, fileOutputStream);
			
		} catch (Exception e) {
			throw new IllegalStateException("Failed to overwrite original file", e);
		}
	}
    
    private void writeSubtitleContents(Subtitle subtitle, PrintStream outputStream) {
    	for (SubtitleEntry entry : subtitle.getEntries()) {
    		outputStream.println(entry.toFormattedEntry());
    		outputStream.println();
    	}
    }

    private void correctIndexes(List<SubtitleEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setIndex(i + 1);
        }
    }
}
