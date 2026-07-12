package com.thunderbear06.item;

import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof CommandAndroidEntity)
            return ActionResult.FAIL;

        if (entity instanceof AndroidEntity android && !android.isLocked()) {
            android.deconstruct();
            if (!user.getWorld().isClient())
                stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ActionResult.SUCCESS;
        }

        if (entity instanceof AndroidFrame androidFrame) {
            androidFrame.onBreak();
            if (!user.getWorld().isClient())
                stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("gui.cc_androids.tooltip.wrench"));
    }
}
