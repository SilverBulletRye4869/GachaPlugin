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
import silverassist.gachaplugin.menu.admin.GachaList;
import silverassist.gachaplugin.menu.admin.MainMenu;
import silverassist.gachaplugin.menu.user.GachaOpen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static silverassist.gachaplugin.Util.sendPrefixMessage;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;

    private final Setup GACHA_SYSTEM = GachaPlugin.getGachaSystem();
    private final GachaOpen GACHA_OPEN = GachaPlugin.getGachaOpen();


    public Command(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getCommand("gacha").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!sender.isOp() || !(sender instanceof Player) || args.length == 0)return true;

        Player p = (Player) sender;
        String id = null;
        if(args.length>1){
            id = args[1];
            if(id.equals("__debug__")){
                sendPrefixMessage(p,"§c§lそのIDは唯一の使用できないIDです");
                return true;
            }
        }
        switch (args[0]){
            case "test":
                if(Spin.isPlay(p))return true;

                GACHA_SYSTEM.getGacha("__debug__").run(p);
                return true;
            case "start":
                if(args.length < 2){
                    sendPrefixMessage(p,"§cガチャを指定してください");
                    return true;
                }
                Player runner = args.length==3 ? Bukkit.getPlayer(args[2]) : p;
                Spin gacha = GACHA_SYSTEM.getGacha(id);
                if(gacha!=null)gacha.run(runner);
                else sendPrefixMessage(p,"§cガチャが見つかりません");

                return true;
            case "create":
                if(!CustomConfig.existYml(id)){
                    YamlConfiguration DATA = CustomConfig.getYmlByID(id);
                    DATA.set("money",0);
                    DATA.set("item",new ItemStack(Material.AIR));
                    CustomConfig.saveYmlByID(id);
                    //DATA.save(CustomConfig.getYmlFileByID(id));
                }

            case "edit":
                if(!CustomConfig.existYml(id)){
                    sendPrefixMessage(p,"§cガチャが存在しません");
                    return true;
                }
                new MainMenu(p,id).open();
                return true;
            case "reload":
                if(args.length<2)return true;
                if(!GACHA_SYSTEM.reloadGacha(args[1])){
                    sendPrefixMessage(p,"§cガチャのreloadに失敗しました。詳細はコンソールを確認してください。");
                    return true;
                };
                sendPrefixMessage(p,"§aガチャをreloadしました");
                return true;
            case "reloadall":
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        GACHA_SYSTEM.getLoadedGachaSet().forEach(loadedGacha -> {
                            GACHA_SYSTEM.reloadGacha(loadedGacha);
                        });
                        sendPrefixMessage(p,"§a全てのガチャをreloadしました");
                    }
                });
                return true;
            case "open":
                GACHA_OPEN.open(p,id);
                return true;

            case "list":
                Stream<Path> stream=null;
                try {
                    stream = Files.list(Paths.get(plugin.getDataFolder().getPath()+"/data"));
                } catch (IOException e) {
                    sendPrefixMessage(p,"dataフォルダの取得に失敗しました");
                    new IOException("dataフォルダの取得に失敗しました");
                    return true;
                }
                new GachaList(p,stream);
            }
        return true;
    }
}
