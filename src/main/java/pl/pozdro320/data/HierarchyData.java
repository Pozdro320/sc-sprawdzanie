package pl.pozdro320.data;

import java.util.Map;

public class HierarchyData {
    private final Map<String, Integer> groupPriorities;

    public HierarchyData(Map<String, Integer> groupPriorities) {
        this.groupPriorities = groupPriorities;
    }

    public boolean canCheck(String moderatorGroup, String targetGroup) {
        int modPower = groupPriorities.getOrDefault(moderatorGroup.toLowerCase(), 0);
        int targetPower = groupPriorities.getOrDefault(targetGroup.toLowerCase(), 0);
        
        return modPower > targetPower;
    }
}