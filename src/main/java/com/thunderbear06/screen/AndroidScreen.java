package com.thunderbear06.screen;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.GuiSprites;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AndroidScreen extends AbstractComputerScreen<AndroidMenu> {
    private static final ResourceLocation BACKGROUND_NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_normal.png");
    private static final ResourceLocation BACKGROUND_ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_advanced.png");
    private static final ResourceLocation BACKGROUND_COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_command.png");

    public AndroidScreen(AndroidMenu container, Inventory player, Component title) {
        super(container, player, title, 8);
        this.imageWidth = 295;
        this.imageHeight = 217;
    }

    @Override
    protected TerminalWidget createTerminal() {
        return new TerminalWidget(this.terminalData, this.computerInput, this.computerActions, this.leftPos + 8 + 17, this.topPos + 6);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = switch (family) {
            case NORMAL -> BACKGROUND_NORMAL;
            case ADVANCED -> BACKGROUND_ADVANCED;
            case COMMAND -> BACKGROUND_COMMAND;
        };

        graphics.blit(texture, this.leftPos + 17, this.topPos, 0, 0.0F, 0.0F, 278, 217, 512, 512);

        var computerTextures = GuiSprites.getComputerTextures(family);
        graphics.blitSprite(
            computerTextures.sidebar(),
            this.leftPos, this.topPos + this.sidebarYOffset, AbstractComputerMenu.SIDEBAR_WIDTH, ComputerSidebar.HEIGHT
        );
        graphics.flush();
    }
}
