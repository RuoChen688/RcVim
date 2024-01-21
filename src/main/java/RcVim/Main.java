package RcVim;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main extends PluginBase implements Listener {

    public static Main instance;

    public LinkedHashMap<String,Integer> loadVim = new LinkedHashMap<>();

    public Config config;

    public Config pConfig;

    public boolean isRcEntity;

    @Override
    public void onEnable(){
        instance = this;
        this.isRcEntity = this.getServer().getPluginManager().getPlugin("RcEntity") != null;
        this.saveResource("vim.yml","/Vim.yml",false);
        this.saveResource("player.yml","/Player.yml",false);
        this.saveResource("config.yml","/Config.yml",false);
        this.config = new Config(this.getDataFolder() + "/Config.yml",Config.YAML);
        this.pConfig = new Config(this.getDataFolder() + "/Player.yml",Config.YAML);
        if(this.isRcEntity) loadVimConfig();
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getLogger().info("插件加载成功");
    }

    public void loadVimConfig(){
        Config config = new Config(this.getDataFolder() + "/Vim.yml",Config.YAML);
        for(Map.Entry<String,Object> entry : config.getAll().entrySet()){
            if(entry.getValue() instanceof Integer) this.loadVim.put(entry.getKey(), (Integer) entry.getValue());
        }
    }

    public static int getPlayerVim(Player player){
        return Integer.parseInt(Main.instance.pConfig.getString(player.getName()).split(":")[1]);
    }

    public static void setPlayerVim(Player player,int vim){
        String date = Main.instance.pConfig.getString(player.getName()).split(":")[0];
        Main.instance.pConfig.set(player.getName(),date + ":" + vim);
        Main.instance.pConfig.save();
    }

    public static boolean isVimMore(Player player,int vim){
        return Main.getPlayerVim(player) >= vim;
    }

    public static int getEntityVim(String name){
        if(!Main.instance.loadVim.containsKey(name)) return Main.instance.config.getInt("默认体力");
        return Main.instance.loadVim.get(name);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player =  event.getPlayer();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(!this.pConfig.exists(player.getName())){
            Map<String,Object> map = this.pConfig.getAll();
            map.put(player.getName(),date + ":" + config.getInt("最大体力"));
            this.pConfig.setAll((LinkedHashMap<String, Object>) map);
            this.pConfig.save();
        }else{
            String[] arr = this.pConfig.getString(player.getName()).split(":");
            if(!arr[0].equals(date)){
                this.pConfig.set(player.getName(),date + ":" + config.getInt("最大体力"));
                this.pConfig.save();
            }
        }
    }

}
