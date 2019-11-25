package ore.area;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.DummyBossBar;

import ore.area.events.PlayerBuyAreaEvent;
import ore.area.events.PlayerJoinAreaEvent;
import ore.area.events.PlayerQuitAreaEvent;
import ore.area.events.PlayerTransferAreaEvent;
import ore.area.utils.Tools;
import ore.area.utils.area.AreaClass;
import ore.area.utils.task.PlayerLoadTask;
import ore.area.utils.task.PlayerTransferTask;

import java.util.LinkedList;


/**
 * @author 若水
 */
public class ListenerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerLoadTask(player),20);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if(Tools.getPlayerTouchArea(block) == null){
            if(Tools.canInAreaLevel(block)){
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(Tools.getPlayerTouchArea(block) == null){
            if(Tools.canInAreaLevel(block)){
                event.setCancelled();
            }
        }
    }
    @EventHandler
    public void onTransfer(PlayerTransferAreaEvent event) {
        Player player = event.getPlayer();

        AreaClass aClass = event.getAreaClass();
        if (aClass != null) {

            boolean can = AreaMainClass.getInstance().canShowTransfer();
            if(AreaMainClass.getInstance().transfer.contains(player.getName())){
                player.sendMessage("§e>> §c您已经有一个传送请求了");
                return;
            }
            AreaMainClass.getInstance().transfer.add(player.getName());
            Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerTransferTask(player,AreaMainClass.getInstance().getTransferTime(),aClass,can),20);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if(from.getX() != to.getX() || from.getZ() != to.getZ()){
            if(AreaMainClass.getInstance().transfer.contains(player.getName())){
                AreaMainClass.getInstance().transfer.remove(player.getName());
                Tools.sendMessage(player,AreaMainClass.getLang("transfer.area.cancel"),
                        "",AreaMainClass.getInstance().getMessageType());
            }

        }
    }


    @EventHandler
    public void onPlayerJoinArea(PlayerJoinAreaEvent event){
        Player player = event.getPlayer();
        if(AreaMainClass.getInstance().isSendJoinMessage()){
            AreaClass areaClass = event.getAreaClass();
            if(areaClass != null){
                String title = AreaMainClass.getLang("join.area.title");
                title = title.replace("{name}",areaClass.getName());
                String sub = AreaMainClass.getLang("join.area.sub.title");
                sub = sub.replace("{sub}",areaClass.getSubMessage());
                Tools.sendMessage(player,title,sub,AreaMainClass.getInstance().getMessageType());
            }
        }
    }

    @EventHandler
    public void onPlayerQuitArea(PlayerQuitAreaEvent event){
        Player player = event.getPlayer();
        if(AreaMainClass.getInstance().isSendQuitMessage()){
            AreaClass areaClass = event.getAreaClass();
            if(areaClass != null){
                String title = AreaMainClass.getLang("quit.area.title");
                title = title.replace("{name}",areaClass.getName());
                String sub = AreaMainClass.getLang("quit.area.sub.title");
                sub = sub.replace("{sub}",areaClass.getSubMessage());
                Tools.sendMessage(player,title,sub,AreaMainClass.getInstance().getMessageType());
            }
        }
    }

    @EventHandler
    public void onBuyAreaEvent(PlayerBuyAreaEvent event){
        Player player = event.getPlayer();
        AreaClass areaClass = event.getAreaClass();
        if(areaClass != null){
            if(AreaMainClass.getInstance().canSendkeyArea()){
                String broad = AreaMainClass.getLang("buy.area.scauss");
                broad = broad.replace("{player}",player.getName())
                        .replace("{level}",areaClass.getLevel()+"")
                        .replace("{name}",areaClass.getName());
                Server.getInstance().broadcastMessage(broad);
            }
            String title = AreaMainClass.getLang("player.buy.area.success").replace("{name}",areaClass.getName());
            Tools.sendMessage(player,title,"",AreaMainClass.getInstance().getMessageType());
        }
    }


}
