package silverassist.gachaplugin;


import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.mainSystem.Setup;

public final class GachaPlugin extends JavaPlugin {

    private static JavaPlugin plugin;
    private static CustomConfig dataYml;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        dataYml = new CustomConfig(this,"data.yml");
        dataYml.saveDefaultConfig();

        new Command(this, new Setup(this));
    }

    public static JavaPlugin getInstance() {return plugin;}
    public static CustomConfig getDataYml() {return dataYml;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
