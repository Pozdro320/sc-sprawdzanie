package pl.pozdro320.data;

import java.util.List;

public class BlockCommandData {
    private final List<String> allowedCommands;

    public BlockCommandData(List<String> allowedCommands) {
        this.allowedCommands = allowedCommands;
    }

    public List<String> getAllowedCommands() {
        return allowedCommands;
    }
}