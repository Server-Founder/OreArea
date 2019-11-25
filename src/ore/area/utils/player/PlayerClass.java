package ore.area.utils.player;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import ore.area.AreaMainClass;


import java.util.LinkedList;

public class PlayerClass {

    private Player player;

    private LinkedList<String> keyAreas;

    private PlayerClass(Player player){
        this.player = player;

    }

    private void setKeyAreas(LinkedList<String> keyAreas) {
        this.keyAreas = keyAreas;
    }

    public LinkedList<String> getKeyAreas() {
        return keyAreas;
    }

    public boolean canKey(String area){
        return keyAreas.contains(area);
    }


    public void addKey(String area){
        keyAreas.add(area);
        save();
    }

    public Player getPlayer() {
        return player;
    }

    public static PlayerClass getPlayerClass(Player player){
        PlayerClass playerClass = new PlayerClass(player);
        playerClass.setKeyAreas(new LinkedList<>(AreaMainClass.getInstance()
                .getPlayerConfig(player.getName()).getStringList("area")));
        return playerClass;

    }

    private void save(){
        Config cn = AreaMainClass.getInstance().getPlayerConfig(player.getName());
        cn.set("area",keyAreas);
        cn.save();
    }
}
