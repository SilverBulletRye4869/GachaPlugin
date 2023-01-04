package silverassist.gachaplugin;



import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.mainSystem.Setup;


public final class GachaPlugin extends JavaPlugin {

    private static JavaPlugin plugin;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.saveDefaultConfig();
        new Command(this, new Setup(this));
    }

    public static JavaPlugin getInstance() {return plugin;}



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
