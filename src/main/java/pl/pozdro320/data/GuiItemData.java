package pl.pozdro320.data;

import java.util.List;
import org.bukkit.Material;

public class GuiItemData {
    private final Material material;
    private final String name;
    private final List<String> lore;

    public GuiItemData(Material material, String name, List<String> lore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
    }
    
    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }
}