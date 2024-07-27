package se.p950tes.subtitler.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import se.p950tes.subtitler.model.Subtitle;
import se.p950tes.subtitler.model.SubtitleEntry;

public class SubtitleParser {

    private static final Pattern INDEX_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^[\\d:,]+\\s+" + Pattern.quote("-->") + "\\s+[\\d:,]+$");

    private static enum Type {
        INDEX, TIMESTAMP, CONTENT;
    }

    private final List<SubtitleEntry> entries = new ArrayList<>();

    private String currentIndex;
    private String currentTimestamp;
    private List<String> currentContent = new ArrayList<>();
    private Type lastParsed;

    public Subtitle parse(Path file) {

        List<String> lines = readLinesFromFile(file);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String nextLine = i < (lines.size() - 1) ? lines.get(i + 1).trim() : null;

            if (line.isEmpty()) {
                continue;
            }
            if (nextLine != null && looksLikeIndex(line) && looksLikeTimestamp(nextLine)) {
                reset();
                currentIndex = line;
                lastParsed = Type.INDEX;
                continue;
            }

            if (currentIndex != null && looksLikeTimestamp(line)) {
                currentTimestamp = line;
                lastParsed = Type.TIMESTAMP;
                continue;
            }

            if (lastParsed == Type.TIMESTAMP || lastParsed == Type.CONTENT) {
                currentContent.add(line);
                lastParsed = Type.CONTENT;
                continue;
            }
        
            reset();
        }
        
        return new Subtitle(file, entries);
    }

    private void reset() {
        if (currentIndex != null && currentTimestamp != null) {
            var entry = new SubtitleEntry(Integer.parseInt(currentIndex), currentTimestamp, currentContent);
            entries.add(entry);
        }
        lastParsed = null;
        currentIndex = null;
        currentTimestamp = null;
        currentContent = new ArrayList<>();
    }
    private static boolean looksLikeIndex(String line) {
        return INDEX_PATTERN.matcher(line).matches();
    }
    private static boolean looksLikeTimestamp(String line) {
        return TIMESTAMP_PATTERN.matcher(line).matches();
    }

    private List<String> readLinesFromFile(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file, e);
        }
    }
}
