package com.Tontoque28.TotemAnnouncer;

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

    /**
     * ESTRATEGIA FINAL: VIGILANCIA DE ESTADÍSTICAS
     * No confiamos en eventos de muerte ni daño (porque BetterTotem los rompe).
     * Simplemente miramos la estadística oficial de "Tótems Usados".
     * Si el número sube, es que se usó uno. Fin de la historia.
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Solo ejecutamos en el servidor y al final del tick para asegurar datos frescos
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {

            // 1. Obtenemos cuántos tótems ha usado el jugador en TODA su vida
            int currentCount = player.getStats().getValue(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));

            // 2. Leemos el último número que recordamos (guardado en la memoria del jugador)
            // "LastTotemCount" es una variable que nos inventamos nosotros en su NBT data.
            int lastKnownCount = player.getPersistentData().getInt("LastTotemCount");

            // --- INICIALIZACIÓN (Primera vez que entras al mundo) ---
            // Si el jugador nunca ha sido revisado por nuestro mod, guardamos su número actual y no hacemos nada.
            // Esto evita que al instalar el mod, anuncie "has usado un totem" solo por entrar al server.
            if (!player.getPersistentData().contains("TotemAnnouncerInit")) {
                player.getPersistentData().putInt("LastTotemCount", currentCount);
                player.getPersistentData().putBoolean("TotemAnnouncerInit", true);
                return;
            }

            // --- DETECCIÓN ---
            // Si el contador actual es MAYOR que el último que vimos... ¡Usó uno!
            if (currentCount > lastKnownCount) {

                // Actualizamos la memoria
                player.getPersistentData().putInt("LastTotemCount", currentCount);

                // Imprimimos en consola para que veas que funciona
                System.out.println("[TotemAnnouncer] Estadística incrementada. Nuevo total: " + currentCount);

                // Enviamos el mensaje
                announceTotem(player);
            }
            // Caso raro: Si el contador bajó (reset de stats), actualizamos para no rompernos.
            else if (currentCount < lastKnownCount) {
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