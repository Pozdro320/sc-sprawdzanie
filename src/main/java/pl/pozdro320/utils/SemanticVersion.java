package pl.pozdro320.utils;

import lombok.Getter;

@Getter
public class SemanticVersion {
    private final int major, minor, patch;

    public SemanticVersion(String version) {
        String[] parts = version.replace("v", "").split("\\.");
        this.major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        this.minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        this.patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
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
}