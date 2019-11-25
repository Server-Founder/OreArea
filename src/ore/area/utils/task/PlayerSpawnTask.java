package ore.area.utils.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import ore.area.AreaMainClass;
import ore.area.utils.Tools;

import java.util.LinkedList;

public class PlayerSpawnTask extends Task {

    private Player player;
    private int time;
    private boolean can;
    private LinkedList<Effect> effects;
    public PlayerSpawnTask(Player player,int time,boolean can,LinkedList<Effect> effects){
        this.player = player;
        this.time = time;
        this.can = can;
        this.effects = effects;
    }

    @Override
    public void onRun(int i) {
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
                            new Vector3(p[0] + player.getX(),p[1]+player.getY(),p[2]+player.getZ())));
                }
            }
            String title = AreaMainClass.getLang("tansfer.area.message")
                    .replace("{name}","主城");
            String sub =  AreaMainClass.getLang("tansfer.area.sub.message").replace("{s}"
                    ,time+"");
            player.sendTitle(title,sub);
        }else{
            AreaMainClass.getInstance().transfer.remove(player.getName());
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
            for (Effect effect :effects){
                player.removeEffect(effect.getId());
            }
            if(AreaMainClass.getInstance().canSendTransferBroadCastMessage()){
                Server.getInstance().broadcastMessage(AreaMainClass.getLang("transaction.area.scauss")
                        .replace("{player}",player.getName()
                                .replace("{name}","主城")));
            }
            if(AreaMainClass.getInstance().canSendTransferMessage()){
                player.sendTitle(AreaMainClass.getLang("transaction.area.scauss")
                        .replace("{player}","你")
                                .replace("{name}","主城"));
            }
            this.cancel();
        }
    }
}
