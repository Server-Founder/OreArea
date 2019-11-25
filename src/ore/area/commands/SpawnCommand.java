package ore.area.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.potion.Effect;
import ore.area.AreaMainClass;
import ore.area.utils.task.PlayerSpawnTask;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author 若水
 */
public class SpawnCommand extends Command {
    public SpawnCommand(String name) {
        super(name,"返回主城","/spawn");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(AreaMainClass.getInstance().transfer.contains(commandSender.getName())){
                commandSender.sendMessage("§e>> §c您已经有一个传送请求了");
                return true;
            }else{
                Map map = (Map) AreaMainClass.getInstance().getConfig().get("附加");
                Map back = (Map) map.get("回城");
                int time = (int) back.get("传送时间");
                AreaMainClass.getInstance().transfer.add(commandSender.getName());
                boolean can = (boolean) back.get("是否显示粒子");
                LinkedList<Effect> effects = new LinkedList<>();
                Map e = (Map) back.get("debuff");
                for(Object id : e.keySet()){
                    Effect effect = Effect.getEffect(Integer.parseInt(id.toString()));
                    effect.setDuration((Integer) e.get(id) * 20);
                    effects.add(effect);
                }
                for(Effect effect: effects){
                    ((Player) commandSender).addEffect(effect);
                }
                Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerSpawnTask((Player)commandSender,time,can,effects),20);
            }
        }
        return false;
    }


}
