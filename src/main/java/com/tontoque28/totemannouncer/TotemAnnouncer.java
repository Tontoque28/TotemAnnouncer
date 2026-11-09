package com.tontoque28.totemannouncer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
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

    private void announceTotem(ServerPlayerEntity player) {
        ITextComponent message = new StringTextComponent("\u00a1")
                .append(player.getDisplayName())
                .append(new StringTextComponent(" ha consumido un t\u00f3tem!"))
                .withStyle(TextFormatting.GOLD, TextFormatting.BOLD);

        player.getServer().getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
    }
}
