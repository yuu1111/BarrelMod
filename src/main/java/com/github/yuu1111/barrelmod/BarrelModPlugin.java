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

/**
 * Hytale用バレルストレージMODのメインプラグインクラス。
 *
 * <p>大容量のアイテムストレージとして機能するバレルブロックを追加する。
 * プラグインのライフサイクル管理、コマンド登録、イベントリスナー登録を行う。
 */
public class BarrelModPlugin extends JavaPlugin {

    private static BarrelModPlugin instance;

    private BarrelDataManager dataManager;
    private BarrelRegistry barrelRegistry;

    /**
     * プラグインのコンストラクタ。
     *
     * @param init Hytaleから渡される初期化オブジェクト
     */
    public BarrelModPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("BarrelMod loaded!");
    }

    /**
     * プラグインのセットアップ処理。
     *
     * <p>データマネージャー、レジストリの初期化、コマンドとイベントリスナーの登録を行う。
     */
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

    /**
     * プラグインの開始処理。
     *
     * <p>永続化されたバレルデータをストレージからロードする。
     */
    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("BarrelMod is starting...");
        dataManager.loadAll(barrelRegistry);
        getLogger().at(Level.INFO).log("Loaded %d barrels from storage.", barrelRegistry.getBarrelCount());
    }

    /**
     * プラグインのシャットダウン処理。
     *
     * <p>全てのバレルデータをストレージに保存する。
     */
    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("BarrelMod is shutting down...");
        dataManager.saveAll(barrelRegistry);
        getLogger().at(Level.INFO).log("Saved %d barrels to storage.", barrelRegistry.getBarrelCount());
    }

    /**
     * プラグインのシングルトンインスタンスを取得する。
     *
     * @return プラグインインスタンス
     */
    public static BarrelModPlugin getInstance() {
        return instance;
    }

    /**
     * バレルデータマネージャーを取得する。
     *
     * @return データマネージャー
     */
    public BarrelDataManager getDataManager() {
        return dataManager;
    }

    /**
     * バレルレジストリを取得する。
     *
     * @return バレルレジストリ
     */
    public BarrelRegistry getBarrelRegistry() {
        return barrelRegistry;
    }
}
