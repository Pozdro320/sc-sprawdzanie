package pl.servercreators.utils;

import lombok.Getter;

@Getter
public class SemanticVersion {
    private final int major, minor, patch;

    public SemanticVersion(String version) {
        String cleanVersion = version.replaceAll("[^0-9.]", "");
        String[] parts = cleanVersion.split("\\.");
        
        this.major = parts.length > 0 ? parseSafe(parts[0]) : 0;
        this.minor = parts.length > 1 ? parseSafe(parts[1]) : 0;
        this.patch = parts.length > 2 ? parseSafe(parts[2]) : 0;
    }

    private int parseSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isNewerThan(SemanticVersion other) {
        if (this.major != other.major) return this.major > other.major;
        if (this.minor != other.minor) return this.minor > other.minor;
        return this.patch > other.patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public String getVersion() {
        return major + "." + minor + "." + patch;
    }
}