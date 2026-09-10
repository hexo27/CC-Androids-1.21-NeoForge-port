package com.thunderbear06.computer;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.server.level.ServerLevel;

public class EntityComputer extends ServerComputer {
    private final BaseAndroidEntity entity;

    public EntityComputer(ServerLevel level, BaseAndroidEntity entity, Properties properties) {
        super(level, entity.blockPosition(), properties);
        this.entity = entity;
    }

    @Override
    protected void tickServer() {
        super.tickServer();
        setPosition((ServerLevel) entity.level(), entity.blockPosition());
    }
}
