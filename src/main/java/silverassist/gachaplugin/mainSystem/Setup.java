package silverassist.gachaplugin.mainSystem;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Setup {
    private final HashMap<String,Map<ItemStack,Integer>> GACHA_DATA= new HashMap<>();
    private final HashMap<String,Spin> SPIN_DATA = new HashMap<>();

    private JavaPlugin plugin;

    public Setup(JavaPlugin plugin){
        this.plugin = plugin;

        GACHA_DATA.put("test", Map.of(
                Util.createItem(Material.GRASS_BLOCK,"§a§l草"),1,
                Util.createItem(Material.DIAMOND_BLOCK,"§b§lダイヤB"),1,
                new ItemStack(Material.GOLD_BLOCK),1,
                new ItemStack(Material.EMERALD_BLOCK),1
        ));

        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    public Spin getGacha(String id){
        if(SPIN_DATA.containsKey(id))SPIN_DATA.get(id);
        return (reloadGacha(id) ? SPIN_DATA.get(id) :null);  //reloadに失敗した場合(ファイルが存在しない場合)
    }

    public boolean reloadGacha(String id){
        TreeMap<Integer,ItemStack> gachaData = new TreeMap<>();
        AtomicInteger now = new AtomicInteger();
        try {
            getData(id).forEach((item, weight) -> {
                now.addAndGet(weight);
                gachaData.put(now.get(), item);
            });
        }catch (NullPointerException e){
            System.err.println(id+"のガチャをreloadしようとしましたが失敗しました");
            return false;
        }
        SPIN_DATA.put(id,new Spin(id,gachaData));
        return true;
    }

    public Map<ItemStack,Integer> getData(String id){
        if(GACHA_DATA.containsKey(id))return GACHA_DATA.get(id);
        if(!CustomConfig.existYml(id))return null;
        YamlConfiguration data = CustomConfig.getYmlByID(id);

        Map<ItemStack,Integer> gachaData = new HashMap<>();
        for(int i = 0;i<54;i++){
            if(data.get(String.valueOf(i))==null)break;
            gachaData.put(data.getItemStack(i+".item"),data.getInt(i+".weight"));
        }
        if(gachaData.size()==0)return null;
        GACHA_DATA.put(id,gachaData);
        return gachaData;
    }

    private class listener implements Listener {
        @EventHandler
        public void inventoryClickEvent(InventoryClickEvent e){
            if(!(e.getWhoClicked() instanceof Player))return;
            Player p = (Player) e.getWhoClicked();
            if(!Spin.isOpen(p))return;
            e.setCancelled(true);
            sendPrefixMessage(p,"§cガチャをまわしている間はインベントリを触ることはできません");
        }

        @EventHandler void inventoryCloseEvent(InventoryCloseEvent e){
            if(!(e.getPlayer() instanceof Player))return;
            Player p = (Player) e.getPlayer();
            if(Spin.isOpen(p))Spin.setClose(p);

        }
    }
}
