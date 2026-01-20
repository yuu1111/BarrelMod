package com.github.yuu1111.barrelmod.storage;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.Objects;
import java.util.UUID;

public class BarrelData {

    public static final int DEFAULT_MAX_CAPACITY = 2048;

    private final UUID id;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final String worldId;

    private String storedItemId;
    private int storedAmount;
    private int maxCapacity;
    private boolean locked;
    private UUID ownerUuid;

    public BarrelData(Vector3i position, String worldId) {
        this.id = UUID.randomUUID();
        this.posX = position.x;
        this.posY = position.y;
        this.posZ = position.z;
        this.worldId = worldId;
        this.storedItemId = null;
        this.storedAmount = 0;
        this.maxCapacity = DEFAULT_MAX_CAPACITY;
        this.locked = false;
        this.ownerUuid = null;
    }

    public BarrelData(UUID id, int posX, int posY, int posZ, String worldId, String storedItemId,
                      int storedAmount, int maxCapacity, boolean locked, UUID ownerUuid) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.worldId = worldId;
        this.storedItemId = storedItemId;
        this.storedAmount = storedAmount;
        this.maxCapacity = maxCapacity;
        this.locked = locked;
        this.ownerUuid = ownerUuid;
    }

    public UUID getId() {
        return id;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getStoredItemId() {
        return storedItemId;
    }

    public int getStoredAmount() {
        return storedAmount;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isLocked() {
        return locked;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isEmpty() {
        return storedItemId == null || storedAmount <= 0;
    }

    public boolean isFull() {
        return storedAmount >= maxCapacity;
    }

    public int getAvailableSpace() {
        return maxCapacity - storedAmount;
    }

    public boolean canAcceptItem(String itemId) {
        return isEmpty() || Objects.equals(storedItemId, itemId);
    }

    public int deposit(String itemId, int amount) {
        if (amount <= 0) {
            return 0;
        }

        if (!canAcceptItem(itemId)) {
            return 0;
        }

        if (isEmpty()) {
            storedItemId = itemId;
        }

        int canDeposit = Math.min(amount, getAvailableSpace());
        storedAmount += canDeposit;

        return canDeposit;
    }

    public int withdraw(int requestedAmount) {
        if (isEmpty() || requestedAmount <= 0) {
            return 0;
        }

        int canWithdraw = Math.min(requestedAmount, storedAmount);
        storedAmount -= canWithdraw;

        if (storedAmount <= 0) {
            storedItemId = null;
            storedAmount = 0;
        }

        return canWithdraw;
    }

    public void clear() {
        storedItemId = null;
        storedAmount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarrelData that = (BarrelData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BarrelData{" +
                "id=" + id +
                ", pos=(" + posX + "," + posY + "," + posZ + ")" +
                ", worldId='" + worldId + '\'' +
                ", storedItemId='" + storedItemId + '\'' +
                ", storedAmount=" + storedAmount +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
