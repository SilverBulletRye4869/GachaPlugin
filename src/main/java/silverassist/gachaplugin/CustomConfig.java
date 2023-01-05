package silverassist.gachaplugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CustomConfig {

    private static Map<String, YamlConfiguration> config = new HashMap<>();
    private static JavaPlugin plugin = GachaPlugin.getInstance();

    public static YamlConfiguration getYmlByID(String id) {
        if(!config.containsKey(id))reloadYmlByID(id);
        return config.get(id);
    }

    public static boolean existYml(String id){
        return new File(plugin.getDataFolder(),"data/"+id+".yml").exists();
    }

    public static void reloadYmlByID(String id){
        File file = new File(plugin.getDataFolder(),"gacha_"+id+".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        config.put(id,y);
    }

    public static void saveYmlByID(String id){
        try{
            config.get(id).save(new File(plugin.getDataFolder(),"data/" + id + ".yml"));
        }catch (IOException e){
            System.err.println("ガチャ『"+id+"』の保存に失敗しました。:"+e);
        }
    }
    //public static File getYmlFileByID(String id){return new File(plugin.getDataFolder(), "gacha_" + id + ".yml");}
}