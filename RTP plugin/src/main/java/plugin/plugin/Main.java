package plugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Main extends JavaPlugin implements CommandExecutor, Listener {

    private final Random random = new Random();

    @Override
    public void onEnable() {
        // Register the "/rtp" command
        getCommand("rtp").setExecutor(this);
        // Register the "/author" command
        getCommand("author").setExecutor(this);
        // Register the inventory click event listener
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rtp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                openRandomTeleportGUI(player);
            } else {
                sender.sendMessage("Only players can use this command.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("author")) {
            sender.sendMessage("This plugin was created by Cobble#0002");
            return true;
        }
        return false;
    }

    private void openRandomTeleportGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Random Teleport");

        // Create the blocks for teleportation
        ItemStack grassBlock = createTeleportBlock(Material.GRASS_BLOCK, "Earth Dimension");
        ItemStack netherrack = createTeleportBlock(Material.NETHERRACK, "Nether");
        ItemStack endStone = createTeleportBlock(Material.END_STONE, "End");

        // Set the blocks in the inventory
        inventory.setItem(10, grassBlock);
        inventory.setItem(13, netherrack);
        inventory.setItem(16, endStone);

        // Open the inventory for the player
        player.openInventory(inventory);
    }

    private ItemStack createTeleportBlock(Material material, String displayName) {
        ItemStack block = new ItemStack(material);
        ItemMeta blockMeta = block.getItemMeta();
        blockMeta.setDisplayName(displayName);
        block.setItemMeta(blockMeta);
        return block;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Random Teleport")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                Player player = (Player) event.getWhoClicked();
                String displayName = clickedItem.getItemMeta().getDisplayName();
                if (displayName != null) {
                    switch (displayName) {
                        case "Earth Dimension":
                            teleportToDimension(player, "world");
                            break;
                        case "Nether":
                            teleportToDimension(player, "world_nether");
                            break;
                        case "End":
                            teleportToDimension(player, "world_the_end");
                            break;
                    }
                    player.closeInventory();
                }
            }
        }
    }

    private void teleportToDimension(Player player, String dimension) {
        Location location = Bukkit.getWorld(dimension).getSpawnLocation();
        int x = getRandomCoordinate(location.getBlockX());
        int z = getRandomCoordinate(location.getBlockZ());
        int y = findSafeTeleportY(x, z, dimension);
        Location teleportLocation = new Location(Bukkit.getWorld(dimension), x + 0.5, y, z + 0.5);
        player.teleport(teleportLocation);
        player.sendMessage("You have been teleported to the " + dimension + " dimension!");
    }

    private int getRandomCoordinate(int currentCoordinate) {
        int range = 1000; // Adjust the range as needed
        int min = currentCoordinate - range;
        int max = currentCoordinate + range;
        return random.nextInt(max - min + 1) + min;
    }

    private int findSafeTeleportY(int x, int z, String dimension) {
        int worldHeight = Bukkit.getWorld(dimension).getMaxHeight();
        for (int y = worldHeight; y >= 0; y--) {
            Location location = new Location(Bukkit.getWorld(dimension), x, y, z);
            if (location.getBlock().isPassable()) {
                return y;
            }
        }
        return worldHeight;
    }
}
