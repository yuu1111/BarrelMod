package com.barrelmod.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarrelDataManager {

    private static final Logger LOGGER = Logger.getLogger("BarrelMod");
    private static final String DATA_FILE = "barrels.json";

    private final Path dataFolder;
    private final Gson gson;

    public BarrelDataManager(Path dataFolder) {
        this.dataFolder = dataFolder;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public void loadAll(BarrelRegistry registry) {
        Path dataFile = dataFolder.resolve(DATA_FILE);

        if (!Files.exists(dataFile)) {
            LOGGER.info("No barrel data file found, starting fresh.");
            return;
        }

        try (Reader reader = Files.newBufferedReader(dataFile)) {
            Type listType = new TypeToken<List<BarrelDataJson>>() {}.getType();
            List<BarrelDataJson> dataList = gson.fromJson(reader, listType);

            if (dataList == null) {
                return;
            }

            for (BarrelDataJson json : dataList) {
                BarrelData barrel = json.toBarrelData();
                registry.register(barrel);
            }

            LOGGER.info("Loaded " + dataList.size() + " barrels from storage.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load barrel data", e);
        }
    }

    public void saveAll(BarrelRegistry registry) {
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create data folder", e);
            return;
        }

        Path dataFile = dataFolder.resolve(DATA_FILE);
        List<BarrelDataJson> dataList = new ArrayList<>();

        for (BarrelData barrel : registry.getAllBarrels()) {
            dataList.add(BarrelDataJson.fromBarrelData(barrel));
        }

        try (Writer writer = Files.newBufferedWriter(dataFile)) {
            gson.toJson(dataList, writer);
            LOGGER.info("Saved " + dataList.size() + " barrels to storage.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save barrel data", e);
        }
    }

    private static class BarrelDataJson {
        String id;
        int posX;
        int posY;
        int posZ;
        String worldId;
        String storedItemId;
        int storedAmount;
        int maxCapacity;
        boolean locked;
        String ownerUuid;

        static BarrelDataJson fromBarrelData(BarrelData barrel) {
            BarrelDataJson json = new BarrelDataJson();
            json.id = barrel.getId().toString();
            json.posX = barrel.getPosX();
            json.posY = barrel.getPosY();
            json.posZ = barrel.getPosZ();
            json.worldId = barrel.getWorldId();
            json.storedItemId = barrel.getStoredItemId();
            json.storedAmount = barrel.getStoredAmount();
            json.maxCapacity = barrel.getMaxCapacity();
            json.locked = barrel.isLocked();
            json.ownerUuid = barrel.getOwnerUuid() != null ? barrel.getOwnerUuid().toString() : null;
            return json;
        }

        BarrelData toBarrelData() {
            UUID barrelId = UUID.fromString(id);
            UUID owner = ownerUuid != null ? UUID.fromString(ownerUuid) : null;
            return new BarrelData(barrelId, posX, posY, posZ, worldId, storedItemId,
                    storedAmount, maxCapacity, locked, owner);
        }
    }
}
