package silverassist.gachaplugin.mainSystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.Util;

import java.util.*;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Spin {

    private static final int[] CT = new int[]{50,50,50,50,50,50,50,50,120,225,600};
    private static final int LOOP_NUM = 100;
    private static final ItemStack BG = Util.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");
    private static final ItemStack HIT = Util.createItem(Material.YELLOW_STAINED_GLASS_PANE,"§r");
    private static Set<Player> playing = new HashSet<>();
    private static Set<Player> openingGachaGUI = new HashSet<>();

    private final String ID;
    private TreeMap<Integer,ItemStack> gachaData;
    private final int RANDOM_MAX;

    public Spin(String id,TreeMap<Integer,ItemStack> gachaData){
        this.ID = id;
        this.gachaData = gachaData;
        this.RANDOM_MAX = gachaData.lastKey();
    }


    public void run(Player p){
        playing.add(p);
        openingGachaGUI.add(p);
        Inventory inv = Bukkit.createInventory(p,27,"gacha-"+ID);
        for(int i =0;i<9;i++){
            inv.setItem(i,BG);
            inv.setItem(i+18,BG);
        }
        inv.setItem(4,HIT);
        inv.setItem(22,HIT);
        p.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(GachaPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                //予めあたりは決めておく
                int bingoNum = (int) (Math.random()*RANDOM_MAX);
                ItemStack bingoItem=null;
                for(int j : gachaData.keySet()){
                    if(bingoNum>=j)continue;
                    bingoItem = gachaData.get(j);
                    break;
                }

                //ぐるぐる回す
                for(int i = 0;i<LOOP_NUM;i++) {
                    p.playSound(p.getLocation(),"block.anvil.break",1,1);
                    InventoryView inv_c = p.getOpenInventory();
                    for (int j = 0; j < 8; j++) inv_c.setItem(9 + j, inv_c.getItem(10 + j));

                    if(i==LOOP_NUM-5)inv_c.setItem(17,bingoItem);  //最後に留まる位置の調整
                    else if(isOpen(p)){
                        int next = (int) (Math.random() * RANDOM_MAX);
                        for (int j : gachaData.keySet()) {
                            if (next >= j) continue;
                            inv_c.setItem(17, gachaData.get(j));
                            break;
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
                p.playSound(p.getLocation(),"block.amethyst_block.break",1,1);
                p.getInventory().addItem(bingoItem);

                String name = bingoItem.getItemMeta().getDisplayName();
                sendPrefixMessage(p,"§a§lおめでとうございます！§6§l『§d§l"+(name.equals("") ? bingoItem.getType() : name)+"§6§l』§a§lが当たりました！！");

                playing.remove(p);
            }
        });
    }

    public static boolean isPlay(Player p){return playing.contains(p);}

    public static boolean isOpen(Player p){return openingGachaGUI.contains(p);}
    public static void setClose(Player p){openingGachaGUI.remove(p);}


}
