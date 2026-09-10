package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class SensorModule extends AbstractAndroidModule {

    private final double entitySearchRadius;
    private final int blockSearchRadius;

    public SensorModule(BaseAndroidEntity android, AndroidBrain brain, double searchRadius, int blockSearchRadius) {
        super(android, brain);
        this.entitySearchRadius = searchRadius;
        this.blockSearchRadius = blockSearchRadius;
    }

    public List<HashMap<String, Object>> getMobs(@Nullable String type) {
        List<HashMap<String, Object>> result = new ArrayList<>();

        this.android.level().getEntitiesOfClass(LivingEntity.class, this.android.getBoundingBox().inflate(this.entitySearchRadius), getTypePredicate(type)).forEach(entity -> {
            try {
                result.add(collectEntityInfo(entity));
            } catch (LuaException ignored) {}
        });

        return result;
    }

    public HashMap<String, Object> getClosestMobOfType(@Nullable String type) throws LuaException {
        var box = this.android.getBoundingBox().inflate(this.entitySearchRadius);

        LivingEntity closest = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity entity : this.android.level().getEntitiesOfClass(LivingEntity.class, box, getTypePredicate(type))) {
            double dist = entity.distanceToSqr(this.android);
            if (dist < bestDist) {
                bestDist = dist;
                closest = entity;
            }
        }

        if (closest == null || closest.isDeadOrDying())
            return new HashMap<>();

        return collectEntityInfo(closest);
    }

    public HashMap<String, Object> getClosestPlayer() throws LuaException {
        var box = this.android.getBoundingBox().inflate(100);

        Player closest = null;
        double bestDist = Double.MAX_VALUE;
        for (Player player : this.android.level().getEntitiesOfClass(Player.class, box, EntitySelector.NO_SPECTATORS)) {
            double dist = player.distanceToSqr(this.android);
            if (dist < bestDist) {
                bestDist = dist;
                closest = player;
            }
        }

        if (closest == null)
            return new HashMap<>();

        return collectEntityInfo(closest);
    }

    public @Nullable ItemEntity getGroundItem(@Nullable String type) {
        List<ItemEntity> items = this.android.level().getEntitiesOfClass(ItemEntity.class, this.android.getBoundingBox().inflate(5), EntitySelector.ENTITY_STILL_ALIVE);

        for (ItemEntity entity : items) {
            if (type == null || BuiltInRegistries.ITEM.getKey(entity.getItem().getItem()).toString().contains(type))
                return entity;
        }

        return null;
    }

    public List<HashMap<String, Integer>> getBlocksOfType(BlockPos origin, Vec3 eyePos, Level world, String type) {

        List<HashMap<String, Integer>> blocks = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-this.blockSearchRadius, -this.blockSearchRadius, -this.blockSearchRadius), origin.offset(this.blockSearchRadius, this.blockSearchRadius, this.blockSearchRadius))) {
            if (!BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock()).toString().contains(type))
                continue;

            for (Direction direction : Direction.values()) {
                if (world.getBlockState(pos.relative(direction)).isSolidRender(world, pos.relative(direction)))
                    continue;

                ClipContext context = new ClipContext(eyePos, Vec3.atCenterOf(pos.relative(direction)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this.android);
                if (!world.clip(context).getBlockPos().equals(pos.relative(direction)))
                    continue;

                blocks.add(new HashMap<>() {{put("x", pos.getX()); put("y", pos.getY()); put("z", pos.getZ());}} );
                break;
            }
        }

        return blocks;
    }

    public HashMap<String, Object> collectEntityInfo(Entity entity) throws LuaException {
        HashMap<String, Object> infoMap = new HashMap<>();

        infoMap.put("uuid", entity.getStringUUID());
        infoMap.put("name", entity.getName().getString());
        infoMap.put("posX", entity.getX());
        infoMap.put("posY", entity.getY());
        infoMap.put("posZ", entity.getZ());
        if (entity instanceof LivingEntity livingEntity) {
            infoMap.put("health", livingEntity.getHealth());
        }

        return infoMap;
    }

    private Predicate<LivingEntity> getTypePredicate(@Nullable String type) {
        if (type == null) {
            return (entity -> entity != this.android && this.android.hasLineOfSight(entity));
        } else {
            return (entity -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString().contains(type)
                    && entity != this.android
                    && !entity.isSpectator()
                    && entity.isAlive()
                    && this.android.hasLineOfSight(entity));
        }
    }

    public HashMap<String, Object> GetContainerInfo(BlockPos pos) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius()))
            throw new LuaException("Position out of range");

        ServerPlayer androidPlr = AndroidPlayer.get(brain).player();

        BlockEntity blockEntity = android.level().getBlockEntity(pos);

        HashMap<String, Object> infoMap = new HashMap<>();

        if (!(blockEntity instanceof Container inv))
            return infoMap;

        infoMap.put("slotCount", inv.getContainerSize());
        infoMap.put("locked", blockEntity instanceof BaseContainerBlockEntity locked && !locked.canOpen(androidPlr));

        List<List<Object>> items = new ArrayList<>();

        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);

            List<Object> itemInfo = new ArrayList<>();

            itemInfo.add(stack.getHoverName().getString());
            itemInfo.add(stack.getCount());

            items.add((itemInfo));
        }

        infoMap.put("slots", items);

        return infoMap;
    }
}
