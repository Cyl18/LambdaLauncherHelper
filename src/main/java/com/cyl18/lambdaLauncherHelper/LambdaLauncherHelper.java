package com.cyl18.lambdaLauncherHelper;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.relauncher.Side;
import org.lwjgl.opengl.Display;

@Mod(modid = LambdaLauncherHelper.MODID, version = LambdaLauncherHelper.VERSION)
public class LambdaLauncherHelper
{
    public static final String MODID = "LambdaLauncherHelper";
    public static final String VERSION = "1.0";
    public static ModMetadata meta;
    @Instance("LambdaLauncherHelper")
    public static LambdaLauncherHelper instance;

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        try {
            File file = new File(".\\title.txt");
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),"utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = bufferedReader.readLine();
            read.close();
            Display.setTitle(lineTxt);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ModLoadingListener thisListener = null;
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod instanceof FMLModContainer) {
                EventBus bus = null;
                try {
                    // Its a bit questionable to be changing FML itself, but reflection is better than ASM transforming
                    // forge
                    Field f = FMLModContainer.class.getDeclaredField("eventBus");
                    f.setAccessible(true);
                    bus = (EventBus) f.get(mod);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
                if (bus != null) {
                    if (mod.getModId().equals("LambdaLauncherHelper")) {
                        thisListener = new ModLoadingListener(mod);
                        bus.register(thisListener);
                    }
                    else
                        bus.register(new ModLoadingListener(mod));
                }
            }
        }
        if (thisListener != null)
            ModLoadingListener.doProgress(ModLoadingListener.State.CONSTRUCT, thisListener);
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        FMLCommonHandler.instance().bus().register(instance);
        meta = event.getModMetadata();
    }

    public static boolean openedMainMenu;


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void guiOpen(GuiOpenEvent event) {
        if (event.gui != null && event.gui instanceof GuiMainMenu){
            if (!openedMainMenu)
            System.out.println("GuiMainMenu Loaded");
            openedMainMenu = true;
        }

    }

}
