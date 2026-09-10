package com.thunderbear06.computer;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.peripherals.DummyPocket;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.core.TerminalSize;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AndroidComputerContainer {
    private final BaseAndroidEntity android;

    public Component label = Component.translatable("entity.cc_androids.android");
    public boolean isOn = false;
    public boolean fresh = false;

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    private boolean startOn = false;
    public ComputerFamily family;

    private UpgradeData<IPocketUpgrade> leftUpgrade;
    private UpgradeData<IPocketUpgrade> rightUpgrade;
    private final DummyPocket dummyPocket;

    public AndroidComputerContainer(BaseAndroidEntity android) {
        this.android = android;
        dummyPocket = new DummyPocket(android);
    }

    public void onTick() {
        if (computerID < 0 && !startOn)
            return;

        EntityComputer computer = getOrCreateServerComputer();

        if (startOn) {
            turnOn(computer);
            startOn = false;
        }

        fresh = false;
        computerID = computer.getID();

        updateOwnerLabel(computer);

        tickPeripherals();

        computer.keepAlive();

        if (!isOn && android.isOn) {
            android.shutdown();
        }
    }

    public void turnOn(ServerComputer computer) {
        computer.turnOn();

        computerID = computer.getID();
        android.isOn = true;
        isOn = true;

        onHandItemChanged(InteractionHand.MAIN_HAND);
        onHandItemChanged(InteractionHand.OFF_HAND);

        getUpgradePeripherals();
    }

    public void openComputer(ServerPlayer player) {
        ServerComputer computer = getOrCreateServerComputer();

        if (!isOn)
            turnOn(computer);

        PlatformHelper.get().openMenu(
                player,
                label,
                (syncId, playerInventory, player1) -> AndroidMenu.ofBrain(syncId, playerInventory, getBrain()),
                new ComputerContainerData(computer, ItemStack.EMPTY)
        );
    }

    protected void updateOwnerLabel(ServerComputer computer) {
        String computerLabel = computer.getLabel();

        if (!label.toString().equals(computerLabel)) {

            if (computerLabel == null || computerLabel.isEmpty())
                label = Component.translatable("entity.cc_androids.android");
            else
                label = Component.literal(computerLabel);

            android.setCustomName(label);
        }
    }

    public final EntityComputer getOrCreateServerComputer() {
        MinecraftServer server = android.level().getServer();
        if (server == null) {
            throw new IllegalStateException("Cannot access server computer on the client.");
        }

        EntityComputer computer = (EntityComputer) ServerContext.get(server).registry().get(instanceID);
        if (computer == null) {
            if (computerID < 0) {
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer");
            }

            computer = createComputer(computerID);
            instanceID = computer.register();
            fresh = true;
        }

        return computer;
    }

    public void setPeripheral(ComputerSide side, IPeripheral peripheral) {
        ServerComputer computer = getServerComputer();

        if (computer == null) {
            CCAndroids.LOGGER.error("Failed to set peripheral of type {} on side {} of computer container owned by {}. Reason: Failed to get ServerComputer (It was null)", peripheral.getType(), side.getName(), android.getName());
            return;
        }

        computer.setPeripheral(side, peripheral);
    }

    public boolean isOn() {
        EntityComputer computer = getServerComputer();
        return computer != null && computer.isOn();
    }

    public ComputerFamily getFamily() {
        return family;
    }

    public int getComputerID() {
        return computerID;
    }

    public void setFamily(ComputerFamily family) {
        this.family = family;
    }

    public void setComputerID (int id) {
        computerID = id;
    }

    public AndroidBrain getBrain() {
        return android.brain;
    }

    protected EntityComputer createComputer(int id) {
        ServerComputer.Properties properties = ServerComputer.properties(id, getFamily())
                .addComponent(ComputerComponents.ANDROID_COMPUTER, android.brain)
                .label(label.toString())
                .terminalSize(new TerminalSize(Config.TURTLE_TERM_WIDTH, Config.TURTLE_TERM_HEIGHT));

        return new EntityComputer((ServerLevel) android.level(), android, properties);
    }

    @Nullable
    public EntityComputer getServerComputer() {
        return !android.level().isClientSide && android.level().getServer() != null ? (EntityComputer) ServerContext.get(android.level().getServer()).registry().get(instanceID) : null;
    }

    public void writeNbt(CompoundTag computerCompound) {
        computerCompound.putBoolean("StartOn", startOn);
        computerCompound.putInt("ComputerID", getComputerID());
    }

    public void readNbt(CompoundTag computerCompound) {
        startOn = computerCompound.getBoolean("StartOn");
        if (computerCompound.contains("ComputerID"))
            setComputerID(computerCompound.getInt("ComputerID"));
    }

    public void getUpgradePeripherals() {
        setPeripheral(ComputerSide.LEFT, leftUpgrade == null ? null : leftUpgrade.upgrade().createPeripheral(dummyPocket));
        setPeripheral(ComputerSide.RIGHT, rightUpgrade == null ? null : rightUpgrade.upgrade().createPeripheral(dummyPocket));
    }

    private void tickPeripherals() {
        ServerComputer computer = getServerComputer();

        assert computer != null;

        if (leftUpgrade != null) leftUpgrade.upgrade().update(dummyPocket, computer.getPeripheral(ComputerSide.LEFT));
        if (rightUpgrade != null) rightUpgrade.upgrade().update(dummyPocket, computer.getPeripheral(ComputerSide.RIGHT));
    }

    public boolean hasUpgrade(ComputerSide side) {
        return (side == ComputerSide.LEFT && leftUpgrade != null) || (side == ComputerSide.RIGHT && rightUpgrade != null);
    }

    public void onHandItemChanged(InteractionHand hand) {
        ItemStack handItem = android.getStackInHand(hand);

        if (hand == InteractionHand.OFF_HAND) {
            leftUpgrade = dummyPocket.createUpgrade(handItem);
            return;
        }

        rightUpgrade = dummyPocket.createUpgrade(handItem);
    }
}
