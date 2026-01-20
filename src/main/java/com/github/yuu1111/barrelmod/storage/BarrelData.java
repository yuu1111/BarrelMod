package com.github.yuu1111.barrelmod.storage;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.Objects;
import java.util.UUID;

/**
 * バレルの状態を表すデータクラス。
 *
 * <p>バレルの位置、格納アイテム、容量、ロック状態などを管理する。
 * 各バレルは一意のUUIDで識別される。
 */
public class BarrelData {

    /** デフォルトの最大容量 */
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

    /**
     * 新規バレルを作成する。
     *
     * @param position ワールド座標
     * @param worldId ワールドID
     */
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

    /**
     * 永続化データから復元するコンストラクタ。
     *
     * @param id バレルID
     * @param posX X座標
     * @param posY Y座標
     * @param posZ Z座標
     * @param worldId ワールドID
     * @param storedItemId 格納アイテムID
     * @param storedAmount 格納数量
     * @param maxCapacity 最大容量
     * @param locked ロック状態
     * @param ownerUuid オーナーUUID
     */
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

    /** @return バレルの一意識別子 */
    public UUID getId() {
        return id;
    }

    /** @return X座標 */
    public int getPosX() {
        return posX;
    }

    /** @return Y座標 */
    public int getPosY() {
        return posY;
    }

    /** @return Z座標 */
    public int getPosZ() {
        return posZ;
    }

    /** @return ワールドID */
    public String getWorldId() {
        return worldId;
    }

    /** @return 格納中のアイテムID。空の場合はnull */
    public String getStoredItemId() {
        return storedItemId;
    }

    /** @return 格納中のアイテム数量 */
    public int getStoredAmount() {
        return storedAmount;
    }

    /** @return 最大容量 */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /** @return ロック状態。trueの場合オーナー以外アクセス不可 */
    public boolean isLocked() {
        return locked;
    }

    /** @return オーナーのUUID。未設定の場合はnull */
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    /** @param ownerUuid オーナーUUID */
    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    /** @param locked ロック状態 */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /** @param maxCapacity 最大容量 */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * バレルが空かどうかを判定する。
     *
     * @return 空の場合true
     */
    public boolean isEmpty() {
        return storedItemId == null || storedAmount <= 0;
    }

    /**
     * バレルが満杯かどうかを判定する。
     *
     * @return 満杯の場合true
     */
    public boolean isFull() {
        return storedAmount >= maxCapacity;
    }

    /**
     * 利用可能な空き容量を取得する。
     *
     * @return 空き容量
     */
    public int getAvailableSpace() {
        return maxCapacity - storedAmount;
    }

    /**
     * 指定アイテムを受け入れ可能か判定する。
     *
     * <p>バレルが空か、同じアイテムが格納されている場合に受け入れ可能。
     *
     * @param itemId アイテムID
     * @return 受け入れ可能な場合true
     */
    public boolean canAcceptItem(String itemId) {
        return isEmpty() || Objects.equals(storedItemId, itemId);
    }

    /**
     * アイテムをバレルに預け入れる。
     *
     * @param itemId アイテムID
     * @param amount 預け入れ数量
     * @return 実際に預け入れた数量
     */
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

    /**
     * アイテムをバレルから引き出す。
     *
     * @param requestedAmount 引き出し希望数量
     * @return 実際に引き出した数量
     */
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

    /**
     * バレルの中身をクリアする。
     */
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
