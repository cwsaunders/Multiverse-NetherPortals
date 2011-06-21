package com.onarandombox.MultiverseNetherPortals;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.DebugLog;

public class MultiverseNetherPortals extends JavaPlugin {
	
	public static final Logger log = Logger.getLogger("Minecraft");
	public static final String logPrefix = "[MultiVerse-NetherPortals] ";
	private static final String NETEHR_PORTALS_CONFIG = "config.yml";
	protected static DebugLog debugLog;
	protected MultiverseCore core;
	protected MVNPPluginListener pluginListener;
	protected MVNPEntityListener entityListener;
	protected MVNPPlayerListener playerListener;
	protected Configuration MVNPconfig;
	private static final String DEFAULT_NETHER_SUFFIX = "_nether";
	protected String netherPrefix = "";
	protected String netherSuffix = DEFAULT_NETHER_SUFFIX;
	
	@Override
	public void onEnable() {
		core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

	    // Test if the Core was found, if not we'll disable this plugin.
        if (core == null) {
            log.info(logPrefix + "Multiverse-Core not found, will keep looking.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
		this.pluginListener = new MVNPPluginListener(this);
		this.entityListener = new MVNPEntityListener(this);
		this.playerListener = new MVNPPlayerListener(this);
		// Register the PLUGIN_ENABLE Event as we will need to keep an eye out for the Core Enabling if we don't find it initially.
		this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Type.ENTITY_PORTAL_ENTER, entityListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());
		
		loadConfig();
	}
	
	private void loadConfig() {
		this.MVNPconfig = new Configuration(new File(this.getDataFolder(), NETEHR_PORTALS_CONFIG));
		this.MVNPconfig.load();
		
		this.netherPrefix = this.MVNPconfig.getString("netherportals.name.prefix", netherPrefix);
		this.netherSuffix = this.MVNPconfig.getString("netherportals.name.suffix", netherSuffix);
		
		if(this.netherPrefix.length() == 0 && this.netherSuffix.length() == 0) {
			log.warning(logPrefix + "I didn't find a prefix OR a suffix defined! I made the suffix \"" + DEFAULT_NETHER_SUFFIX + "\" for you.");
			this.netherSuffix = this.MVNPconfig.getString("netherportals.name.suffix", netherSuffix);
		}
		
		this.MVNPconfig.save();
	}

	@Override
	public void onDisable() {
		log.info(logPrefix + "- Disabled");
	}
	
	@Override
	public void onLoad() {
		getDataFolder().mkdirs();
		debugLog = new DebugLog("Multiverse-NetherPortals", getDataFolder() + File.separator + "debug.log");
	}
	
	/**
	 * Parse the Authors Array into a readable String with ',' and 'and'.
	 * 
	 * @return
	 */
	private String getAuthors() {
		String authors = "";
		for (int i = 0; i < this.getDescription().getAuthors().size(); i++) {
			if (i == this.getDescription().getAuthors().size() - 1) {
				authors += " and " + this.getDescription().getAuthors().get(i);
			} else {
				authors += ", " + this.getDescription().getAuthors().get(i);
			}
		}
		return authors.substring(2);
	}
}
