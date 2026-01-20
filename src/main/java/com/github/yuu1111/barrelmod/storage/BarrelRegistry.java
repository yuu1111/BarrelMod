package com.github.yuu1111.barrelmod.storage;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * バレルのインメモリレジストリ
 *
 * IDおよび座標によるバレルの高速検索を提供する
 * スレッドセーフな実装で、マルチスレッド環境でも安全に使用可能
 */
public class BarrelRegistry {

    private final Map<UUID, BarrelData> barrelsById;
    private final Map<String, BarrelData> barrelsByLocation;

    /**
     * 新しいレジストリを作成する
     */
    public BarrelRegistry() {
        this.barrelsById = new ConcurrentHashMap<>();
        this.barrelsByLocation = new ConcurrentHashMap<>();
    }

    /**
     * 指定位置に新しいバレルを作成して登録する
     *
     * @param position ワールド座標
     * @param worldId ワールドID
     * @return 作成されたバレルデータ
     */
    public BarrelData createBarrel(Vector3i position, String worldId) {
        BarrelData barrel = new BarrelData(position, worldId);
        register(barrel);
        return barrel;
    }

    /**
     * バレルをレジストリに登録する
     *
     * @param barrel 登録するバレル
     */
    public void register(BarrelData barrel) {
        barrelsById.put(barrel.getId(), barrel);
        barrelsByLocation.put(getLocationKey(barrel.getPosX(), barrel.getPosY(), barrel.getPosZ(), barrel.getWorldId()), barrel);
    }

    /**
     * バレルをレジストリから登録解除する
     *
     * @param barrel 登録解除するバレル
     */
    public void unregister(BarrelData barrel) {
        barrelsById.remove(barrel.getId());
        barrelsByLocation.remove(getLocationKey(barrel.getPosX(), barrel.getPosY(), barrel.getPosZ(), barrel.getWorldId()));
    }

    /**
     * IDでバレルを検索する
     *
     * @param id バレルID
     * @return バレルデータ、見つからない場合は空のOptional
     */
    public Optional<BarrelData> getById(UUID id) {
        return Optional.ofNullable(barrelsById.get(id));
    }

    /**
     * 座標でバレルを検索する
     *
     * @param position ワールド座標
     * @param worldId ワールドID
     * @return バレルデータ、見つからない場合は空のOptional
     */
    public Optional<BarrelData> getByPosition(Vector3i position, String worldId) {
        return Optional.ofNullable(barrelsByLocation.get(getLocationKey(position.x, position.y, position.z, worldId)));
    }

    /**
     * 座標でバレルを検索する
     *
     * @param x X座標
     * @param y Y座標
     * @param z Z座標
     * @param worldId ワールドID
     * @return バレルデータ、見つからない場合は空のOptional
     */
    public Optional<BarrelData> getByPosition(int x, int y, int z, String worldId) {
        return Optional.ofNullable(barrelsByLocation.get(getLocationKey(x, y, z, worldId)));
    }

    /**
     * 指定座標にバレルが存在するか確認する
     *
     * @param position ワールド座標
     * @param worldId ワールドID
     * @return 存在する場合true
     */
    public boolean existsAt(Vector3i position, String worldId) {
        return barrelsByLocation.containsKey(getLocationKey(position.x, position.y, position.z, worldId));
    }

    /**
     * 全てのバレルを取得する
     *
     * @return バレルのコレクション
     */
    public Collection<BarrelData> getAllBarrels() {
        return barrelsById.values();
    }

    /**
     * 登録されているバレルの数を取得する
     *
     * @return バレル数
     */
    public int getBarrelCount() {
        return barrelsById.size();
    }

    /**
     * 全てのバレルをクリアする
     */
    public void clear() {
        barrelsById.clear();
        barrelsByLocation.clear();
    }

    private String getLocationKey(int x, int y, int z, String worldId) {
        return worldId + ":" + x + "," + y + "," + z;
    }
}
