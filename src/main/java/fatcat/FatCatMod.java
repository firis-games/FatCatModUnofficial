package fatcat;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import fatcat.gui.GuiStatusHandler;
import fatcat.model.RenderFatCat;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = FatCatMod.MODID, version = FatCatMod.VERSION)
@EventBusSubscriber
public class FatCatMod {
    public static final String MODID = "fatcat";
    public static final String VERSION = "1.0.3.b";
	public static final int STATUS_GUI_ID = 0;

	public static Logger logger;
	
    /* デバッグモード */
	public static boolean DEBUG = true;
	
	// 育成モードをオフにするオプション
	public static boolean breeding_mode = true;
	
	// ロギング
	public static boolean logging = false;

    /** This is the starting index for all of our mod's item IDs */
    private static int modEntityIndex = 0;

	@Instance(MODID)
	public static FatCatMod instance;
	@SidedProxy(clientSide = "fatcat.ClientProxy", serverSide = "fatcat.CommonProxy")
	public static CommonProxy proxy;
	
	public Map<Integer, String> skinMap;
	public List<Integer> skinTypes;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
    	
    	//Entity登録
    	EntityRegistry.registerModEntity(new ResourceLocation(FatCatMod.MODID, "fatcat"),
    			EntityFatCat.class,
    			"fatcat", ++modEntityIndex, this, 64, 10, true);

    	EntityRegistry.registerModEntity(new ResourceLocation(FatCatMod.MODID, "fatcat_unko"),
    			EntityFatCat.class,
    			"fatcat_unko", ++modEntityIndex, this, 64, 10, true);

    	//Config
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());

    	config.load();
    	Property breeding_mode_property = config.get(Configuration.CATEGORY_GENERAL, "BreedingMode", true);
    	breeding_mode_property.setComment("Breeding MODE(true/false): FatCat status is fixed if you disable this option");
    	breeding_mode = breeding_mode_property.getBoolean(true);
    	Property logging_mode_property = config.get(Configuration.CATEGORY_GENERAL, "Logging", false);
    	logging_mode_property.setComment("logging for debug");
    	logging = logging_mode_property.getBoolean(false);
     	Property debug_property = config.get(Configuration.CATEGORY_GENERAL, "Debug", false);
    	debug_property.setComment("debugging mode for development");
    	DEBUG = debug_property.getBoolean(false);
    	config.save();
    	
	}
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	proxy.registerRenderers();
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiStatusHandler());
    	initSkinMap();
    }
    
    private void initSkinMap() {
		skinMap = new HashMap<Integer, String>();
		ArrayList<String> files = new ArrayList<String>();
		URL path = FatCatMod.class.getResource("/assets/fatcat/textures/models/cat/");
		String protocol = path.getProtocol();
		
		if ("file".equals(protocol)) {
			File modelDir = new File(path.getPath());
			for (File f : modelDir.listFiles()) {
				if (f.isDirectory()) {
					for (File skin : f.listFiles()) {
						files.add(skin.toURI().toString());
					}
				}
			}
//			System.out.println(files.toString());
		} else if ("jar".equals(protocol)) {
	        JarURLConnection jarUrlConnection = null;
	        JarFile jarFile = null;
	        try {
	        	try {
	        		jarUrlConnection = (JarURLConnection)path.openConnection();

	        		jarFile = jarUrlConnection.getJarFile();
	        		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
	        			JarEntry entry = e.nextElement();
	        			files.add(entry.getName());
	        		}

	        	} finally {
	        		if (jarFile != null) {
	        			jarFile.close();
	        		}
	        	}
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		} else {
			System.out.println("Error: unsupported protocol: " + protocol);
		}

		skinTypes = detectSkinFiles(files);
	}

	private ArrayList<Integer> detectSkinFiles(ArrayList<String> files) {
		ArrayList<Integer> types = new ArrayList<Integer>();
		Pattern integerRx = Pattern.compile(".*?/(\\d+)-.*\\.png$");
		Pattern nameRx = Pattern.compile(".*assets/fatcat/textures/models/cat/(.*\\.png)$");
		for (String png : files) {
			Matcher m = nameRx.matcher(png);
//			System.out.println("m=<"+m+">,png=<"+png+">");
			if (m.find()) {
				String name = m.group(1);
				Matcher m1 = integerRx.matcher(name);
				if (m1.find()) {
					Integer i = Integer.parseInt(m1.group(1));
					if (name.contains("joke")) {
						i += 1000;
					} 
//					System.out.println("name=<"+name+">,i=<"+i.toString()+">");
					skinMap.put(i, "fatcat:textures/models/cat/" + name);
					types.add(i);
				}
			}
		}
		java.util.Collections.sort(types);
		return types;
	}
	
	/**
     * クリエイティブタブ
     */
    public static final CreativeTabs FatCatModTab = new CreativeTabs("tabFatCat") {
    	@SideOnly(Side.CLIENT)
    	@Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(FcmItems.egg);
        }
    };
    
    /**
     * アイテムインスタンス保持用
     * アイテムIDを旧式にあわせて設定するため@ObjectHolderにて設定
     */
    @ObjectHolder(FatCatMod.MODID)
    public static class FcmItems {
    	
    	@ObjectHolder("fatcat_egg")
        public final static Item egg = null;

    	@ObjectHolder("fatcat_unko")
        public final static Item unko = null;
    	
    	@ObjectHolder("fatcat_brush")
        public final static Item brush = null;
    	
    	@ObjectHolder("fatcat_furball")
        public final static Item furball = null;
    	
    	@ObjectHolder("fatcat_feather_toy")
        public final static Item feather_toy = null;
    }
    
    /**
     * アイテム登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event)
    {
    	event.getRegistry().register(new ItemFatCatEgg()
    			.setRegistryName("fatcat_egg")
    			.setUnlocalizedName("fatcat_egg"));
    	
    	event.getRegistry().register(new ItemFatCatUnko()
    			.setRegistryName("fatcat_unko")
    			.setUnlocalizedName("fatcat_unko"));
    	
    	event.getRegistry().register(new ItemCatBrush()
    			.setRegistryName("fatcat_brush")
    			.setUnlocalizedName("fatcat_brush"));
    	
    	event.getRegistry().register(new ItemFeatherToy()
    			.setRegistryName("fatcat_feather_toy")
    			.setUnlocalizedName("fatcat_feather_toy"));
    	
    	event.getRegistry().register(new ItemFurball()
    			.setRegistryName("fatcat_furball")
    			.setUnlocalizedName("fatcat_furball"));
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    protected static void registerModels(ModelRegistryEvent event)
    {
    	//アイテムモデルの設定
    	ModelLoader.setCustomModelResourceLocation(FcmItems.egg, 0,
    			new ModelResourceLocation(FcmItems.egg.getRegistryName(), "inventory"));
    	
		ModelLoader.setCustomModelResourceLocation(FcmItems.unko, 0,
    			new ModelResourceLocation(FcmItems.unko.getRegistryName(), "inventory"));
		
    	ModelLoader.setCustomModelResourceLocation(FcmItems.brush, 0,
    			new ModelResourceLocation(FcmItems.brush.getRegistryName(), "inventory"));
    	
    	ModelLoader.setCustomModelResourceLocation(FcmItems.furball, 0,
    			new ModelResourceLocation(FcmItems.furball.getRegistryName(), "inventory"));
    	
    	ModelLoader.setCustomModelResourceLocation(FcmItems.feather_toy, 0,
    			new ModelResourceLocation(FcmItems.feather_toy.getRegistryName(), "inventory"));
    	
    	//Entityモデルの設定
    	RenderingRegistry.registerEntityRenderingHandler(
    			EntityFatCat.class, new IRenderFactory<EntityFatCat>() {
				@Override
				public Render<? super EntityFatCat> createRenderFor(RenderManager manager) {
					return new RenderFatCat(manager);
				}
    	});
    	
    }

}