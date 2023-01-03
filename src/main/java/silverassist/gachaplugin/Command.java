package silverassist.gachaplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.mainSystem.Setup;
import silverassist.gachaplugin.mainSystem.Spin;
import silverassist.gachaplugin.menu.admin.ItemList;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CustomConfig dataYml = GachaPlugin.getDataYml();
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
        if(args.length>0)id = args[1];
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
                GACHA.getGacha(id).run(runner);
                return true;
            case "create":
                if(dataYml.getConfig().get(id)!=null){
                    sendPrefixMessage(p,"§cそのガチャは既に存在します");
                    return true;
                }
                dataYml.getConfig().set(id,"unsetted");
            case "edit":
                new ItemList(p,id).open();
        }
        return true;
    }
}
