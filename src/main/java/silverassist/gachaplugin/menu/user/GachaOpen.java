package silverassist.gachaplugin.menu.user;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Vault;
import silverassist.gachaplugin.mainSystem.Setup;
import silverassist.gachaplugin.mainSystem.Spin;

import java.util.HashMap;
import java.util.List;

import static silverassist.gachaplugin.Util.*;


public class GachaOpen {
    private static final ItemStack GUI_SPIN_BUTTON = createItem(Material.LIME_STAINED_GLASS_PANE,"§a§lまわす",List.of("§c回転開始後のキャンセルは","§cできません。"));
    private static final boolean existVault = GachaPlugin.existVault();

    private final Setup GACHA_SYSTEM = GachaPlugin.getGachaSystem();
    private HashMap<Player,String> opening = new HashMap<>();


    public GachaOpen(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public boolean open(Player p,String id){
        Spin spin = GACHA_SYSTEM.getGacha(id);
        if(spin == null){
            sendPrefixMessage(p,"§cガチャが見つかりませんでした。");
            return false;
        }
        opening.put(p,id);
        Inventory inv = Bukkit.createInventory(p,27,PREFIX+" §d§l"+id);
        invFill(inv);
        int money = spin.getPaymentMoney();
        ItemStack item = spin.getPaymentItem();
        if(existVault) {
            inv.setItem(10, money > 0 ?
                    createItem(Material.GOLD_INGOT, "§6§l1回転: " + money + "円") :
                    createItem(Material.BARRIER, "§c§l1回転: 0円", List.of("§fこのガチャをまわすのに", "§f§lお金は不要です。"))
            );
        }
        inv.setItem(existVault ? 12 : 11, item.getType() != Material.AIR ? item :createItem(Material.BARRIER,"§c§l必要アイテム: なし", List.of("§fこのガチャをまわすのに","§f§lアイテムは不要です。")));
        for(int i = 14;i<=16;i++)inv.setItem(i,GUI_SPIN_BUTTON);
        p.openInventory(inv);
        return true;
    }


    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!opening.containsKey(e.getPlayer()))return;
            opening.remove(e.getPlayer());
        }
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!opening.containsKey(e.getWhoClicked()))return;
            e.setCancelled(true);
            if(e.getClickedInventory() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST) || e.getCurrentItem().getType()!=Material.LIME_STAINED_GLASS_PANE)return;
            Player p = (Player)e.getWhoClicked();
            if(GACHA_SYSTEM.isPlay(p)){
                sendPrefixMessage(p,"§cあなたは現在別のガチャをまわしています。");
                return;
            }
            Spin spin =GACHA_SYSTEM.getGacha(opening.get(p));
            int money = spin.getPaymentMoney();
            if(existVault && Vault.getEconomy().getBalance(p) < money){
                sendPrefixMessage(p,"§c§lお金が足りません");
                return;
            }
            ItemStack item = spin.getPaymentItem();
            if(item.getType() != Material.AIR && !p.getInventory().containsAtLeast(item,item.getAmount())){
                sendPrefixMessage(p,"§c§lガチャをまわすのに必要なアイテムを所持していません");
                return;
            }
            if(existVault)Vault.getEconomy().withdrawPlayer(p,money);
            p.getInventory().removeItem(item);
            GACHA_SYSTEM.getGacha(opening.get(p)).run(p);
        }
    }
}
