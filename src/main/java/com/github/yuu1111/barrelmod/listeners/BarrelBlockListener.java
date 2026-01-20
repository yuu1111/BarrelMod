package com.github.yuu1111.barrelmod.listeners;

import com.github.yuu1111.barrelmod.BarrelModPlugin;
import com.github.yuu1111.barrelmod.storage.BarrelData;
import com.github.yuu1111.barrelmod.storage.BarrelRegistry;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.protocol.InteractionType;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class BarrelBlockListener {

    private static final String BARREL_ITEM_ID = "barrelmod_barrel";

    private final BarrelModPlugin plugin;
    private final Map<UUID, PendingPlacement> pendingPlacements = new ConcurrentHashMap<>();

    public BarrelBlockListener(BarrelModPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(JavaPlugin javaPlugin) {
        javaPlugin.getEventRegistry().registerGlobal(PlayerInteractEvent.class, this::onPlayerInteract);
        javaPlugin.getEventRegistry().registerGlobal(PlaceBlockEvent.class, this::onBlockPlace);
        javaPlugin.getEventRegistry().registerGlobal(BreakBlockEvent.class, this::onBlockBreak);
        plugin.getLogger().at(Level.INFO).log("BarrelBlockListener registered");
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        Vector3i targetBlock = event.getTargetBlock();
        InteractionType actionType = event.getActionType();
        ItemStack heldItem = event.getItemInHand();

        plugin.getLogger().at(Level.INFO).log(
            "PlayerInteractEvent: player=%s, pos=%s, action=%s, item=%s",
            player.getDisplayName(),
            targetBlock,
            actionType,
            heldItem != null ? heldItem.getItemId() : "empty"
        );

        if (heldItem != null && BARREL_ITEM_ID.equals(heldItem.getItemId())) {
            if (actionType == InteractionType.Secondary && targetBlock != null) {
                UUID playerUuid = player.getUuid();
                pendingPlacements.put(playerUuid, new PendingPlacement(playerUuid, System.currentTimeMillis()));
                plugin.getLogger().at(Level.INFO).log("Pending barrel placement for player %s", player.getDisplayName());
            }
            return;
        }

        if (targetBlock == null) {
            return;
        }

        String worldId = "default";
        BarrelRegistry registry = plugin.getBarrelRegistry();
        Optional<BarrelData> optBarrel = registry.getByPosition(targetBlock, worldId);

        if (optBarrel.isEmpty()) {
            plugin.getLogger().at(Level.INFO).log("No barrel at %s", targetBlock);
            return;
        }

        BarrelData barrel = optBarrel.get();
        plugin.getLogger().at(Level.INFO).log("Barrel found at %s: stored=%s x%d",
            targetBlock, barrel.getStoredItemId(), barrel.getStoredAmount());

        UUID playerUuid = player.getUuid();
        if (barrel.isLocked() && !playerUuid.equals(barrel.getOwnerUuid())) {
            player.sendMessage(Message.raw("This barrel is locked!"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (actionType == InteractionType.Primary) {
            handleDeposit(player, barrel, heldItem);
        } else if (actionType == InteractionType.Secondary) {
            handleWithdraw(player, barrel);
        }
    }

    private void onBlockPlace(PlaceBlockEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null) {
            return;
        }

        String itemId = item.getItemId();
        plugin.getLogger().at(Level.INFO).log("PlaceBlockEvent: itemId=%s, pos=%s", itemId, event.getTargetBlock());

        if (!BARREL_ITEM_ID.equals(itemId)) {
            return;
        }

        Vector3i position = event.getTargetBlock();
        String worldId = "default";

        UUID ownerUuid = null;
        for (Map.Entry<UUID, PendingPlacement> entry : pendingPlacements.entrySet()) {
            PendingPlacement pending = entry.getValue();
            if (System.currentTimeMillis() - pending.timestamp < 5000) {
                ownerUuid = pending.playerUuid;
                pendingPlacements.remove(entry.getKey());
                break;
            }
        }

        pendingPlacements.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().timestamp > 5000);

        BarrelRegistry registry = plugin.getBarrelRegistry();
        BarrelData barrel = registry.createBarrel(position, worldId);
        if (ownerUuid != null) {
            barrel.setOwnerUuid(ownerUuid);
        }

        plugin.getLogger().at(Level.INFO).log("Barrel registered at %s, owner=%s", position, ownerUuid);
    }

    private void onBlockBreak(BreakBlockEvent event) {
        Vector3i position = event.getTargetBlock();
        String worldId = "default";

        plugin.getLogger().at(Level.INFO).log("BreakBlockEvent: pos=%s", position);

        BarrelRegistry registry = plugin.getBarrelRegistry();
        Optional<BarrelData> optBarrel = registry.getByPosition(position, worldId);

        if (optBarrel.isPresent()) {
            BarrelData barrel = optBarrel.get();
            if (!barrel.isEmpty()) {
                plugin.getLogger().at(Level.INFO).log("Barrel broken with items: %s x%d",
                    barrel.getStoredItemId(), barrel.getStoredAmount());
            }
            registry.unregister(barrel);
            plugin.getLogger().at(Level.INFO).log("Barrel unregistered at %s", position);
        }
    }

    private void handleDeposit(Player player, BarrelData barrel, ItemStack heldItem) {
        if (heldItem == null || heldItem.isEmpty()) {
            displayBarrelInfo(player, barrel);
            return;
        }

        String itemId = heldItem.getItemId();

        if (!barrel.canAcceptItem(itemId)) {
            player.sendMessage(Message.raw("This barrel only accepts: " + barrel.getStoredItemId()));
            return;
        }

        int toDeposit = heldItem.getQuantity();
        int deposited = barrel.deposit(itemId, toDeposit);

        if (deposited > 0) {
            player.sendMessage(Message.raw("Deposited " + deposited + " items. Total: " + barrel.getStoredAmount() + "/" + barrel.getMaxCapacity()));
            plugin.getLogger().at(Level.INFO).log("Player %s deposited %d %s into barrel",
                player.getDisplayName(), deposited, itemId);
        } else if (barrel.isFull()) {
            player.sendMessage(Message.raw("Barrel is full! (" + barrel.getMaxCapacity() + ")"));
        }
    }

    private void handleWithdraw(Player player, BarrelData barrel) {
        if (barrel.isEmpty()) {
            displayBarrelInfo(player, barrel);
            return;
        }

        String itemId = barrel.getStoredItemId();
        int toWithdraw = 1;
        int withdrawn = barrel.withdraw(toWithdraw);

        if (withdrawn > 0) {
            player.sendMessage(Message.raw("Withdrawn " + withdrawn + " " + itemId + ". Remaining: " + barrel.getStoredAmount()));
            plugin.getLogger().at(Level.INFO).log("Player %s withdrew %d %s from barrel",
                player.getDisplayName(), withdrawn, itemId);
        }
    }

    private void displayBarrelInfo(Player player, BarrelData barrel) {
        if (barrel.isEmpty()) {
            player.sendMessage(Message.raw("Barrel is empty (capacity: " + barrel.getMaxCapacity() + ")"));
        } else {
            player.sendMessage(Message.raw(barrel.getStoredItemId() + " x " + barrel.getStoredAmount() + "/" + barrel.getMaxCapacity()));
        }
    }

    private static class PendingPlacement {
        final UUID playerUuid;
        final long timestamp;

        PendingPlacement(UUID playerUuid, long timestamp) {
            this.playerUuid = playerUuid;
            this.timestamp = timestamp;
        }
    }
}
