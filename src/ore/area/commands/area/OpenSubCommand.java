package ore.area.commands.area;


import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import ore.area.AreaMainClass;
import ore.area.commands.SubCommand;
import ore.area.utils.area.AreaClass;

/**
 * @author 若水
 */
public class OpenSubCommand extends SubCommand {
    public OpenSubCommand(AreaMainClass plugin) {
        super(plugin);
    }


    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("ore.area.kq.open");
    }


    @Override
    public String getName() {
        return "开启";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"open"};
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length > 1){
            String name = args[1];
            AreaClass aClass = AreaClass.getAreaClass(name);
            if(aClass != null){
                aClass.setKey(true);
                aClass.save();
                sender.sendMessage("§e>> §b"+name+"矿区成功开启..");
            }else{
                sender.sendMessage("§e>> §c抱歉,"+name+"矿区不存在..");
            }
        }else{
            return false;
        }
        return true;
    }
}
