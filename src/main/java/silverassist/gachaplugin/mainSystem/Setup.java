package silverassist.gachaplugin.mainSystem;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Setup {

    private final HashMap<String,Map<ItemStack,Integer>> GACHA_DATA= new HashMap<>();
    private final HashMap<String,List<Integer>> RANK_DATA = new HashMap<>();
    private final HashMap<String,Spin> SPIN_DATA = new HashMap<>();
    HashMap<String,Boolean> announce = new HashMap<>();

    public Setup(JavaPlugin plugin){
        GACHA_DATA.put("__debug__", Map.of(
                Util.createItem(Material.GRASS_BLOCK,"§a§l草"),1,
                Util.createItem(Material.DIAMOND_BLOCK,"§b§lダイヤB"),1,
                new ItemStack(Material.GOLD_BLOCK),1,
                new ItemStack(Material.EMERALD_BLOCK),1
        ));
        RANK_DATA.put("__debug__",List.of(1,4,2,3));
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        FileConfiguration config = plugin.getConfig();
        Map<String,String> map = Map.of("normal","0","rare","1","super_rare","2","ultra_rare","3","legendary","4");
        map.keySet().forEach(key ->{
            announce.put(map.get(key)+"b",config.getBoolean("message."+key+".broadcast"));
            announce.put(map.get(key)+"t",config.getBoolean("message."+key+".title"));
        });
    }


    public Spin getGacha(String id){
        if(SPIN_DATA.containsKey(id))SPIN_DATA.get(id);
        return (reloadGacha(id) ? SPIN_DATA.get(id) :null);  //reloadに失敗した場合(ファイルが存在しない場合)nullを返す
    }

    public boolean reloadGacha(String id){
        LinkedHashMap<Integer,ItemStack> spinData = new LinkedHashMap<>();
        AtomicInteger now = new AtomicInteger();
        try {
            getData(id,!id.equals("__debug__")).forEach((item, weight) -> {
                now.addAndGet(weight);
                spinData.put(now.get(), item);
            });
        }catch (NullPointerException e){
            System.err.println(id+"のガチャをreloadしようとしましたがデータが見つかりませんでした。");
            return false;
        }
        SPIN_DATA.put(id,new Spin(this,id,spinData, RANK_DATA.get(id)));
        return true;
    }

    public Map<ItemStack,Integer> getData(String id){return getData(id,false);}
    public Map<ItemStack,Integer> getData(String id,boolean toReload){
        if(!GACHA_DATA.containsKey(id) || toReload) {
            if(!reloadData(id))return null;
        };
        return GACHA_DATA.get(id);
    }

    public boolean reloadData(String id){
        if(!CustomConfig.existYml(id)){System.err.println("ガチャ『"+id+"』が存在しません");return false;}
        YamlConfiguration data = CustomConfig.getYmlByID(id);
        LinkedHashMap<ItemStack,Integer> gachaData = new LinkedHashMap<>();
        RANK_DATA.put(id,new ArrayList<>());
        for(int i = 0;i<54;i++){
            if(data.get(String.valueOf(i))==null)break;
            gachaData.put(data.getItemStack(i+".item"),data.getInt(i+".weight"));
            RANK_DATA.get(id).add(data.getInt(i+".rank"));
        }
        if(gachaData.size()==0){System.err.println("ガチャ『"+id+"』が空です");return false;};
        GACHA_DATA.put(id,gachaData);
        return true;
    }

    public Set<String> getLoadedGachaSet(){
        return GACHA_DATA.keySet();
    }

    private class listener implements Listener {
        @EventHandler
        public void inventoryCloseEvent(InventoryCloseEvent e){
            if(!(e.getPlayer() instanceof Player))return;
            Player p = (Player) e.getPlayer();
            if(Spin.isOpen(p))Spin.setClose(p);
        }
        @EventHandler
        public void inventoryClickEvent(InventoryClickEvent e){
            if(!(e.getWhoClicked() instanceof Player))return;
            Player p = (Player) e.getWhoClicked();
            if(e.getCurrentItem()==null || !Spin.isOpen(p))return;
            e.setCancelled(true);
            sendPrefixMessage(p,"§cガチャをまわしている間はインベントリを触ることはできません");
        }


    }
}
