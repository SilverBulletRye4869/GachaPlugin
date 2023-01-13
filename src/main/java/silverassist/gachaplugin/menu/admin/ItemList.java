package silverassist.gachaplugin.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static silverassist.gachaplugin.Util.PREFIX;


public class ItemList {

    private static final JavaPlugin plugin = GachaPlugin.getInstance();

    private final Player p;
    private final String GACHA_ID;
    private final YamlConfiguration DATA;
    private final HashSet<ItemStack> itemSet = new HashSet<>();

    private boolean isBack = true;

    public ItemList(Player p, String gachaID){
        this.p = p;
        p.closeInventory();
        this.GACHA_ID= gachaID;
        DATA=CustomConfig.getYmlByID(gachaID);
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv;

        inv = Bukkit.createInventory(p,54,PREFIX+"§d§l"+GACHA_ID+"§a§lの編集画面");

        for(int i = 0;i<54;i++){
            ItemStack item = DATA.getItemStack(i+".item");
            if(item == null)break;
            itemSet.add(item);
            item = new ItemStack(item);
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
            lore.add("§r");
            lore.add("§6§l比重: §d§l"+DATA.getString(i+".weight"));
            lore.add("§6§lランク: §d§l"+DATA.getInt(i +".rank"));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            inv.setItem(i,item);
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                p.openInventory(inv);
            }
        },1);

    }

    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!p.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            if(isBack)new MainMenu(p,GACHA_ID).open();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getWhoClicked().equals(p))return;
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)return;
            e.setCancelled(true);

            switch (e.getClickedInventory().getType()) {
                case CHEST:
                    isBack = false;
                    new ItemEdit(p,GACHA_ID,e.getSlot()).open();
                    return;
                case PLAYER:
                    int dataSize = DATA.getKeys(false).size() - 2;
                    if(dataSize>53)return;
                    ItemStack item = e.getCurrentItem();
                    if(itemSet.contains(item)){
                        Util.sendPrefixMessage(p,"§c§lそのアイテムは既に存在します");
                        return;
                    }
                    itemSet.add(item);
                    DATA.set(dataSize+".item",item);
                    DATA.set(dataSize+".weight",1);
                    DATA.set(dataSize+".rank",0);
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
                    lore.add("§r");
                    lore.add("§6§l比重: §d§l1");
                    lore.add("§6§lランク: §d§l0");
                    itemMeta.setLore(lore);
                    item = new ItemStack(item);
                    item.setItemMeta(itemMeta);
                    p.getOpenInventory().setItem(dataSize,item);
            }
            CustomConfig.saveYmlByID(GACHA_ID);
            //DATA.save(CustomConfig.getYmlFileByID(GACHA_ID));
        }
    }
}
