package com.thunderbear06.inventory;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class HandContainer implements Container {
    private final BaseAndroidEntity android;

    public HandContainer(BaseAndroidEntity android) {
        this.android = android;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return android.getMainHandStack().isEmpty() && android.getOffHandStack().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> android.getMainHandStack();
            case 1 -> android.getOffHandStack();
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        };
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0)
            return ItemStack.EMPTY;
        if (amount >= getItem(slot).getCount())
            return removeItemNoUpdate(slot);

        ItemStack result = getItem(slot).copyWithCount(amount);
        getItem(slot).shrink(amount);

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removedStack;

        switch (slot) {
            case 0 -> {
                removedStack = android.getMainHandStack();
                android.setStackInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            case 1 -> {
                removedStack = android.getOffHandStack();
                android.setStackInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        }

        return removedStack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        switch (slot) {
            case 0 -> android.setStackInHand(InteractionHand.MAIN_HAND, stack);
            case 1 -> android.setStackInHand(InteractionHand.OFF_HAND, stack);
            default -> throw new IllegalArgumentException("Invalid slot index: " + slot);
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        android.setStackInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        android.setStackInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }
}
