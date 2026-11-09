package com.tontoque28.totemannouncer;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = TotemAnnouncer.MODID, name = "Totem Announcer", version = "1.0.0", acceptableRemoteVersions = "*")
public class TotemAnnouncer {

    public static final String MODID = "totemannouncer";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            int currentCount = player.getStatFile().readStat(StatList.getObjectUseStats(Items.TOTEM_OF_UNDYING));
            int lastKnownCount = player.getEntityData().getInteger("LastTotemCount");

            if (!player.getEntityData().hasKey("TotemAnnouncerInit")) {
                player.getEntityData().setInteger("LastTotemCount", currentCount);
                player.getEntityData().setBoolean("TotemAnnouncerInit", true);
                return;
            }

            if (currentCount > lastKnownCount) {
                player.getEntityData().setInteger("LastTotemCount", currentCount);
                announceTotem(player);
            } else if (currentCount < lastKnownCount) {
                player.getEntityData().setInteger("LastTotemCount", currentCount);
            }
        }
    }

    private void announceTotem(EntityPlayerMP player) {
        TextComponentString message = new TextComponentString("\u00a1");
        message.appendSibling(player.getDisplayName());
        message.appendText(" ha consumido un t\u00f3tem!");
        message.getStyle().setColor(TextFormatting.GOLD).setBold(true);
        player.getServer().getPlayerList().sendMessage(message);
    }
}
