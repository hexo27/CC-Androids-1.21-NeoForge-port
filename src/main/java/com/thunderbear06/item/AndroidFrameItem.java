package com.thunderbear06.item;

import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AndroidFrameItem extends Item {
    public AndroidFrameItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        BlockPos blockPos = new BlockPlaceContext(context).getClickedPos();
        ItemStack itemStack = context.getItemInHand();
        Vec3 vec3d = Vec3.atBottomCenterOf(blockPos);
        AABB box = EntityRegistry.ANDROID_FRAME_ENTITY.get().getDimensions().makeBoundingBox(vec3d.x, vec3d.y, vec3d.z);

        if (world.noCollision(box) && world.getEntitiesOfClass(Entity.class, box, e -> true).isEmpty()) {
            if (world instanceof ServerLevel serverWorld) {
                AndroidFrame frame = EntityRegistry.ANDROID_FRAME_ENTITY.get().spawn(serverWorld, blockPos, MobSpawnType.SPAWN_EGG);

                if (frame == null) {
                    return InteractionResult.FAIL;
                }

                float yaw = context.getPlayer() != null ? context.getPlayer().getYRot() : 0.0F;
                float f = (float) Mth.floor((Mth.wrapDegrees(yaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                frame.moveTo(frame.getX(), frame.getY(), frame.getZ(), f, 0.0F);
                serverWorld.addFreshEntityWithPassengers(frame);
                world.playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.75F, 0.8F);
                frame.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
            }

            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.FAIL;
    }
}
