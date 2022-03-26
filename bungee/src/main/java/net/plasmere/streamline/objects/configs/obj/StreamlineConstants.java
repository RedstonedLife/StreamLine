package net.plasmere.streamline.objects.configs.obj;

public class StreamlineConstants {
    public String version;
    public String language;
    public boolean isFresh;
    public boolean isBeta;

    public StreamlineConstants(String version, String language, boolean isFresh, boolean isBeta) {
        this.version = version;
        this.language = language;
        this.isFresh = isFresh;
        this.isBeta = isBeta;
    }

    public StreamlineConstants setVersion(String set) {
        this.version = set;
        return this;
    }

    public StreamlineConstants setLanguage(String set) {
        this.language = set;
        return this;
    }

    public StreamlineConstants setFresh(boolean set) {
        this.isFresh = set;
        return this;
    }

    public StreamlineConstants setBeta(boolean set) {
        this.isBeta = set;
        return this;
    }
}