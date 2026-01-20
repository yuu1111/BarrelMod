package com.barrelmod.storage;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BarrelRegistry {

    private final Map<UUID, BarrelData> barrelsById;
    private final Map<String, BarrelData> barrelsByLocation;

    public BarrelRegistry() {
        this.barrelsById = new ConcurrentHashMap<>();
        this.barrelsByLocation = new ConcurrentHashMap<>();
    }

    public BarrelData createBarrel(Vector3i position, String worldId) {
        BarrelData barrel = new BarrelData(position, worldId);
        register(barrel);
        return barrel;
    }

    public void register(BarrelData barrel) {
        barrelsById.put(barrel.getId(), barrel);
        barrelsByLocation.put(getLocationKey(barrel.getPosX(), barrel.getPosY(), barrel.getPosZ(), barrel.getWorldId()), barrel);
    }

    public void unregister(BarrelData barrel) {
        barrelsById.remove(barrel.getId());
        barrelsByLocation.remove(getLocationKey(barrel.getPosX(), barrel.getPosY(), barrel.getPosZ(), barrel.getWorldId()));
    }

    public Optional<BarrelData> getById(UUID id) {
        return Optional.ofNullable(barrelsById.get(id));
    }

    public Optional<BarrelData> getByPosition(Vector3i position, String worldId) {
        return Optional.ofNullable(barrelsByLocation.get(getLocationKey(position.x, position.y, position.z, worldId)));
    }

    public Optional<BarrelData> getByPosition(int x, int y, int z, String worldId) {
        return Optional.ofNullable(barrelsByLocation.get(getLocationKey(x, y, z, worldId)));
    }

    public boolean existsAt(Vector3i position, String worldId) {
        return barrelsByLocation.containsKey(getLocationKey(position.x, position.y, position.z, worldId));
    }

    public Collection<BarrelData> getAllBarrels() {
        return barrelsById.values();
    }

    public int getBarrelCount() {
        return barrelsById.size();
    }

    public void clear() {
        barrelsById.clear();
        barrelsByLocation.clear();
    }

    private String getLocationKey(int x, int y, int z, String worldId) {
        return worldId + ":" + x + "," + y + "," + z;
    }
}
