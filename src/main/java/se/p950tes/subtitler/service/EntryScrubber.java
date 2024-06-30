package se.p950tes.subtitler.service;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import se.p950tes.subtitler.model.SubtitleEntry;

public class EntryScrubber {

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^<>]*>");

    // (SCREAM) [SCREAM] {SCREAM}
    private static final Pattern BRACKET_PATTERN = Pattern.compile("[\\[\\{\\(][^\\[\\]\\{\\}\\(\\)]*[\\]\\}\\)]");
    
    // music junk artifacts (J“ / j“ / [M1  )
    private static final Pattern JUNK_PATTERN = Pattern.compile("(^([jJ]“)+|([jJ]“|)+$|^\\[M1$)");
    
    // All caps, at least 3 characters
    private static final Pattern ALL_CAPS_PATTERN = Pattern.compile("^[A-Z ,\\!]{3,}$");
    
    // Names followed by colon: (Guard 1: Hello there)
    private static final Pattern VOICE_INDICATORS_PATTERN = Pattern.compile("^[A-Za-z]{2}[A-Za-z0-9 ]*\\s?: ");

    
    public void scrub(SubtitleEntry entry) {
        
        String linesAsString = String.join("\n", entry.getLines());
        linesAsString = scrubEntry(linesAsString);

        var newLines = Arrays.stream(linesAsString.split("\n"))
        		.map(String::trim)
        		.filter(StringUtils::isNotBlank)
        		.toList();
        
        entry.setLines(newLines);
    }
    
    private String scrubEntry(String entry) {
        entry = removeAll(HTML_PATTERN, entry);
        entry = removeAll(BRACKET_PATTERN, entry);
        entry = removeAll(JUNK_PATTERN, entry);

        entry = removeAll(VOICE_INDICATORS_PATTERN, entry);
        entry = removeAll(ALL_CAPS_PATTERN, entry);
        return entry;
    }

    private static String removeAll(Pattern patternToRemove, String value) {
        var matcher = patternToRemove.matcher(value);
        return matcher.replaceAll("");
    }
}

