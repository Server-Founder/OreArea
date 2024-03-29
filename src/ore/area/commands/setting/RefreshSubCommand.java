package ore.area.commands.setting;

import cn.nukkit.command.CommandSender;
import ore.area.AreaMainClass;
import ore.area.commands.SubCommand;
import ore.area.utils.area.AreaClass;

public class RefreshSubCommand extends SubCommand {
    public RefreshSubCommand(AreaMainClass plugin) {
        super(plugin);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("ore.area.kq.refresh");
    }

    @Override
    public String getName() {
        return "refresh";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"刷新"};
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length == 2){
            String name = args[1];
            AreaClass areaClass = AreaClass.getAreaClass(name);
            if(areaClass != null){
                areaClass.setBlock();
                sender.sendMessage("§e>> §a矿区"+name+"刷新成功");
                return true;
            }else{
                sender.sendMessage("§e>> §c不存在"+name+"矿区");
                return true;
            }
        }
        return false;
    }
}
