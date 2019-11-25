package ore.area.utils.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import ore.area.AreaMainClass;
import ore.area.utils.Tools;
import ore.area.utils.area.AreaClass;

import java.util.LinkedList;

/**
 * @author 若水
 */
public class PlayerTransferTask extends Task {

    private Player player;
    private int time;
    private AreaClass aClass;
    private boolean can;
    public PlayerTransferTask(Player player, int time, AreaClass aClass,boolean can){
        this.player = player;
        this.time = time;
        this.aClass = aClass;
        this.can = can;
    }
    @Override
    public void onRun(int i) {
        if(player.isOnline()){
            if(!AreaMainClass.getInstance().transfer.contains(player.getName())){
                this.cancel();
                return;
            }
            if(time > 0){
                time--;
                if(can){
                    LinkedList<double[]> pos = Tools.showParticle(time);
                    for (double[] p : pos){
                        player.getLevel().addParticle(new FlameParticle(
                                new Vector3(p[0] + player.getX(),p[1]+player.getY()+2,p[2]+player.getZ())));
                    }
                }
                String title = AreaMainClass.getLang("tansfer.area.message")
                        .replace("{name}",aClass.getName());
                String sub =  AreaMainClass.getLang("tansfer.area.sub.message").replace("{s}"
                        ,time+"");
                Tools.sendMessage(player,title,sub,AreaMainClass.getInstance().getTransferMessageType());
            }else{
                AreaMainClass.getInstance().transfer.remove(player.getName());
                player.teleport(aClass.getTransfer());
                if(AreaMainClass.getInstance().canSendTransferBroadCastMessage()){
                    Server.getInstance().broadcastMessage(AreaMainClass.getLang("transaction.area.scauss")
                            .replace("{player}",player.getName()
                                    .replace("{name}",aClass.getName())));
                }
                if(AreaMainClass.getInstance().canSendTransferMessage()){
                    Tools.sendMessage(player,AreaMainClass.getLang("transaction.area.scauss")
                                    .replace("{player}","你")
                                            .replace("{name}",aClass.getName()),aClass.getSubMessage()
                            ,AreaMainClass.getInstance().getTransferMessageType());
                }
                this.cancel();
            }
        }else{
            this.cancel();
        }
    }
}
