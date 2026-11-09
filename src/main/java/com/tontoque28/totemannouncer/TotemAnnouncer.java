package com.tontoque28.totemannouncer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import java.util.Map;
import java.util.UUID;

public class TotemAnnouncer implements ModInitializer {
    private static final Map<UUID, Integer> lastCounts = new java.util.HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                int currentCount = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                Integer last = lastCounts.getOrDefault(player.getUUID(), null);

                if (last == null) {
                    lastCounts.put(player.getUUID(), currentCount);
                } else if (currentCount > last) {
                    lastCounts.put(player.getUUID(), currentCount);
                    Component message = Component.literal("¡").append(player.getDisplayName()).append(" ha consumido un tótem!")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                    server.getPlayerList().broadcastSystemMessage(message, false);
                } else if (currentCount < last) {
                    lastCounts.put(player.getUUID(), currentCount);
                }
            }
        });
    }
}
