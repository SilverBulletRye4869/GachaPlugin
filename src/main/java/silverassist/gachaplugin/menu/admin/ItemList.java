package silverassist.gachaplugin.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.yaml.snakeyaml.nodes.Tag.PREFIX;

public class ItemList {
    private static final CustomConfig dataYml = GachaPlugin.getDataYml();

    private final Player p;
    private final JavaPlugin plugin = GachaPlugin.getInstance();
    private final String GACHA_ID;
    private boolean isBack = true;

    public ItemList(Player p, String gachaID){
        this.p = p;
        this.GACHA_ID= gachaID;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv;
        FileConfiguration data = dataYml.getConfig();

        inv = Bukkit.createInventory(p,54,PREFIX+"§d§l"+GACHA_ID+"§a§lの編集画面");

        for(int i = 0;i<54;i++){
            String path = GACHA_ID+"."+i+".";
            ItemStack item = data.getItemStack(path+"item");
            if(item == null)break;
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
            lore.add("§r");
            lore.add("§6§l比重: §d§l"+data.getString(path+"weight"));
            lore.add("§6§lランク: §d§l"+data.getInt(path +"rank"));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            inv.setItem(i,item);
        }
        p.openInventory(inv);

    }

    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!p.equals(e.getPlayer()) || !isBack)return;
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getWhoClicked().equals(p))return;
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)return;
            e.setCancelled(true);

            switch (e.getInventory().getType()) {
                case CHEST:
                    p.closeInventory();
                    new ItemEdit(p,GACHA_ID,e.getSlot()).open();
                    break;
                case PLAYER:
                    FileConfiguration data = dataYml.getConfig();
                    int dataSize = data.getConfigurationSection(GACHA_ID).getKeys(false).size();
                    if(dataSize>53)return;
                    ItemStack item = e.getCurrentItem();
                    data.set(GACHA_ID+"."+dataSize+".item",item);
                    data.set(GACHA_ID+"."+dataSize+".weight",1);
                    data.set(GACHA_ID+"."+dataSize+".rare",0);
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
                    lore.add("§r");
                    lore.add("§6§l比重: §d§l1");
                    lore.add("§6§lランク: §d§l0");
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    e.getClickedInventory().setItem(dataSize,item);
            }
        }
    }
}
