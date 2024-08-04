package se.p950tes.subtitler.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class FileManager {

	private final Optional<String> backupFileSuffix;
	private final boolean verbose;
	
	public FileManager(Optional<String> backupFileSuffix, boolean verbose) {
		this.backupFileSuffix = backupFileSuffix;
		this.verbose = verbose;
	}

	public boolean validateInputFile(Path file, boolean shouldBeWriteable) {
		if ((! Files.exists(file)) || (! Files.isRegularFile(file)) || (! Files.isReadable(file))) {
			System.err.println("File does not exist: " + file.toAbsolutePath());
			return false;
		}
		if (shouldBeWriteable) {
			if (! Files.isWritable(file)) {
				System.err.println("File is not writeable: " + file.toAbsolutePath());
				return false;
			}
		}
		if (backupFileSuffix.isPresent()) {
			Path backupFile = resolveBackupFile(file);
			if (Files.exists(backupFile)) {
				System.err.println("Backup file already exists: " + backupFile.toAbsolutePath());
				return false;
			}
		}
		return true;
	}
	
	public List<String> readLinesFromFile(Path file) {
		try {
			return Files.readAllLines(file);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file: " + file, e);
		}
	}
	
	public PrintStream openPrintOutputStream(Path file) throws IOException {
		OutputStream fileOutputStream = Files.newOutputStream(file);
		return new PrintStream(fileOutputStream);
	}
	
	public Path backupInputFile(Path file) {
		try {
			Path backupFile = resolveBackupFile(file);
			printVerbose("Backing up original file to " + backupFile);
			
			Files.copy(file, backupFile);
			return backupFile;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to backup original file", e);
		}
	}
	
	private Path resolveBackupFile(Path file) {
		String originalFileName = file.getFileName().toString();
		String backupFileName = originalFileName + backupFileSuffix.get();
		Path directory = file.toAbsolutePath().getParent();
		
		File backupFile = new File(directory.toFile(), backupFileName);
		return backupFile.toPath();
	}
	
	private void printVerbose(String line) {
		if (verbose) {
			System.out.println(line);
		}
	}
}
