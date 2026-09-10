package com.thunderbear06.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class AndroidInventory extends SimpleContainer {
    public AndroidInventory(int size) {
        super(size);
    }

    public CompoundTag toNbtCompound(HolderLookup.Provider registries) {
        CompoundTag compound = new CompoundTag();

        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (stack.isEmpty())
                continue;

            compound.put(Integer.toString(i), stack.save(registries, new CompoundTag()));
        }

        return compound;
    }

    public void fromNbtCompound(HolderLookup.Provider registries, CompoundTag compound) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            String key = Integer.toString(i);
            if (compound.contains(key)) {
                final int slot = i;
                ItemStack.parse(registries, compound.getCompound(key)).ifPresent(stack -> setItem(slot, stack));
            }
        }
    }
}
