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

import java.util.List;

import static silverassist.gachaplugin.Util.*;

public class SetNum {
    private static final List<Integer> numKeyPos = List.of(38,10,11,12,19,20,21,28,29,30);
    private static final JavaPlugin plugin = GachaPlugin.getInstance();


    private final Player p;
    private final String PATH;
    private final String GACHA_ID;
    private final YamlConfiguration DATA;
    private final int MIN_NUM;
    private int nowNum;


    public SetNum(Player p,String gachaID, String path){this(p,gachaID,path,0);}
    public SetNum(Player p,String gachaID, String path, int minNum) {
        this.p = p;
        p.closeInventory();
        this.PATH = path;
        this.GACHA_ID = gachaID;
        this.MIN_NUM = minNum;
        DATA= CustomConfig.getYmlByID(gachaID);
        nowNum = DATA.getInt(path);
        plugin.getServer().getPluginManager().registerEvents(new listener(), plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(p,54, PREFIX+"§r"+PATH+"の設定");
        invFill(inv);
        for(int i = 0;i<numKeyPos.size();i++){
            ItemStack item = createItem(Material.PAPER, "§6§l" + i);
            item.setAmount(Math.max(1,i));
            inv.setItem(numKeyPos.get(i),item);
        }
        inv.setItem(15,createItem(nowNum == 0 ? Material.PAPER : Material.MAP,"§6§l"+nowNum));
        inv.setItem(41,createItem(Material.RED_STAINED_GLASS_PANE,"§c§lリセット"));
        inv.setItem(43,createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l確定"));
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
            String[] types = PATH.split("\\.");
            if(PATH.equals("money"))new MainMenu(p,GACHA_ID).open();
            else new ItemEdit(p,GACHA_ID,Integer.parseInt(types[0])).open();
        }
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!p.equals(e.getWhoClicked()) || !e.getInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            int slot = e.getSlot();
            switch (slot){
                case 41:
                    e.getClickedInventory().setItem(15,createItem(Material.PAPER,"§6§l0"));
                    nowNum = 0;
                    return;
                case 43:
                    if(nowNum<MIN_NUM){
                        sendPrefixMessage((Player)e.getWhoClicked(),"§c§l値は最低でも、『"+MIN_NUM+"』にする必要があります");
                        return;
                    }
                    DATA.set(PATH,nowNum);
                    p.closeInventory();
                    break;
                default:
                    if(nowNum>9999999 || !numKeyPos.contains(slot))return;
                    int numKey = numKeyPos.indexOf(slot);
                    nowNum = nowNum * 10 + numKey;
                    e.getClickedInventory().setItem(15,createItem(nowNum == 0 ? Material.PAPER : Material.MAP,"§6§l"+nowNum));
                    return;
            }
            CustomConfig.saveYmlByID(GACHA_ID);
            //DATA.save(CustomConfig.getYmlFileByID(GACHA_ID));
        }
    }
}
