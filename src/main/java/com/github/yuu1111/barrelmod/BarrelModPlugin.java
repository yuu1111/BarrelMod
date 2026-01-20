package com.github.yuu1111.barrelmod;

import com.github.yuu1111.barrelmod.commands.BarrelCommand;
import com.github.yuu1111.barrelmod.listeners.BarrelBlockListener;
import com.github.yuu1111.barrelmod.storage.BarrelDataManager;
import com.github.yuu1111.barrelmod.storage.BarrelRegistry;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.logging.Level;

public class BarrelModPlugin extends JavaPlugin {

    private static BarrelModPlugin instance;

    private BarrelDataManager dataManager;
    private BarrelRegistry barrelRegistry;

    public BarrelModPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("BarrelMod loaded!");
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("BarrelMod is setting up...");

        Path dataFolder = getDataDirectory();
        this.dataManager = new BarrelDataManager(dataFolder);
        this.barrelRegistry = new BarrelRegistry();

        getCommandRegistry().registerCommand(new BarrelCommand(this));

        BarrelBlockListener blockListener = new BarrelBlockListener(this);
        blockListener.register(this);

        getLogger().at(Level.INFO).log("BarrelMod setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("BarrelMod is starting...");
        dataManager.loadAll(barrelRegistry);
        getLogger().at(Level.INFO).log("Loaded %d barrels from storage.", barrelRegistry.getBarrelCount());
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("BarrelMod is shutting down...");
        dataManager.saveAll(barrelRegistry);
        getLogger().at(Level.INFO).log("Saved %d barrels to storage.", barrelRegistry.getBarrelCount());
    }

    public static BarrelModPlugin getInstance() {
        return instance;
    }

    public BarrelDataManager getDataManager() {
        return dataManager;
    }

    public BarrelRegistry getBarrelRegistry() {
        return barrelRegistry;
    }
}
