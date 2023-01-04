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
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;

import java.util.List;

import static silverassist.gachaplugin.Util.*;

public class MainMenu {
    private static final JavaPlugin plugin = GachaPlugin.getInstance();

    private final Player p;
    private final String GACHA_ID;
    private boolean isBack = true;
    private final YamlConfiguration DATA;

    public MainMenu(Player p, String gachaID){
        p.closeInventory();
        this.p = p;
        this.GACHA_ID = gachaID;
        DATA=CustomConfig.getYmlByID(gachaID);
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(p,27,PREFIX+"§d"+GACHA_ID+"§aの編集画面");
        invFill(inv);
        inv.setItem(11,createItem(Material.DROPPER,"§6§l排出品の設定"));
        inv.setItem(13, createItem(Material.GOLD_INGOT,"§6§l回すのに必要なお金･アイテムの変更", List.of(
                "§a料金: "+(DATA.get("money") == null ? 100 : DATA.getInt("money")),
                "§aアイテム: "+(DATA.get("item") == null ? "§c§lなし" : "§d§lあり")
        )));
        inv.setItem(15,createItem(Material.BEDROCK,"§7§l近日実装予定"));
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
        }

        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            if(!p.equals(e.getWhoClicked()) || !e.getInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            switch (e.getSlot()){
                case 11:
                    isBack=false;
                    new ItemList(p,GACHA_ID).open();
                    return;
                case 13:
                    isBack=false;
                    new SetPaymentMenu(p,GACHA_ID).open();
                    return;
            }
        }
    }
}
