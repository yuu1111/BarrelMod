package com.github.yuu1111.barrelmod.listeners;

import com.github.yuu1111.barrelmod.BarrelModPlugin;
import com.github.yuu1111.barrelmod.storage.BarrelData;
import com.github.yuu1111.barrelmod.storage.BarrelRegistry;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.protocol.InteractionType;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class BarrelBlockListener {

    private static final String BARREL_ITEM_ID = "barrelmod_barrel";

    private final BarrelModPlugin plugin;

    public BarrelBlockListener(BarrelModPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerInteractEvent.class, this::onPlayerInteract);
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        Vector3i targetBlock = event.getTargetBlock();
        if (targetBlock == null) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        UUID playerUuid = player.getPlayerRef().getUuid();
        String worldId = "default";

        BarrelRegistry registry = plugin.getBarrelRegistry();
        Optional<BarrelData> optBarrel = registry.getByPosition(targetBlock, worldId);

        if (optBarrel.isEmpty()) {
            ItemStack heldItem = event.getItemInHand();
            if (heldItem != null && !heldItem.isEmpty() && BARREL_ITEM_ID.equals(heldItem.getItemId())) {
                if (event.getActionType() == InteractionType.Primary) {
                    BarrelData barrel = registry.createBarrel(targetBlock, worldId);
                    barrel.setOwnerUuid(playerUuid);
                    player.sendMessage(Message.raw("Barrel placed!"));
                    plugin.getLogger().at(Level.INFO).log("Player %s placed a barrel at %s", player.getDisplayName(), targetBlock);
                }
            }
            return;
        }

        BarrelData barrel = optBarrel.get();

        if (barrel.isLocked() && !playerUuid.equals(barrel.getOwnerUuid())) {
            player.sendMessage(Message.raw("This barrel is locked!"));
            return;
        }

        event.setCancelled(true);

        InteractionType actionType = event.getActionType();
        if (actionType == InteractionType.Primary) {
            handleDeposit(player, barrel);
        } else if (actionType == InteractionType.Secondary) {
            handleWithdraw(player, barrel);
        }
    }

    private void handleDeposit(Player player, BarrelData barrel) {
        ItemStack heldItem = player.getInventory().getItemInHand();

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
            player.sendMessage(Message.raw("Deposited " + deposited + " items"));
        } else if (barrel.isFull()) {
            player.sendMessage(Message.raw("Barrel is full!"));
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
            ItemStack itemStack = new ItemStack(itemId, withdrawn);
            player.sendMessage(Message.raw("Withdrawn " + withdrawn + " items"));
        }
    }

    private void displayBarrelInfo(Player player, BarrelData barrel) {
        if (barrel.isEmpty()) {
            player.sendMessage(Message.raw("Barrel is empty"));
        } else {
            player.sendMessage(Message.raw(barrel.getStoredItemId() + " x " + barrel.getStoredAmount()));
        }
    }
}
