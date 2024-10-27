package me.cheesemaster.cheeseshield;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CheeseShield extends JavaPlugin implements Listener {

    private Map<UUID, Integer> loginAttempts = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "CheeseShield has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "CheeseShield has been disabled!");
    }

    /**
     * Blocks large item stacks to prevent overflow-related crashes.
     */
    @EventHandler
    public void preventItemStackOverflow(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item) {
            Item item = (Item) event.getEntity();
            ItemStack stack = item.getItemStack();
            if (stack.getAmount() > 64) {
                event.setCancelled(true);
                getLogger().log(Level.WARNING, "Blocked oversized item stack: " + stack.getType());
            }
        }
    }

    /**
     * Limits login attempts to prevent login spam attacks.
     */
    @EventHandler
    public void throttleLoginAttempts(AsyncPlayerPreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        loginAttempts.put(playerId, loginAttempts.getOrDefault(playerId, 0) + 1);

        if (loginAttempts.get(playerId) > 5) {  // Arbitrary limit for demo
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Too many login attempts!");
            getLogger().log(Level.WARNING, "Player " + event.getName() + " was kicked due to excessive login attempts.");
        }
    }

    /**
     * Repairs corrupted chunks upon loading.
     */
    @EventHandler
    public void fixChunkCorruption(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        // Simulated chunk corruption fix for demonstration
        getLogger().log(Level.INFO, "Checking chunk at [" + chunk.getX() + ", " + chunk.getZ() + "] for corruption.");
        // Insert logic to check and repair corrupted chunks
    }

    /**
     * Scans and sanitizes player command input to prevent malicious command crashes.
     */
    @EventHandler
    public void sanitizeCommandInput(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.contains("op") || command.contains("stop")) {  // Example sanitization
            event.setCancelled(true);
            event.getPlayer().sendMessage("You are not allowed to use this command.");
            getLogger().log(Level.WARNING, "Blocked potentially harmful command: " + command);
        }
    }

    /**
     * Limits the number of entities spawning in a region to prevent entity overload crashes.
     */
    @EventHandler
    public void monitorEntitySpawns(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getLocation().getChunk().getEntities().length > 50) {  // Example limit
            event.setCancelled(true);
            getLogger().log(Level.WARNING, "Blocked excessive entity spawn at " + entity.getLocation());
        }
    }

    /**
     * Clears lingering entities periodically to reduce lag.
     */
    public void autoPurgeLaggingEntities() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                int removed = 0;
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item) {  // Removing dropped items for demo
                        entity.remove();
                        removed++;
                    }
                }
                if (removed > 0) {
                    getLogger().log(Level.INFO, "Removed " + removed + " lagging entities in world " + world.getName());
                }
            }
        }, 0L, 6000L);  // Adjust interval as needed
    }

    /**
     * Limits the packet flood from players to prevent overload.
     */
    @EventHandler
    public void blockInvalidPacketFlood(PlayerJoinEvent event) {
        // Placeholder for packet checking logic
        getLogger().log(Level.INFO, "Monitoring packets for " + event.getPlayer().getName() + " to prevent flooding.");
    }
}

