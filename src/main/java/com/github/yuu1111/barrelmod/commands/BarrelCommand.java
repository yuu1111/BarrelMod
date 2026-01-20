package com.github.yuu1111.barrelmod.commands;

import com.github.yuu1111.barrelmod.BarrelModPlugin;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class BarrelCommand extends AbstractCommand {

    private final BarrelModPlugin plugin;

    public BarrelCommand(BarrelModPlugin plugin) {
        super("barrel", "Barrel mod commands");
        this.plugin = plugin;

        addSubCommand(new GiveSubCommand(plugin));
        addSubCommand(new InfoSubCommand(plugin));
        addSubCommand(new SaveSubCommand(plugin));
    }

    @Override
    protected CompletableFuture<Void> execute(CommandContext context) {
        sendHelp(context);
        return CompletableFuture.completedFuture(null);
    }

    private void sendHelp(CommandContext context) {
        context.sendMessage(Message.raw("=== Barrel Mod Commands ==="));
        context.sendMessage(Message.raw("/barrel give <player> [amount] - Give barrels to a player"));
        context.sendMessage(Message.raw("/barrel info - Show mod information"));
        context.sendMessage(Message.raw("/barrel save - Save barrel data"));
    }

    private static class GiveSubCommand extends AbstractCommand {
        private static final String BARREL_ITEM_ID = "BarrelMod_Barrel";
        private final BarrelModPlugin plugin;
        private final RequiredArg<PlayerRef> playerArg;
        private final DefaultArg<Integer> amountArg;

        public GiveSubCommand(BarrelModPlugin plugin) {
            super("give", "Give barrels to a player");
            this.plugin = plugin;
            this.playerArg = withRequiredArg("player", "Target player", ArgTypes.PLAYER_REF);
            this.amountArg = withDefaultArg("amount", "Number of barrels", ArgTypes.INTEGER, 1, "1");
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            PlayerRef playerRef = context.get(playerArg);
            int amount = context.get(amountArg);
            amount = Math.max(1, Math.min(amount, 64));

            if (playerRef == null) {
                context.sendMessage(Message.raw("Player not found"));
                return CompletableFuture.completedFuture(null);
            }

            context.sendMessage(Message.raw("Use: /give " + BARREL_ITEM_ID + " " + amount));
            context.sendMessage(Message.raw("to give " + amount + " barrel(s) to " + playerRef.getUsername()));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class InfoSubCommand extends AbstractCommand {
        private final BarrelModPlugin plugin;

        public InfoSubCommand(BarrelModPlugin plugin) {
            super("info", "Show mod information");
            this.plugin = plugin;
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            int barrelCount = plugin.getBarrelRegistry().getBarrelCount();
            context.sendMessage(Message.raw("=== Barrel Mod Info ==="));
            context.sendMessage(Message.raw("Version: 1.0.1"));
            context.sendMessage(Message.raw("Total barrels: " + barrelCount));
            context.sendMessage(Message.raw("Max capacity per barrel: 2048 items"));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class SaveSubCommand extends AbstractCommand {
        private final BarrelModPlugin plugin;

        public SaveSubCommand(BarrelModPlugin plugin) {
            super("save", "Save barrel data");
            this.plugin = plugin;
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            plugin.getDataManager().saveAll(plugin.getBarrelRegistry());
            context.sendMessage(Message.raw("Barrel data saved successfully."));
            return CompletableFuture.completedFuture(null);
        }
    }
}
