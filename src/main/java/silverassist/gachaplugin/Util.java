package silverassist.gachaplugin;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Util {
    private static String PREFIX = "§b§l[§e§lGacha§b§l]";

    public static ItemStack createItem(Material m,String name){
        return createItem(m,name,null,0,null);
    }

    public static ItemStack createItem(Material m, String name, List<String> lore){
        return createItem(m,name,lore,0,null);
    }
    public static ItemStack createItem(Material m, String name, List<String> lore, HashMap<Enchantment,Integer> ench){
        return createItem(m,name,lore,0,ench);
    }
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){
        return createItem(m,name,lore,model,null);
    }

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

    public static void sendPrefixMessage(Player p, String msg){
        p.sendMessage(PREFIX+"§r"+msg);
    }
}
