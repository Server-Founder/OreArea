package ore.area.commands.area;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import cn.nukkit.level.Position;
import ore.area.AreaMainClass;
import ore.area.commands.SubCommand;

import java.util.LinkedList;


/**
 * @author 若水
 */
public class Pos2SubCommand extends SubCommand {
    public Pos2SubCommand(AreaMainClass plugin) {
        super(plugin);
    }


    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("ore.area.kq.pos") && sender.isPlayer();
    }

    @Override
    public String getName() {
        return "第二点";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pos2"};
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
       if(!AreaMainClass.getInstance().pos.containsKey((Player) sender)){
           sender.sendMessage("§e> §c请先设置第一点");
           return true;
       }else{
           LinkedList<Position> positions = AreaMainClass.getInstance().pos.get((Player) sender);
           Position pos1 = positions.get(0);
           if(!pos1.getLevel().getFolderName().equals(((Player) sender).level.getFolderName())){
               sender.sendMessage("§e>> §c两点必须在同一个世界..请重新设置");
               AreaMainClass.getInstance().pos.remove((Player) sender);
               return true;
           }
           positions.add(new Position(((Player) sender).getX(), ((Player) sender).getY()
                   ,((Player) sender).getZ(),((Player) sender).getLevel()));
           AreaMainClass.getInstance().pos.put((Player) sender, positions);
           sender.sendMessage("§e>> §6第二点创建成功.. §7请输入/kq 创建 <名称> 创建一个矿区吧");
       }
        return true;
    }
}
