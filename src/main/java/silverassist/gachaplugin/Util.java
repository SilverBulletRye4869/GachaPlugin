package silverassist.gachaplugin;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Util {
    public static final String PREFIX = "§b§l[§e§lGacha§b§l]";
    public static final ItemStack GUI_BG = createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");

    public static ItemStack createItem(Material m,String name){return createItem(m,name,null,0,null);}

    public static ItemStack createItem(Material m, String name, List<String> lore){return createItem(m,name,lore,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, HashMap<Enchantment,Integer> ench){return createItem(m,name,lore,0,ench);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){return createItem(m,name,lore,model,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model,HashMap<Enchantment,Integer> ench){
        ItemStack item = new ItemStack(m);
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta!=null){
            itemMeta.setDisplayName(name);
            if(lore!=null)itemMeta.setLore(lore);
            itemMeta.setCustomModelData(model);
            item.setItemMeta(itemMeta);
        }
        if(ench!=null)item.addEnchantments(ench);
        return item;
    }

    public static void invFill(Inventory inv){invFill(inv,GUI_BG,false);}
    public static void invFill(Inventory inv,ItemStack item){invFill(inv,item,false);}
    public static void invFill(Inventory inv,ItemStack item,boolean isAppend){
        int size = inv.getSize();
        for(int i = 0;i<size;i++){
            if(isAppend && inv.getItem(i).getType() != Material.AIR)continue;
            inv.setItem(i,item);
        }
    }

    public static void sendPrefixMessage(Player p, String msg){
        p.sendMessage(PREFIX+"§r"+msg);
    }


}
