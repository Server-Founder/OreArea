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
public class Pos1SubCommand extends SubCommand {

    public Pos1SubCommand(AreaMainClass plugin) {
        super(plugin);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("ore.area.kq.pos") && sender.isPlayer();
    }

    @Override
    public String getName() {
        return "第一点";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pos1"};
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            LinkedList<Position> positions = new LinkedList<>();
            positions.add(new Position(((Player) sender).getX(), ((Player) sender).getY()
                    ,((Player) sender).getZ(),((Player) sender).getLevel()));
            AreaMainClass.getInstance().pos.put((Player) sender, positions);
            sender.sendMessage("§e>> §a第一点设置成功 请设置第二点");
        }

        return true;
    }
}
