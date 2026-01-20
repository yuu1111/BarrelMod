# Barrel Mod for Hytale

Storage Drawer風のバレルModです。単一アイテムタイプを大量 (最大2048個) に保存できます。

## 機能

- **大容量ストレージ**: 1つのバレルに最大2048個のアイテムを保存
- **単一アイテムタイプ**: 各バレルは1種類のアイテムのみを保存
- **簡単操作**:
  - 右クリック: 手持ちアイテムを収納
  - Shift + 右クリック: インベントリ内の同種アイテムを全て収納
  - 左クリック (空の手): 1個取り出し
  - Shift + 左クリック: 1スタック取り出し
- **ロック機能**: 所有者のみがアクセス可能
- **データ永続化**: サーバー再起動後もデータを保持

## インストール

1. プロジェクトをビルド:
   ```bash
   ./gradlew build
   ```

2. `build/libs/BarrelMod-1.0.0.jar` をサーバーの `plugins/` フォルダにコピー

3. サーバーを再起動

## 必要条件

- Hytale Server (Early Access)
- Java 25

## 開発環境セットアップ

1. `libs/` フォルダに `HytaleServer.jar` を配置
   - Hytale CDN からダウンロード: `cdn.hytale.com`

2. IntelliJ IDEA でプロジェクトを開く

3. Java 25 SDK を設定

## コマンド

| コマンド | 説明 | 権限 |
|---------|------|------|
| `/barrel give <player> [amount]` | プレイヤーにバレルを付与 | barrel.admin |
| `/barrel info` | Mod情報を表示 | barrel.use |
| `/barrel reload` | データを保存 | barrel.admin |

## 権限ノード

- `barrel.use` - バレルの使用とinfoコマンド
- `barrel.admin` - 管理コマンド (give, reload)

## テクスチャ

`src/main/resources/assets/Common/` 内のREADMEを参照して、必要なテクスチャを作成してください:
- `BlockTextures/barrel_top.png`
- `BlockTextures/barrel_bottom.png`
- `BlockTextures/barrel_side.png`
- `Icons/barrel_icon.png`

## ライセンス

MIT License
