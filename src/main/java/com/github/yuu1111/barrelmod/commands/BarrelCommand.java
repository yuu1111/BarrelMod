package com.github.yuu1111.barrelmod.commands;

import com.github.yuu1111.barrelmod.BarrelModPlugin;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;
import java.util.concurrent.CompletableFuture;

public class BarrelCommand extends AbstractCommand {

    private final BarrelModPlugin plugin;

    public BarrelCommand(BarrelModPlugin plugin) {
        super("barrel", "Barrel mod commands");
        this.plugin = plugin;

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
        context.sendMessage(Message.raw("/barrel info - Show mod information"));
        context.sendMessage(Message.raw("/barrel save - Save barrel data"));
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
