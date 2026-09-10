package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;

public class InteractionModule extends AbstractAndroidModule {
    public InteractionModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public void interactWithBlock(InteractionHand hand, BlockPos pos) {
        ServerPlayer player = AndroidPlayer.get(this.brain).player();

        this.android.swing(hand);

        player.gameMode.useItemOn(player, this.android.level(), player.getItemInHand(hand), hand, new BlockHitResult(pos.getCenter(), Direction.UP, pos, false));
    }

    public void interactWithEntity(InteractionHand hand, LivingEntity entity) {
        ServerPlayer player = AndroidPlayer.get(this.brain).player();

        ItemStack handStack = player.getItemInHand(hand);

        this.android.swing(hand);

        if (entity instanceof AndroidEntity droid && handStack.is(ItemRegistry.COMPONENTS.get())) {
            if (droid.repair(handStack))
                return;
        }

        if (entity instanceof Mob mob) {
            if (handStack.is(Items.LEAD) && mob.getLeashHolder() == null) {
                mob.setLeashedTo(this.android, true);
                handStack.shrink(1);
                this.android.setStackInHand(hand, handStack);
                return;
            } else if (handStack.isEmpty() && mob.getLeashHolder() != null && mob.getLeashHolder().equals(this.android)) {
                mob.dropLeash(true, true);
                return;
            }
        }

        entity.interact(player, hand);
    }

    public void tickDoorInteraction()
    {
        PathNavigation nav = android.getNavigation();

        if (!(nav instanceof GroundPathNavigation groundNav))
            return;

        if (groundNav.isDone() || groundNav.getPath() == null)
            return;
        Path path = groundNav.getPath();
        Node node = path.getNextNode();
        Node lastNode = path.getEndNode();

        toggleDoor(node.asBlockPos(), true);
        if (lastNode != null && lastNode.cameFrom != null)
            toggleDoor(lastNode.cameFrom.asBlockPos(), false);
    }

    private void toggleDoor(BlockPos pos, boolean open) {
        BlockState state = android.level().getBlockState(pos);

        if (state.is(BlockTags.WOODEN_DOORS)) {
            Block block = state.getBlock();
            if (block instanceof DoorBlock door)
                door.setOpen(android, android.level(), state, pos, open);
        }
    }

    public void StoreHeldItemInContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayer androidPlr = AndroidPlayer.get(brain).player();

        ItemStack heldStack = androidPlr.getMainHandItem();

        BlockEntity blockEntity = android.level().getBlockEntity(pos);

        if (!(blockEntity instanceof Container inv))
            throw new LuaException("Targeted block does not have an inventory");
        if (blockEntity instanceof BaseContainerBlockEntity locked && !locked.canOpen(androidPlr))
            throw new LuaException("Targeted container is locked!");
        if (!inv.canPlaceItem(slot, heldStack))
            throw new LuaException("Held item can not be placed in that slot");

        ItemStack invStack = inv.getItem(slot);

        inv.startOpen(androidPlr);

        android.swing(InteractionHand.MAIN_HAND);

        if (invStack.isEmpty())
        {
            inv.setItem(slot, heldStack.copy());

            android.setStackInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        else if (ItemStack.isSameItemSameComponents(invStack, heldStack))
        {
            int space = invStack.getMaxStackSize() - invStack.getCount();
            int transfer = Math.min(heldStack.getCount(), space);

            invStack.grow(transfer);

            heldStack.shrink(transfer);
        }
        else
        {
            throw new LuaException("There is already a item in that slot");
        }

        inv.stopOpen(androidPlr);
    }

    public void GrabItemFromContainer(BlockPos pos, int slot) throws LuaException
    {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayer androidPlr = AndroidPlayer.get(brain).player();

        ItemStack heldStack = android.getMainHandStack();

        BlockEntity blockEntity = android.level().getBlockEntity(pos);

        if (!(blockEntity instanceof Container inv))
            throw new LuaException("Targeted block does not have an inventory");
        if (blockEntity instanceof BaseContainerBlockEntity locked && !locked.canOpen(androidPlr))
            throw new LuaException("Targeted container is locked!");

        ItemStack invStack = inv.getItem(slot);

        if (invStack.isEmpty())
            throw new LuaException("Slot is empty");

        inv.startOpen(androidPlr);

        android.swing(InteractionHand.MAIN_HAND);

        if (heldStack.isEmpty())
        {
            android.setStackInHand(InteractionHand.MAIN_HAND, invStack.copy());

            inv.setItem(slot, ItemStack.EMPTY);
        }
        else if (ItemStack.isSameItemSameComponents(invStack, heldStack))
        {
            int space = heldStack.getMaxStackSize() - heldStack.getCount();
            int transfer = Math.min(invStack.getCount(), space);

            heldStack.grow(transfer);

            invStack.shrink(transfer);
        }
        else
        {
            throw new LuaException("Slot is blocked by item");
        }

        inv.stopOpen(androidPlr);
    }
}
