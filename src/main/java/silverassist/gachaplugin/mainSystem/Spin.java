package silverassist.gachaplugin.mainSystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.util.*;

import static silverassist.gachaplugin.Util.PREFIX;
import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Spin {

    private static final int[] CT = new int[]{50,50,50,50,50,50,50,50,120,225,600};
    private static final int LOOP_NUM = 100;
    private static final ItemStack BG = Util.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");
    private static final ItemStack HIT = Util.createItem(Material.YELLOW_STAINED_GLASS_PANE,"§r");
    private static final JavaPlugin plugin = GachaPlugin.getInstance();
    private static Set<Player> playing = new HashSet<>();
    private static Set<Player> openingGachaGUI = new HashSet<>();


    private final Setup GACHA_SYSTEM;
    private final String ID;
    private final LinkedHashMap<Integer,ItemStack> spinData;
    private final int RANDOM_MAX;
    private final LinkedList<Integer> MAP_KEY_LIST;
    private final List<Integer> RARE_DATA;
    private final int PAYMENT_MONEY;
    private final ItemStack PAYMENT_ITEM;

    public Spin(Setup system,String id,LinkedHashMap<Integer,ItemStack> spinData, List rareData){
        this.GACHA_SYSTEM = system;
        this.ID = id;
        this.spinData = spinData;
        this.RANDOM_MAX = (int) spinData.keySet().toArray()[spinData.size()-1];
        this.MAP_KEY_LIST = new LinkedList<>(spinData.keySet());
        this.RARE_DATA = rareData;
        YamlConfiguration y = CustomConfig.getYmlByID(id);
        PAYMENT_MONEY = y.getInt("money");
        PAYMENT_ITEM = y.getItemStack("item");
    }


    public void run(Player p){
        p.closeInventory();
        playing.add(p);
        openingGachaGUI.add(p);
        Inventory inv = Bukkit.createInventory(p,27,"gacha-"+ID);
        for(int i =0;i<9;i++){
            inv.setItem(i,BG);
            inv.setItem(i+18,BG);
        }
        inv.setItem(4,HIT);
        inv.setItem(22,HIT);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        p.openInventory(inv);
                    }
        },1);


        Bukkit.getScheduler().runTaskAsynchronously(GachaPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                //予めあたりは決めておく
                int bingoNum = (int) (Math.random()*RANDOM_MAX);
                ItemStack bingoItem=null;
                int rare = 0;
                for(int j : spinData.keySet()){
                    if(bingoNum>=j)continue;
                    bingoItem = spinData.get(j);
                    rare = RARE_DATA.get(MAP_KEY_LIST.indexOf(j));
                    break;
                }

                //ぐるぐる回す
                for(int i = 0;i<LOOP_NUM;i++) {
                    p.playSound(p.getLocation(),"block.anvil.break",1,1);
                    if(isOpen(p)){
                        InventoryView inv_c = p.getOpenInventory();
                        for (int j = 0; j < 8; j++) inv_c.setItem(9 + j, inv_c.getItem(10 + j));
                        if(i==LOOP_NUM-5)inv_c.setItem(17,bingoItem);  //最後に留まる位置の調整
                        else {
                            int next = (int) (Math.random() * RANDOM_MAX);
                            for (int j : spinData.keySet()) {
                                if (next >= j) continue;
                                inv_c.setItem(17, spinData.get(j));
                                break;
                            }
                        }
                    }
                    try {
                        Thread.sleep(CT[(int) Math.round(i/10.0)]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int ver = Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]);
                p.playSound(p.getLocation(),ver >= 17 ?"block.amethyst_block.break":"minecraft:block.glass.break",1,1);
                p.getInventory().addItem(bingoItem);

                String name = bingoItem.getItemMeta().getDisplayName();
                String gotItem = (name.equals("") ? bingoItem.getType().toString() : name);
                sendPrefixMessage(p,"§a§lおめでとうございます！§6§l『§d§l"+gotItem+"§6§l』§a§lが当たりました！！");

                if(GACHA_SYSTEM.announce.get(rare+"b"))plugin.getServer().broadcastMessage(PREFIX+"ce§l"+p.getName()+"§a§lが『§d§l"+gotItem+"§a§l』を、§6§l"+ID+"§a§lで引き当てました！！");
                if(GACHA_SYSTEM.announce.get(rare+"t")){
                    plugin.getServer().getOnlinePlayers().forEach(player ->{
                        player.sendTitle("§d§l"+gotItem+"§a§lが当選！！","§c§l"+p.getName()+"§a§lが§6§l"+ID+"§a§lで獲得！",1,40,1);
                    });
                }

                playing.remove(p);
            }
        });
    }

    public int getPaymentMoney(){return this.PAYMENT_MONEY;}
    public ItemStack getPaymentItem(){return this.PAYMENT_ITEM;}

    public static boolean isPlay(Player p){return playing.contains(p);}

    public static boolean isOpen(Player p){return openingGachaGUI.contains(p);}
    public static void setClose(Player p){openingGachaGUI.remove(p);}


}
