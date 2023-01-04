package silverassist.gachaplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.mainSystem.Setup;
import silverassist.gachaplugin.mainSystem.Spin;
import silverassist.gachaplugin.menu.admin.MainMenu;

import java.io.IOException;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;

    private final Setup GACHA;

    public Command(JavaPlugin plugin, Setup gacha){
        this.plugin = plugin;
        this.GACHA = gacha;
        plugin.getCommand("gacha").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!sender.isOp() || !(sender instanceof Player) || args.length == 0)return true;

        Player p = (Player) sender;
        String id = null;
        if(args.length>0){
            id = args[1];
        }
        switch (args[0]){
            case "test":
                if(Spin.isPlay(p))return true;

                GACHA.getGacha("test").run(p);
                return true;
            case "start":
                if(args.length < 2){
                    sendPrefixMessage(p,"§cガチャを指定してください");
                    return true;
                }
                Player runner = args.length==3 ? Bukkit.getPlayer(args[2]) : p;
                Spin gacha = GACHA.getGacha(id);
                if(gacha!=null)gacha.run(runner);
                else sendPrefixMessage(p,"§cガチャが見つかりません");

                return true;
            case "create":
                if(!CustomConfig.existYml(id)){
                    YamlConfiguration DATA = CustomConfig.getYmlByID(id);
                    DATA.set("money",0);
                    DATA.set("item",new ItemStack(Material.AIR));
                    try {
                        DATA.save(CustomConfig.getYmlFileByID(id));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            case "edit":
                if(!CustomConfig.existYml(id)){
                    sendPrefixMessage(p,"§cガチャが存在しません");
                    return true;
                }
                new MainMenu(p,id).open();
        }
        return true;
    }
}
