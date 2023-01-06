package silverassist.gachaplugin.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import static silverassist.gachaplugin.Util.*;

public class GachaList {
    private static final JavaPlugin plugin = GachaPlugin.getInstance();

    private final Player p;
    private final ArrayList<String> fileNames = new ArrayList<>();

    private boolean noIcon = false;
    private boolean opening = true;  //閉じた後に非同期が動かないようにするやつ
    private int nowPage = 0;

    public GachaList(Player p, Stream<Path> stream){
        this.p = p;
        stream.forEach(e->{
            String fileName = e.getFileName().toString();
            if(fileName.matches(".*\\.yml$")) fileNames.add(fileName.replaceAll("\\.yml$",""));
        });
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(int page, boolean noIcon){
        this.nowPage = page;
        this.noIcon = noIcon;
        Inventory inv = Bukkit.createInventory(p,54,PREFIX+"§d§lガチャ一覧");
        for(int i = 45;i<54;i++)inv.setItem(i,GUI_BG);
        if(page>0)inv.setItem(45,createItem(Material.RED_STAINED_GLASS_PANE,"§c前へ"));
        if(page < (fileNames.size()-1)/45)inv.setItem(53,createItem(Material.RED_STAINED_GLASS_PANE,"§a§l次へ"));
        p.openInventory(inv);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for(int i = page*45;i<Math.min((page+1)*45,fileNames.size());i++){
                    String id = fileNames.get(i);
                    if(noIcon && opening)inv.setItem(i%45,createItem(Material.PAPER,"§6§l"+id));
                    else {
                        ItemStack item = new ItemStack(CustomConfig.getYmlByID(id).getItemStack("item"));
                        if(item.getType().equals(Material.AIR)){
                            item = createItem(Material.PAPER,"§6§l"+id);
                        }else{
                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName("§6§l"+id);
                            item.setItemMeta(itemMeta);
                        }
                        if(opening)inv.setItem(i % 45, item);
                    }
                }
            }
        });
    }

    private boolean unregisterCancel = false;
    private class listener implements Listener {
        @EventHandler
        public void onInvenotryClose(InventoryCloseEvent e){
            if(!p.equals(e.getPlayer()) || unregisterCancel)return;
            opening = false;
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            if(!p.equals(e.getWhoClicked()))return;
            ItemStack item = e.getCurrentItem();
            if(item==null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            int slot = e.getSlot();
            if(slot <45){
                String id = fileNames.get(nowPage*45+slot);
                new MainMenu(p,id).open();
                return;
            }else if(slot==45 && item.getType().equals(Material.RED_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(nowPage-1,noIcon);
            }else if(slot==53 && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(nowPage+1,noIcon);
            }
        }
    }
}
