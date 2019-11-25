package ore.area;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ore.area.commands.OreCommand;
import ore.area.commands.SpawnCommand;
import ore.area.utils.area.AreaClass;
import ore.area.utils.loadMoney;
import ore.area.utils.task.AreaLoadTask;
import ore.area.windows.ListenerWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 若水
 */
public class AreaMainClass extends PluginBase {

    private static final String PLAYER_NAME = "/Player";
    private static final String AREA_NAME = "/Area";
    private static final String LANGUAGE = "/language";
    private static AreaMainClass instance;

    private loadMoney money;

    private LinkedHashMap<String,Config> playerConfig = new LinkedHashMap<>();

    private static Config language;

    public LinkedHashMap<String, AreaClass> areas = new LinkedHashMap<>();


    public LinkedHashMap<Player,AreaClass> clickArea = new LinkedHashMap<>();

    public LinkedHashMap<String,String> canJoin = new LinkedHashMap<>();


    public LinkedList<String> transfer = new LinkedList<>();

    private LinkedList<String> timeLoad = new LinkedList<>();



    public static LinkedHashMap<String, Integer > timer = new LinkedHashMap<>();

    public LinkedHashMap<Player,LinkedList< Position>> pos = new LinkedHashMap<>();



    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        if(!new File(this.getDataFolder()+AREA_NAME).exists()){
            if(!new File(this.getDataFolder()+AREA_NAME).mkdirs()){
                this.getLogger().info("创建文件夹"+AREA_NAME+"失败....");
            }
        }
        if(!new File(this.getDataFolder()+PLAYER_NAME).exists()){
            if(!new File(this.getDataFolder()+PLAYER_NAME).mkdir()){
                this.getLogger().info("创建文件夹"+PLAYER_NAME+"失败");
            }
        }
        if(!new File(this.getDataFolder()+LANGUAGE).exists()){
            this.saveResource("language.yml");
        }


        language = new Config(this.getDataFolder()+LANGUAGE+".yml",Config.YAML);

        timeLoad.addAll(getConfig().getStringList("刷新矿区时间段"));

        getServer().getCommandMap().register("OreArea", new OreCommand(this));
        getServer().getCommandMap().register("OreArea", new SpawnCommand("spawn"));
        this.getServer().getPluginManager().registerEvents(new ListenerWindow(),this);
        this.getServer().getPluginManager().registerEvents(new ListenerEvents(),this);
        money = new loadMoney();
        readAllFiles();
        readAllPlayer();
    }


    public LinkedList<String> getTimeLoad() {
        return timeLoad;
    }

    public static String getLang(String lang) {
        return language.getString(lang);
    }

    public File getAreaFile(String name){
        return new File(this.getDataFolder()+AREA_NAME+"/"+name+".yml");
    }

    public loadMoney getMoney() {
        return money;
    }

    private File getPlayerFile(String name){
        return new File(this.getDataFolder()+PLAYER_NAME+"/"+name+".yml");
    }

    public static AreaMainClass getInstance() {
        return instance;
    }


    public Config getPlayerConfig(String name){
        if(!playerConfig.containsKey(name)){
            if(!getPlayerFile(name).exists()){
                this.saveResource("player.yml",PLAYER_NAME+"/"+name+".yml",false);
            }
            playerConfig.put(name,new Config(getPlayerFile(name),Config.YAML));
        }
        return playerConfig.get(name);
    }

    private void readAllPlayer(){
        for(String name:getPlayerFiles()){
            playerConfig.put(name, new Config(getPlayerFile(name),Config.YAML));
        }
    }

    private void readAllFiles(){
        for(String name:getAreaFiles()){
            AreaClass areaClass = AreaClass.getClassByConfig(name,new Config(AreaMainClass.getInstance().getAreaFile(name),Config.YAML));
            areas.put(name,areaClass);
            this.getServer().getScheduler().scheduleRepeatingTask(new AreaLoadTask(name),20);
        }
    }

    private String[] getPlayerFiles(){
        return getDefaultFiles(PLAYER_NAME);
    }

    private String[] getDefaultFiles(String playerName) {
        List<String> names = new ArrayList<>();
        File files = new File(this.getDataFolder()+ playerName);
        if(files.isDirectory()){
            File[] filesArray = files.listFiles();
            if(filesArray != null){
                if(filesArray.length>0){
                    for(File file : filesArray){
                        names.add( file.getName().substring(0, file.getName().lastIndexOf(".")));
                    }
                }
            }
        }
        return names.toArray(new String[0]);
    }

    private String[] getAreaFiles(){
        return getDefaultFiles(AREA_NAME);
    }

    /**
     * 获取是否进入矿区提示
     * */
    boolean isSendJoinMessage(){
        return getConfig().getBoolean("进入矿区是否提示");
    }

    /**
     * 获取是否离开矿区提示
     * */
    boolean isSendQuitMessage(){
        return getConfig().getBoolean("离开矿区是否提示");
    }

    /**
     * 获取是否开启购买解锁
     * */
    public boolean isKeyCanOpen(){
        return getConfig().getBoolean("是否开启玩家购买解锁");
    }

    /**
     * 获取是否显示传送粒子
     * */
    boolean canShowTransfer(){
        return getConfig().getBoolean("是否显示粒子");
    }



    /**
     * 获取传送时间
     * */
    int getTransferTime(){
        return getConfig().getInt("传送时间");
    }

    /**
     * 获取玩家传送矿区是否提示
     * */
    public boolean canSendTransferMessage(){
        return getConfig().getBoolean("玩家传送到矿区是否提示");
    }

    /**
     * 获取玩家传送矿区是否全服公告
     * */
    public boolean canSendTransferBroadCastMessage(){
        return getConfig().getBoolean("玩家传送矿区是否全服公告");
    }


    /**
     * 获取解锁矿区是否公告
     * */
    boolean canSendkeyArea(){
        return getConfig().getBoolean("玩家解锁矿区是否公告");
    }




    /**
     * 获取显示类型
     * */
    String getMessageType(){
        return getConfig().getString("提示类型").toLowerCase();
    }

    /**
     * 获取显示类型
     * */
    public String getTransferMessageType(){
        return getConfig().getString("玩家传送到矿区提示").toLowerCase();
    }


}
