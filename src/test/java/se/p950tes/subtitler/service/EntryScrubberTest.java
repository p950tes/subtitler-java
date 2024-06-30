package se.p950tes.subtitler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.p950tes.subtitler.model.SubtitleEntry;

class EntryScrubberTest {

    private final EntryScrubber scrubber = new EntryScrubber();

    @Test
    void html_single_line_removed() {
        assertEquals("Hello", scrub("<p>Hello</p><br/>").get(0));
    }
    @Test
    void html_single_line_malformed() {
        assertEquals("<pHello", scrub("<pHello</p><br/>").get(0));
    }
    @Test
    void square_bracket_single_line_removed() {
        assertEquals("Hello", scrub("[HELP]Hello[help]").get(0));
    }
    @Test
    void square_bracket_line_malformed() {
        assertEquals("[HELPHello[HELP", scrub("[HELPHello[HELP").get(0));
    }
    @Test
    void round_bracket_single_line_removed() {
        assertEquals("Hello", scrub("(HELP)Hello(help)").get(0));
    }
    @Test
    void round_bracket_line_malformed() {
        assertEquals("(HELPHello(HELP", scrub("(HELPHello(HELP").get(0));
    }
    
    @Test
    void empty_spaces_removed() {
        assertEquals("Hello", scrub(" (HELP) Hello (help) ").get(0));
    }

    private List<String> scrub(String... lines) {
        SubtitleEntry entry = new SubtitleEntry(1, "00:00:16,141 --> 00:00:18,727", List.of(lines));
        scrubber.scrub(entry);
        return entry.getLines();
    }
}
