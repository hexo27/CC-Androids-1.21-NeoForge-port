package com.thunderbear06.inventory;

import com.thunderbear06.CCAndroids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import java.util.Optional;

public class AndroidInventory extends SimpleInventory {
    public AndroidInventory(int size) {
        super(size);
    }

    public NbtCompound toNbtCompound(RegistryWrapper.WrapperLookup registries) {
        NbtCompound compound = new NbtCompound();

        for (int i = 0; i < this.size(); i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty())
                continue;

            compound.put(Integer.toString(i), stack.encode(registries));
        }

        return compound;
    }

    public void fromNbtCompound(NbtCompound compound, RegistryWrapper.WrapperLookup registries) {
        for (int i = 0; i < this.size(); i++) {
            String key = Integer.toString(i);
            if (compound.contains(key)) {
                Optional<ItemStack> itemStack = ItemStack.fromNbt(registries, compound.getCompound(key));

                if (itemStack.isEmpty()) {
                    CCAndroids.LOGGER.error("Failed to deserialize android inventory item from NBT: {}", key);
                    continue;
                }

                setStack(i, itemStack.get());
            }
        }
    }
}
