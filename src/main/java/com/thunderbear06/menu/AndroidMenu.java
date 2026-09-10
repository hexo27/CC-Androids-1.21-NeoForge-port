package com.thunderbear06.menu;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.inventory.HandContainer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AndroidMenu extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int PLAYER_START_X = SIDEBAR_WIDTH + BORDER;
    public static final int ANDROID_START_X = SIDEBAR_WIDTH + 175;

    public AndroidMenu(int id, Predicate<Player> canUse, ComputerFamily family, @Nullable ServerComputer computer, @Nullable ComputerContainerData containerData, Inventory playerInventory, SimpleContainer inventory, Container container) {
        super(MenuRegistry.ANDROID.get(), id, canUse, family, computer, containerData);

        // Android Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(inventory, x + y * 3, ANDROID_START_X + 1 + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player HotBar
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }

        // Android Hands
        addSlot(new Slot(container, 0, ANDROID_START_X + 1, PLAYER_START_Y + 3 * 18 + 5));
        addSlot(new Slot(container, 1, ANDROID_START_X + 1 + 18, PLAYER_START_Y + 3 * 18 + 5));
    }

    public static AndroidMenu ofBrain(int id, Inventory inventory, AndroidBrain brain) {
        return new AndroidMenu(
                id, player -> true, brain.getAndroid().getComputer().getFamily(), brain.getAndroid().getComputer().getOrCreateServerComputer(), null, inventory, brain.getAndroid().inventory, new HandContainer(brain.getAndroid())
        );
    }

    public static AndroidMenu ofData(int id, Inventory inv, ComputerContainerData data) {
        return new AndroidMenu(id, player -> true, data.family(), null, data, inv, new SimpleContainer(11), new SimpleContainer(2));
    }

    private ItemStack tryItemMerge(Player player, int slotNum, int firstSlot, int lastSlot, boolean reverse) {
        var slot = slots.get(slotNum);
        var originalStack = ItemStack.EMPTY;
        if (slot.hasItem()) {
            var clickedStack = slot.getItem();
            originalStack = clickedStack.copy();
            if (!moveItemStackTo(clickedStack, firstSlot, lastSlot, reverse)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (clickedStack.getCount() != originalStack.getCount()) {
                slot.onTake(player, clickedStack);
            } else {
                return ItemStack.EMPTY;
            }
        }
        return originalStack;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        if (slot >= 0 && slot < 9) {
            return tryItemMerge(player, slot, 16, 45, true);
        } else if (slot >= 9) {
            return tryItemMerge(player, slot, 0, 9, false);
        }
        return ItemStack.EMPTY;
    }
}
