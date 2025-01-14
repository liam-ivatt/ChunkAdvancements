package me.liamivatt.chunkAdvancements;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class ChunkAdvancements extends JavaPlugin implements Listener {

    private int targetChunkX;
    private int targetChunkZ;

    @Override
    public void onEnable() {

        // Save/Load target chunk
        saveDefaultConfig();
        loadTargetChunk();

        // Set command and eventListener
        getCommand("setchunk").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ChunkAdvancements enabled");

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Handle /setchunk, for players ONLY
        if (command.getName().equalsIgnoreCase("setchunk")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Command must be set by player");
                return true;
            }

            // Get player chunk
            Player player = (Player) sender;
            Chunk currentChunk = player.getLocation().getChunk();

            // Store current player chunk as target chunk
            targetChunkX = currentChunk.getX();
            targetChunkZ = currentChunk.getZ();

            player.sendMessage("Target chunk set to: X:" + targetChunkX + ", Z:" + targetChunkZ);
            getLogger().info("Target chunk set by " + player.getName() + ", to X:" + targetChunkX + ", Z:" + targetChunkZ);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        //Get player and current chunk
        Player player = event.getPlayer();
        Chunk currentChunk = player.getLocation().getChunk();

        //Check if player is within chunk
        if (currentChunk.getX() == targetChunkX && currentChunk.getZ() == targetChunkZ) {

            //Retrieve advancement
            NamespacedKey key = NamespacedKey.minecraft("nether/find_fortress");
            Advancement advancement = Bukkit.getAdvancement(key);
            AdvancementProgress progress = player.getAdvancementProgress(advancement);

            if (!progress.isDone()) {
                Collection<String> criteria = advancement.getCriteria();
                String criterion = criteria.iterator().next();
                progress.awardCriteria(criterion);
            }

        }
    }

    private void saveTargetChunk() {

        FileConfiguration config = getConfig();
        config.set("targetChunk.x", targetChunkX);
        config.set("targetChunk.z", targetChunkZ);
        saveConfig();

    }

    private void loadTargetChunk() {

        FileConfiguration config = getConfig();
        if (config.contains("targetChunk.x") && config.contains("targetChunk.z")) {
            targetChunkX = config.getInt("targetChunk.x");
            targetChunkZ = config.getInt("targetChunk.z");
            getLogger().info("Loaded target chunk at X:" + targetChunkX + ", Z:" + targetChunkZ);
        } else {
            getLogger().info("No target chunk set in config.");
        }
    }

}


