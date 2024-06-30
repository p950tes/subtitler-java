package se.p950tes.subtitler.model;

import java.nio.file.Path;
import java.util.List;

public class Subtitle {

    private Path path;
    private List<SubtitleEntry> entries;

    public Subtitle(Path path, List<SubtitleEntry> entries) {
        this.path = path;
        this.entries = entries;
    }
    public Path getPath() {
        return path;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public List<SubtitleEntry> getEntries() {
        return entries;
    }
    public void setEntries(List<SubtitleEntry> entries) {
        this.entries = entries;
    }
    @Override
    public String toString() {
        return "Subtitle [\n" + 
        "  path: " + path + "\n" +
        "  entries: \n" + entries + "]";
    }
}
