package jeiexporter.jei;

import jeiexporter.config.ConfigHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

/**
 * @author youyihj
 */
public class OreDictEntry {
    private final String name;
    private final int amount;
    private ItemStack representationItem = ItemStack.EMPTY;

    public OreDictEntry(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getRepresentationItem() {
        if (representationItem.isEmpty()) {
            NonNullList<ItemStack> items = OreDictionary.getOres(name);
            for (String owners : ConfigHandler.ownersOfOreDict) {
                for (ItemStack item : items) {
                    if (item.getItem().getRegistryName().getResourceDomain().equals(owners)) {
                        if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                            ItemStack copy = item.copy();
                            copy.setItemDamage(0);
                            return representationItem = copy;
                        }
                        return representationItem = item;
                    }
                }
            }
            return representationItem = items.get(0);
        }
        return representationItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OreDictEntry that = (OreDictEntry) o;
        return amount == that.amount && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }

    @Override
    public String toString() {
        return "OreDictEntry{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
