package com.thunderbear06.item;

import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof CommandAndroidEntity)
            return InteractionResult.FAIL;

        if (entity instanceof AndroidEntity android && !android.isLocked()) {
            android.deconstruct();
            if (!user.level().isClientSide())
                stack.hurtAndBreak(1, user, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return InteractionResult.SUCCESS;
        }

        if (entity instanceof AndroidFrame androidFrame) {
            androidFrame.onBreak();
            if (!user.level().isClientSide())
                stack.hurtAndBreak(1, user, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("gui.cc_androids.tooltip.wrench"));
    }
}
