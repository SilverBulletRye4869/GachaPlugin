package silverassist.gachaplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.mainSystem.Setup;
import silverassist.gachaplugin.mainSystem.Spin;

public class Command implements CommandExecutor {
    final private JavaPlugin plugin;
    private final Setup GACHA;

    public Command(JavaPlugin plugin, Setup gacha){
        this.plugin = plugin;
        this.GACHA = gacha;
        plugin.getCommand("gacha").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!sender.isOp())return true;
        try{
            if(!(sender instanceof Player))return true;
            Player p = (Player) sender;
            switch (args[0]){
                case "test":
                    if(Spin.isPlay(p))return true;
                    GACHA.getGacha("test").run(p);

                case "start":
                    Player runner = args.length==3 ? Bukkit.getPlayer(args[1]) : p;
                    GACHA.getGacha(args[1]).run(runner);

            }

        }catch (ArrayIndexOutOfBoundsException e){
            sender.sendMessage("§c引数が足りません！");
        }
        return true;
    }
}
