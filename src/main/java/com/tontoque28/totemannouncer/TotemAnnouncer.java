package com.tontoque28.totemannouncer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TotemAnnouncer.MODID)
public class TotemAnnouncer {

    public static final String MODID = "totemannouncer";

    public TotemAnnouncer(FMLJavaModLoadingContext context) {
        TickEvent.PlayerTickEvent.Post.BUS.addListener(TotemAnnouncer::onPlayerTick);
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (event.player() instanceof ServerPlayer player) {
            int currentCount = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
            int lastKnownCount = player.getPersistentData().getIntOr("LastTotemCount", 0);

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

    private static void announceTotem(ServerPlayer player) {
        Component message = Component.literal("\u00a1")
                .append(player.getDisplayName())
                .append(" ha consumido un t\u00f3tem!")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);

        player.level().getServer().getPlayerList().broadcastSystemMessage(message, false);
    }
}
