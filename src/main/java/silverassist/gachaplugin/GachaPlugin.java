package silverassist.gachaplugin;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.event.SignEvent;
import silverassist.gachaplugin.mainSystem.Setup;
import silverassist.gachaplugin.menu.user.GachaOpen;


public final class GachaPlugin extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Setup GACHA_SYSTEM;
    private static GachaOpen GACHA_OPEN;
    private static Vault vault = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.saveDefaultConfig();

        vault = new Vault(this);
        if (!vault.setupEconomy() ) {
            vault.log.severe(String.format("[%s] プラグイン「Vault」「Essentials」の認証に失敗しました。", getDescription().getName()));
        }

        GACHA_SYSTEM = new Setup(this);
        GACHA_OPEN = new GachaOpen(this);
        new Command(this);
        new SignEvent(this);

    }

    public static JavaPlugin getInstance() {return plugin;}
    public static Setup getGachaSystem(){ return GACHA_SYSTEM;}
    public static GachaOpen getGachaOpen(){return GACHA_OPEN;}
    public static Vault getVault(){return vault;}
    public static boolean existVault(){return  Vault.getEconomy()!=null;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
