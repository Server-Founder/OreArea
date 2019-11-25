package ore.area.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.DummyBossBar;
import ore.area.AreaMainClass;
import ore.area.utils.area.AreaClass;
import ore.area.utils.area.BlockClass;
import ore.area.utils.area.Vector;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 若水
 */
public class Tools {



    public static Position getDefaultPosition(Position pos1,Position pos2) {
        double y;
        if(pos1.getY() >= pos2.getY()){
            y = pos1.getY();
        }else{
            y = pos2.getY();
        }
        return pos1.setComponents(pos1.getX(),y,pos1.getZ());

    }

    public static ore.area.utils.area.Vector getVectorByMap(Map map, Level level){
        int sx ,sy ,sz ,ex ,ey ,ez ;
        sx = (int) map.get("startX");
        ex = (int) map.get("endX");
        sy = (int) map.get("startY");
        ey = (int) map.get("endY");
        sz = (int) map.get("startZ");
        ez = (int) map.get("endZ");
        return new Vector(level,sx,ex,sy,ey,sz,ez);
    }

    public static BlockClass getBlockClassByMap(String name, int value){
        Block block;
        if(name.split(":").length > 1){
            block = Block.get(Integer.parseInt(name.split(":")[0]),Integer.parseInt(name.split(":")[1]));
        }else{
            block = Block.get(Integer.parseInt(name));
        }
        return new BlockClass(block,value);
    }


    public static LinkedHashMap<String,Object> getMapByBlockClass(LinkedList<BlockClass> classes){
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        for(BlockClass blockClass : classes){
            Block block = blockClass.getBlock();
            String blockName;
            if(block.getDamage() == 0){
                blockName = block.getId()+"";
            }else{
                blockName = block.getId()+":"+block.getDamage();
            }
            map.put(blockName,blockClass.getSpawnRation());
        }
        return map;
    }

    /** 获取点击位置是否在矿区*/
    public static String getPlayerTouchArea(Position position){
        for(AreaClass areaClass:AreaMainClass.getInstance().areas.values()){
            Vector vector = areaClass.getPos().clone();
            vector.sort();
            if(position.level.getFolderName().equals(vector.getLevel().getFolderName())
                    && vector.getStartX() <= position.x && vector.getEndX() >= position.x
                    && vector.getStartY() <= position.y && vector.getEndY() >= position.y
                    && vector.getStartZ() <= position.z && vector.getEndZ() >= position.z){
                return areaClass.getName();
            }
        }
        return null;
    }

    /***
     *  判断方块是否在矿区世界
     * */
    public static boolean canInAreaLevel(Position pos){
        for(AreaClass areaClass:AreaMainClass.getInstance().areas.values()){
            if(areaClass.getPos().getLevel().getFolderName().equals(pos.getLevel().getFolderName())){
                return true;
            }
        }
        return false;
    }

    public static LinkedList<double[]> showParticle(int s) {
        double x,z,y;
        LinkedList<double[]> pos = new LinkedList<>();
        LinkedList<double[]> a = new LinkedList<>();
        double rr = s * 0.2;
        for(int i =0;i<=90;i+=10){
            x = rr * Math.cos(Math.toRadians(i));
            y = rr * Math.sin(Math.toRadians(i));
            a.add(new double[]{x,y});
            a.add(new double[]{x,-y});
            a.add(new double[]{-x,y});
            a.add(new double[]{-x,-y});
        }
        for(double[] b: a){
            for(int i=0;i<=90;i+=10){
                x = (s-b[0]) * Math.cos(Math.toRadians(i));
                z = (s-b[0]) * Math.sin(Math.toRadians(i));
                pos.add(new double[]{x,b[1],z});
                pos.add(new double[]{-z,b[1],x});
                pos.add(new double[]{-x,b[1],-z});
                pos.add(new double[]{z,b[1],-x});
            }
        }
        return pos;
    }

    public static LinkedList<AreaClass> sqrtAreaClass(){
        LinkedList<AreaClass> areaClass = new LinkedList<>();
        LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
        for(AreaClass c:AreaMainClass.getInstance().areas.values()){
            map.put(c.getName(),c.getLevel());
        }
        List<Map.Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        for(Map.Entry<String,Integer> mapping:list){
            areaClass.add(AreaClass.getAreaClass(mapping.getKey()));
        }
        return areaClass;
    }

    private static final Pattern PAT =  Pattern.compile("^[+]?([0-9]+(.[0-9]{1,2})?)$");
    /**
     * 判断是否为数字
     * */
    public static boolean isNumber(String source){
        boolean flag = false;
        try {
            if(PAT.matcher(source).matches()){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断是否存在区域重复
     * */
    public static String checkOverlap(Vector vector){
        vector.sort();
        for(AreaClass overlap: AreaMainClass.getInstance().areas.values()){
            if(overlap.getPos().getLevel().getFolderName().equals(vector.getLevel().getFolderName())){
                if(vector.getStartX() <= overlap.getPos().getEndX()
                        && vector.getEndX() >= overlap.getPos().getStartX() &&
                    vector.getStartY() <= overlap.getPos().getEndY()
                        && vector.getEndY() >= overlap.getPos().getStartY() &&
                    vector.getStartZ() <= overlap.getPos().getEndZ()
                        && vector.getEndZ() >= overlap.getPos().getStartZ()){
                    return overlap.getName();
                }
            }
        }
        return null;

    }

    public static void sendMessage(Player player, String message, String sub, String type){
        switch(type){
            case "tip":
                player.sendTip(message+sub);
                break;
            case "popup":
                player.sendPopup(message+sub);
                break;
            case "action":
                player.sendActionBar(message+sub);
                break;
            case "title":
                player.sendTitle(message,sub);
                break;
            case "boss":
                bossBar bar = new bossBar(player,0xbb075);
                bar.setLength(0);
                bar.setText(message+sub);
                player.createBossBar(bar.build());
                Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                    @Override
                    public void onRun(int i) {
                        player.removeBossBar(bar.getBossBarId());
                    }
                },60);
                break;
            default:break;
        }
    }
    private static class bossBar extends DummyBossBar.Builder{

        private final long bossBarId;

        private String text;
        private float length;
        bossBar(Player player, long bossBarId) {
            super(player);
            this.bossBarId = bossBarId;

        }

        public void setLength(float length) {
            this.length = length;
        }

        public float getLength() {
            return length;
        }

        void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        long getBossBarId() {
            return bossBarId;
        }
    }




    public static LinkedHashMap<String,Object> getPosMap(Vector vector){
        return new LinkedHashMap<String,Object>(){
            {
                put("startX",vector.getStartX());
                put("startY",vector.getStartY());
                put("startZ",vector.getStartZ());
                put("endX",vector.getEndX());
                put("endY",vector.getEndY());
                put("endZ",vector.getEndZ());

            }
        };
    }

    public static LinkedHashMap<String,Object> getPosMap(int x,int y,int z){
        return new LinkedHashMap<String,Object>(){
            {
                put("x",x);
                put("y",y);
                put("z",z);

            }
        };
    }

    /**
     * 判断是否在刷新时间段
     * */
    public static boolean inTime(){
        if(AreaMainClass.getInstance().getTimeLoad().size() > 0){
            TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            format.setTimeZone(timeZone);
            String s = format.format(new Date());
            int hour = Integer.parseInt(s.split(":")[0]);
            int minute = Integer.parseInt(s.split(":")[1]);
            int h,m,h1,m1;
            for(String fix:AreaMainClass.getInstance().getTimeLoad()){
                String[] lists = fix.split("-");
                h = Integer.parseInt(lists[0].trim().split(":")[0]);
                m = Integer.parseInt(lists[0].trim().split(":")[1]);
                h1 = Integer.parseInt(lists[1].trim().split(":")[0]);
                m1 = Integer.parseInt(lists[1].trim().split(":")[1]);
                if(hour >= h && hour <= h1 && minute >= m && minute <= m1){
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }


    public static Position getPositionByMap(Map transfer, Level level) {
        return Position.fromObject(new Vector3((int)transfer.get("x"),(int) transfer.get("y"), (int)transfer.get("z")),level);
    }
}
