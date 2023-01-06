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
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import java.io.IOException;
import java.util.List;

import static silverassist.gachaplugin.Util.*;

public class SetPaymentMenu {
    private static final JavaPlugin plugin = GachaPlugin.getInstance();

    private final Player p;
    private final String GACHA_ID;
    private final YamlConfiguration DATA;
    private boolean isBack = true;


    public SetPaymentMenu(Player p, String gachaID) {
        this.p = p;
        p.closeInventory();
        this.GACHA_ID = gachaID;
        DATA = CustomConfig.getYmlByID(gachaID);
        plugin.getServer().getPluginManager().registerEvents(new listener(), plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(p,27,PREFIX+"§d"+GACHA_ID+"§aの回転に必要な料金･アイテム設定");
        invFill(inv);
        inv.setItem(11,createItem(Material.GOLD_INGOT,"§6§l金額の設定", List.of("§a§l現在: §d§l"+DATA.getInt("money")+"円")));
        inv.setItem(15,DATA.getItemStack("item").getType().equals(Material.AIR)? createItem(Material.BARRIER,"§c§lなし") : DATA.getItemStack("item"));
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
            if(isBack) new MainMenu(p,GACHA_ID).open();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) throws IOException {
            if(!p.equals(e.getWhoClicked()) || e.getCurrentItem()==null)return;
            e.setCancelled(true);
            switch (e.getClickedInventory().getType()) {
                case CHEST:
                    switch (e.getSlot()) {
                        case 11:
                            isBack = false;
                            new SetNum(p, GACHA_ID ,"money").open();
                            return;
                        case 15:
                            DATA.set("item", new ItemStack(Material.AIR));
                            e.getInventory().setItem(15, createItem(Material.BARRIER, "§c§lなし"));
                            break;

                    }
                    break;
                case PLAYER:
                    if(e.getCurrentItem().getType()==Material.AIR)return;
                    DATA.set("item",e.getCurrentItem());
                    p.getOpenInventory().setItem(15,e.getCurrentItem());
            }
            CustomConfig.saveYmlByID(GACHA_ID);
            //DATA.save(CustomConfig.getYmlFileByID(GACHA_ID));
        }
    }

}
