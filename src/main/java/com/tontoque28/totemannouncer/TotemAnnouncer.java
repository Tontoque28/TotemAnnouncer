package com.tontoque28.totemannouncer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(TotemAnnouncer.MODID)
public class TotemAnnouncer {

    public static final String MODID = "totemannouncer";

    public TotemAnnouncer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            int currentCount = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
            int lastKnownCount = player.getPersistentData().getInt("LastTotemCount");

            if (!player.getPersistentData().contains("TotemAnnouncerInit")) {
                player.getPersistentData().putInt("LastTotemCount", currentCount);
                player.getPersistentData().putBoolean("TotemAnnouncerInit", true);
                return;
            }

            if (currentCount > lastKnownCount) {
                player.getPersistentData().putInt("LastTotemCount", currentCount);
                announceTotem(player);
            } else if (currentCount < lastKnownCount) {
                player.getPersistentData().putInt("LastTotemCount", currentCount);
            }
        }
    }

    private void announceTotem(ServerPlayer player) {
        Component message = Component.literal("¡")
                .append(player.getDisplayName())
                .append(" ha consumido un tótem!")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);

        player.getServer().getPlayerList().broadcastSystemMessage(message, false);
    }
}
