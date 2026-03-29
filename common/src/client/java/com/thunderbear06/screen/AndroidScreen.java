package com.thunderbear06.screen;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.GuiSprites;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.client.render.RenderTypes;
import dan200.computercraft.client.render.SpriteRenderer;
import dan200.computercraft.core.terminal.Terminal;
import dev.architectury.platform.Platform;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class AndroidScreen extends AbstractComputerScreen<AndroidMenu> {
    private static final Identifier BACKGROUND_NORMAL = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_normal.png");
    private static final Identifier BACKGROUND_ADVANCED = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_advanced.png");
    private static final Identifier BACKGROUND_COMMAND = new Identifier(CCAndroids.MOD_ID, "textures/gui/android_command.png");

    public AndroidScreen(AndroidMenu container, PlayerInventory player, Text title) {
        super(container, player, title, 8);
        this.backgroundWidth = 295;
        this.backgroundHeight = 217;
    }

    private static Field COMPUTER_ACTIONS;
    private static Field COMPUTER_INPUT; //fields for new 1.117.x stuff

    private static Constructor<? extends TerminalWidget> NEW_CCTERM; //1.117.x term constructor

    static {
        try {
            COMPUTER_ACTIONS =
                    AbstractComputerScreen.class.getDeclaredField("computerActions");
            COMPUTER_ACTIONS.setAccessible(true);

            COMPUTER_INPUT = AbstractComputerScreen.class.getDeclaredField("computerInput");
            COMPUTER_INPUT.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
            COMPUTER_ACTIONS = null;
            COMPUTER_INPUT = null;
        }

        try {
            Class<?> computerActionsClass = Class.forName(
                    "dan200.computercraft.client.gui.ClientComputerActions"
            );
            Class<?> computerInputClass = Class.forName(
                    "dan200.computercraft.core.input.UserComputerInput"
            );

            NEW_CCTERM = TerminalWidget.class.getConstructor(
                    Terminal.class,
                    computerInputClass,
                    computerActionsClass,
                    int.class,
                    int.class
            ); //todo: get rid of yellow annoying lines somehow
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {} //maybe dont ignore? not sure how you guys manage errors, if you should log or not.
    }

    @Nullable
    protected Object getComputerOptions() {
        if (COMPUTER_ACTIONS == null) return null;
        try {
            return COMPUTER_ACTIONS.get(this);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    protected Object getComputerInput() {
        if (COMPUTER_INPUT == null) return null;
        try {
            return COMPUTER_INPUT.get(this);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    protected TerminalWidget createTerminal() {
        //i recommend putting SOME kind of warning somewhere, like "compatible with 1.117.x but is unstable" as I have not tested this to its extent.
        if (Platform.getMod("computercraft").getVersion().contains("1.117.")) { //todo: future proof this, for versions above 1.117.x
            try {
                return NEW_CCTERM.newInstance(this.terminalData, getComputerInput(), getComputerOptions(), this.x + 8 + 17, this.y + 6);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException ignored) {} //like previous error, not show how you manage errors.
            //note: I doubt it will error but on the off chance it does, id recommend doing SOMETHING.
        }
        return new TerminalWidget(this.terminalData, this.input, this.x + 8 + 17, this.y + 6); //fallback terminal, which will crash in... 1.117.x...

    }

    protected void drawBackground(DrawContext graphics, float partialTicks, int mouseX, int mouseY) {
        Identifier texture = switch (family) {
            case NORMAL -> BACKGROUND_NORMAL;
            case ADVANCED -> BACKGROUND_ADVANCED;
            case COMMAND -> BACKGROUND_COMMAND;
        };

        graphics.drawTexture(texture, this.x + 17, this.y, 0, 0.0F, 0.0F, 278, 217, 512, 512);

        SpriteRenderer spriteRenderer = SpriteRenderer.createForGui(graphics, RenderTypes.GUI_SPRITES);
        ComputerSidebar.renderBackground(spriteRenderer, GuiSprites.getComputerTextures(this.family), this.x, this.y + this.sidebarYOffset);
        graphics.draw();
    }
}
