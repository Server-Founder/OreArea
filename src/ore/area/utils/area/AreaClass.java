package ore.area.utils.area;

import cn.nukkit.Server;


import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import ore.area.AreaMainClass;
import ore.area.utils.Tools;
import ore.area.utils.task.AreaLoadTask;
import ore.area.utils.task.AsyncBlockTask;


import java.util.*;

/**
 * @author 若水
 */
public class AreaClass {

    private Vector pos;

    private boolean key;

    private double money;

    private double transferMoney;

    private Position transfer;

    private LinkedList<BlockClass> blocks;

    private String name;

    private int reset;

    private int level;

    private String message;

    private String subMessage;

    private String buttonImage;




    private AreaClass(int level, String name, Vector pos, double money, Position transfer, boolean key, int reset, LinkedList<BlockClass> spawnBlocks){
        this.pos = pos;
        this.money = money;
        this.transfer = transfer;
        this.key = key;
        this.blocks = spawnBlocks;
        this.name = name;
        this.reset = reset;
        this.level = level;

    }
    /**
     * 获取传送金钱
     * */

    public double getTransferMoney() {
        return transferMoney;
    }

    public void setTransferMoney(double transferMoney) {
        this.transferMoney = transferMoney;
    }

    /**
     * 设置解锁矿区金钱
     * */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * 获取矿区级别 (一般用作排序)
     * */
    public int getLevel() {
        return level;
    }

    /**
     * 设置矿区级别
     * */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获取矿区名称
     * */
    public String getName() {
        return name;
    }

    /**
     * 获取刷新时间
     * */
    public int getReset() {
        return reset;
    }

    /**
     *  根据 {@link Vector} 创建矿区
     * */
    public static boolean createAreaClass(String name, Vector pos){
        if(!AreaMainClass.getInstance().areas.containsKey(name)){
            if(Tools.checkOverlap(pos.clone()) == null){
                AreaMainClass.getInstance().saveResource("area.yml","/Area/"+name+".yml",false);

                Config config = new Config(AreaMainClass.getInstance().getAreaFile(name),Config.YAML);
                config.set("level",pos.getLevel().getFolderName());
                config.set("pos",Tools.getPosMap(pos));
                int y;
                if(pos.getEndY() > pos.getStartY()){
                    y = pos.getEndY();
                }else{
                    y = pos.getStartY();
                }
                config.set("transfer",Tools.getPosMap((pos.getStartX() + pos.getEndX()) / 2,(y+1),(pos.getStartZ() + pos.getEndZ()) / 2));
                config.save();
                AreaClass cn = getClassByConfig(name,config);
                if(cn != null){
                    AreaMainClass.getInstance().areas.put(name,cn);
                    Server.getInstance().getScheduler().scheduleRepeatingTask(new AreaLoadTask(name),20);
                    cn.setBlock();
                    return true;
                }
            }
        }
        return false;
    }


    private void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取矿区介绍
     * */
    public String getMessage() {
        return message;
    }


    /**
     * 设置矿区小标题
     * */
    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }

    /**
     * 设置矿区传送点
     * */
    public void setTransfer(Position transfer) {
        this.transfer = transfer;
    }


    /**
     * 获取矿区传送点
     * */
    public Position getTransfer() {
        return transfer;
    }


    /**
     * 获取矿区小标题
     * */
    public String getSubMessage() {
        return subMessage;
    }


    /**
     * 通过矿区名称获取缓存在服务器的 {@link AreaClass}
     * */
    public static AreaClass getAreaClass(String name){
        if(AreaMainClass.getInstance().areas.containsKey(name)){
            return AreaMainClass.getInstance().areas.get(name);
        }
        return null;
    }



    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    /**
     * 获取矿区价值
     * */
    public double getMoney() {
        return money;
    }

    public boolean isKey() {
        return key;
    }

    public Vector getPos() {
        return pos;
    }


    public LinkedList<BlockClass> getBlocks() {
        return blocks;
    }


    /**
     * 设置是否开启矿区
     * */
    public void setKey(boolean key) {
        this.key = key;
    }


    /**
     * 通过Config 文件 获取 {@link AreaClass}
     * */
    public static AreaClass getClassByConfig(String name,Config config){
        Level level = Server.getInstance().getLevelByName(config.getString("level"));
        if(level != null){
            Vector pos = Tools.getVectorByMap((Map) config.get("pos"),level);
            boolean key = config.getBoolean("key");
            Position transfer = Tools.getPositionByMap((Map) config.get("transfer"),level);
            double money = config.getDouble("price");
            LinkedList<BlockClass> blocks = new LinkedList<>();
            Map map = (Map)config.get("spawnBlock");
            for(Object s:map.keySet()){
                blocks.add(Tools.getBlockClassByMap(String.valueOf(s), (int)map.get(s)));
            }
            AreaClass c =  new AreaClass(config.getInt("areaLevel"),name,pos,money,transfer,key,config.getInt("resetTime(s)"),blocks);
            c.setMessage(config.getString("message"));
            c.setSubMessage(config.getString("subMessage"));
            c.setButtonImage(config.getString("buttonImage"));
            c.setTransferMoney(config.getDouble("transferMoney"));
            return c;
        }
        return null;
    }


    /**
     * 设置按键图片
     *  path: 路径
     * */
    private void setButtonImage(String buttonImage) {
        this.buttonImage = buttonImage;
    }

    /**
     * 保存矿区数据
     * */
    public void save(){
        Config config = new Config(AreaMainClass.getInstance().getAreaFile(name),Config.YAML);
        config.set("level",pos.getLevel().getFolderName());
        config.set("areaLevel",level);
        config.set("pos",Tools.getPosMap(pos));
        config.set("price",money);
        config.set("resetTime(s)",reset);
        config.set("transfer",Tools.getPosMap((int) transfer.x,(int) transfer.y,(int) transfer.z));
        config.set("transferMoney",transferMoney);
        config.set("key",key);
        config.set("message",message);
        config.set("subMessage",subMessage);
        config.set("spawnBlock",Tools.getMapByBlockClass(blocks));
        config.save();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AreaClass){
            return ((AreaClass) obj).getName().equals(name);
        }
        return false;
    }

    /**
     * 为矿区填充方块
     * */
    public void setBlock(){
        if(isKey()){
            Server.getInstance().getScheduler().scheduleAsyncTask(AreaMainClass.getInstance(),new AsyncBlockTask(this));
        }
    }
    /**
     * 获取按键图片...
     * */
    public ElementButtonImageData getButtonImage(){
        String type;
        String path;
        if(buttonImage.split(":").length > 1){
            String objPath = buttonImage.split(":")[0];
            if(objPath.equals(ElementButtonImageData.IMAGE_DATA_TYPE_PATH)
                    || objPath.equals(ElementButtonImageData.IMAGE_DATA_TYPE_URL)){
                type = objPath;
                path = buttonImage.split(":")[1];
            }else{
                type = buttonImage.split(":")[1];
                path = buttonImage.split(":")[0];
            }
        }else{
            type = ElementButtonImageData.IMAGE_DATA_TYPE_PATH;
            path = buttonImage;
        }
        return new ElementButtonImageData(type, path);
    }


    /**
     * 删除矿区
     * */
    public void delete(){
        if(!AreaMainClass.getInstance().getAreaFile(name).delete()){
            Server.getInstance().getLogger().warning("删除矿区 "+name+"失败");
        }else{
            key = false;
            AreaMainClass.getInstance().areas.remove(name);
        }
    }
}
