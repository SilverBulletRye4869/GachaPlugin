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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.io.IOException;
import java.util.List;

import static silverassist.gachaplugin.Util.PREFIX;

public class ItemEdit {
    private static final ItemStack[] RANK_BLOCK = new ItemStack[]{
            Util.createItem(Material.IRON_BLOCK, "§7§lノーマル",List.of("§f§lクリックで変更")),
            Util.createItem(Material.COPPER_BLOCK, "§c§lレア",List.of("§f§lクリックで変更")),
            Util.createItem(Material.GOLD_BLOCK, "§e§lスーパーレア",List.of("§f§lクリックで変更")),
            Util.createItem(Material.DIAMOND_BLOCK, "§b§lウルトラレア",List.of("§f§lクリックで変更")),
            Util.createItem(Material.NETHERITE_BLOCK, "§5§lレジェンダリー",List.of("§f§lクリックで変更"))
    };

    private static final JavaPlugin plugin = GachaPlugin.getInstance();


    private final Player p;
    private final String GACHA_ID;
    private final int ITEM_ID;
    private final YamlConfiguration DATA;
    private boolean isBack = true;


    public ItemEdit(Player p, String gachaID, int itemID) {
        this.p = p;
        p.closeInventory();
        this.GACHA_ID = gachaID;
        this.ITEM_ID = itemID;
        this.DATA=CustomConfig.getYmlByID(gachaID);
        plugin.getServer().getPluginManager().registerEvents(new listener(), plugin);
    }

    public void open(){

        Inventory inv = Bukkit.createInventory(p, 27, PREFIX + "§d§lアイテムの詳細設定");
        Util.invFill(inv);
        int weight = DATA.getInt(ITEM_ID+".weight");
        int rank = DATA.getInt(ITEM_ID+ ".rank");
        inv.setItem(11, Util.createItem(Material.PAPER, "§6§l比重: " + weight, List.of("§f§lクリックで変更")));
        inv.setItem(15, RANK_BLOCK[rank]);

        //inv.setItem(xxx,Util.createItem(Material.CHEST,"§c§lガチャを回せる回数を設定"));
        inv.setItem(26, Util.createItem(Material.LAVA_BUCKET, "§c§k§laa §r§c§lこのアイテムを削除 §c§k§laa"));
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                p.openInventory(inv);
            }
        },1);
    }


    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!p.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            if(isBack)new ItemList(p,GACHA_ID).open();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) throws IOException {
            if(!p.equals(e.getWhoClicked()) || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)return;
            e.setCancelled(true);
            switch (e.getSlot()){
                case 11:
                    isBack=false;
                    new SetNum(p,GACHA_ID,ITEM_ID+".weight").open();
                    return;

                case 15:
                    int rank = DATA.getInt(ITEM_ID+".rank") + 1;
                    if(rank>= RANK_BLOCK.length)rank=0;
                    DATA.set(ITEM_ID+".rank",rank);
                    e.getClickedInventory().setItem(15,RANK_BLOCK[rank]);
                    break;

                case 26:
                    int dataSize = DATA.getKeys(false).size() - 2;
                    for(int i = ITEM_ID;i<dataSize-1;i++)DATA.set(String.valueOf(i),DATA.get(String.valueOf(i+1)));
                    DATA.set(String.valueOf(dataSize-1),null);
                    p.closeInventory();
            }

            CustomConfig.saveYmlByID(GACHA_ID);
            //DATA.save(CustomConfig.getYmlFileByID(GACHA_ID));
        }

    }
}