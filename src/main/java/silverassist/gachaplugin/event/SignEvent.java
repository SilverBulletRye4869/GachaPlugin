package silverassist.gachaplugin.event;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.gachaplugin.CustomConfig;
import silverassist.gachaplugin.GachaPlugin;
import silverassist.gachaplugin.menu.user.GachaOpen;

import static silverassist.gachaplugin.Util.PREFIX;

public class SignEvent implements Listener {
    private final GachaOpen GACHA_OPEN= GachaPlugin.getGachaOpen();

    public SignEvent(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if(!(e.getClickedBlock().getState() instanceof Sign))return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        String[] lines = sign.getLines();
        if(!lines[0].equals(PREFIX))return;
        if(!CustomConfig.existYml(lines[3]))return;
        GACHA_OPEN.open(e.getPlayer(),lines[3]);
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent e){
        if(!e.getPlayer().isOp())return;
        String lines[] = e.getLines();
        if(!lines[0].equals("gacha"))return;
        if(!CustomConfig.existYml(lines[3]))return;
        e.setLine(0,PREFIX);
        for(int i = 1;i<=2;i++)e.setLine(i,lines[i].replace("&","§"));


    }
}
