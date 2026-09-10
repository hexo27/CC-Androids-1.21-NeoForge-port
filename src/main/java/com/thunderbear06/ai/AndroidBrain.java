package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AndroidModules;
import com.thunderbear06.ai.task.Task;
import com.thunderbear06.ai.task.TaskManager;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class AndroidBrain
{
    protected final AndroidEntity android;
    protected final TaskManager taskManager;
    protected final AndroidModules modules;

    @Deprecated
    public AndroidPlayer fakePlayer;
    private GameProfile owningPlayerProfile;

    public AndroidBrain(AndroidEntity entity)
    {
        android = entity;
        taskManager = new TaskManager();
        modules = new AndroidModules(entity, this);

        if (android.level() instanceof ServerLevel)
        {
            this.fakePlayer = AndroidPlayer.get(this);
        }
        else
        {
            this.fakePlayer = null;
        }
    }

    public void onShutdown()
    {
        taskManager.clearCurrentTask();
    }

    public void setTask(Task task)
    {
        if (CCAndroids.CONFIG.DebugLogging)
            CCAndroids.LOGGER.info("Set current android task to {}", task.getName());

        taskManager.setCurrentTask(task);
    }

    public AndroidEntity getAndroid()
    {
        return this.android;
    }

    public AndroidModules getModules()
    {
        return this.modules;
    }

    public TaskManager getTaskManager()
    {
        return taskManager;
    }

    public boolean isOwningPlayer(Player player)
    {
        return this.owningPlayerProfile == player.getGameProfile();
    }

    public GameProfile getOwningPlayerProfile()
    {
        return this.owningPlayerProfile;
    }

    public void setOwningPlayer(GameProfile gameProfile)
    {
        this.owningPlayerProfile = gameProfile;
    }

    public void writeNbt(CompoundTag computerCompound)
    {
        if (this.owningPlayerProfile == null)
            return;

        computerCompound.putUUID("OwningPlayerUUID", this.owningPlayerProfile.getId());
        computerCompound.putString("OwningPlayerName", this.owningPlayerProfile.getName());
    }

    public void readNbt(CompoundTag computerCompound)
    {
        if (!computerCompound.contains("OwningPlayerUUID"))
            return;

        this.owningPlayerProfile = new GameProfile(computerCompound.getUUID("OwningPlayerUUID"), computerCompound.getString("OwningPlayerName"));
    }
}
