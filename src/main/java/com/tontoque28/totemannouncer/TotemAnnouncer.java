package com.tontoque28.totemannouncer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

public class TotemAnnouncer implements ModInitializer {

    private static final Map<UUID, Integer> LAST_COUNTS = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                int currentCount = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                Integer last = LAST_COUNTS.get(player.getUUID());
                if (last == null) {
                    LAST_COUNTS.put(player.getUUID(), currentCount);
                } else if (currentCount > last) {
                    LAST_COUNTS.put(player.getUUID(), currentCount);
                    Component message = new TextComponent("\u00a1")
                            .append(player.getDisplayName())
                            .append(new TextComponent(" ha consumido un t\u00f3tem!"))
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                    server.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
                } else if (currentCount < last) {
                    LAST_COUNTS.put(player.getUUID(), currentCount);
                }
            }
        });
    }
}
