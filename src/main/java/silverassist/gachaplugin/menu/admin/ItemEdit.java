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
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.util.List;

import static org.yaml.snakeyaml.nodes.Tag.PREFIX;

public class ItemEdit {
    private static final ItemStack[] RANK_BLOCK = new ItemStack[]{
            Util.createItem(Material.IRON_BLOCK, "§7§lノーマル"),
            Util.createItem(Material.COPPER_BLOCK, "§c§lレア"),
            Util.createItem(Material.GOLD_BLOCK, "§e§lスーパーレア"),
            Util.createItem(Material.DIAMOND_BLOCK, "§b§lウルトラレア"),
            Util.createItem(Material.NETHERITE_BLOCK, "§5§lレジェンダリー")
    };


    private final Player p;
    private final JavaPlugin plugin = GachaPlugin.getInstance();
    private final String GACHA_ID;
    private final int ITEM_ID;
    private boolean isBack = true;
    private CustomConfig DATA = GachaPlugin.getDataYml();

    public ItemEdit(Player p, String gachaID, int itemID) {
        this.p = p;
        this.GACHA_ID = gachaID;
        this.ITEM_ID = itemID;
        plugin.getServer().getPluginManager().registerEvents(new listener(), plugin);
    }

    public void open(){
        FileConfiguration data = DATA.getConfig();
        Inventory inv = Bukkit.createInventory(p, 27, PREFIX + "§d§lアイテムの詳細設定");
        Util.invFill(inv, Util.createItem(Material.BLUE_STAINED_GLASS_PANE, "§r"));
        int weight = data.getInt(GACHA_ID + "." + ITEM_ID + ".weight");
        int rank = data.getInt(GACHA_ID + "." + ITEM_ID + ".rank");
        inv.setItem(11, Util.createItem(Material.PAPER, "§6§l比重: " + weight, List.of("§f§lクリックで変更")));
        inv.setItem(15, RANK_BLOCK[rank]);
        inv.setItem(26, Util.createItem(Material.LAVA_BUCKET, "§c§k§laa §r§c§lこのアイテムを削除 §c§k§laa"));
        p.openInventory(inv);
    }


    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!p.equals(e.getPlayer()) || !isBack)return;
            new ItemList(p,GACHA_ID).open();
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)return;
            e.setCancelled(true);
            FileConfiguration data = DATA.getConfig();
            switch (e.getSlot()){
                case 11:
                    p.closeInventory();
                    isBack=false;
                    new SetItemWeight(p,GACHA_ID,ITEM_ID);
                    break;
                case 15:
                    int rank = data.getInt(GACHA_ID+"."+ITEM_ID+".rank") + 1;
                    if(rank>= RANK_BLOCK.length)rank=0;
                    data.set(GACHA_ID+"."+ITEM_ID+".rank",rank);
                    e.getClickedInventory().setItem(15,RANK_BLOCK[rank]);
                    break;
                case 26:
                    int dataSize = data.getConfigurationSection(GACHA_ID).getKeys(false).size();
                    for(int i = ITEM_ID;i<dataSize-1;i++){
                        data.set(GACHA_ID+"."+i,GACHA_ID+"."+(i+1));
                    }
                    data.set(GACHA_ID+"."+(dataSize-1),null);
                    p.closeInventory();
            }

        }

    }
}