package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleEffect188 {
    public static void sendToPlayer(SpecializedCrates sc, ParticleEffect effect, Player player, Location location, float offsetX, float offsetY,
                                    float offsetZ, float speed,
                                    int count) throws Exception {
        if (!Utils.isPlayerInRange(sc, player, location)) {
            return;
        }

        try {

            EnumParticle particle = EnumParticle.valueOf(effect.enumValue != null ? effect.enumValue : effect.name());
            PacketPlayOutWorldParticles enumParticle =
                    new PacketPlayOutWorldParticles(particle, true, (float) location.getX(), (float) location.getY(),
                            (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, new int[10]);

            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().playerConnection.sendPacket(enumParticle);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to send Particle " + effect.name() + ". (Version 1.8.8)");
        }
    }
}
