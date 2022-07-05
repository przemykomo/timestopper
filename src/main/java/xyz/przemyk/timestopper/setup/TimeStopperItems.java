package xyz.przemyk.timestopper.setup;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.items.TimeStateSwitcherItem;

@SuppressWarnings("unused")
public class TimeStopperItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TimeStopperMod.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<TimeStateSwitcherItem> STOPPER = ITEMS.register("timestopper", () -> new TimeStateSwitcherItem(TimeState.STOPPED));
    public static final RegistryObject<TimeStateSwitcherItem> ACCELERATOR = ITEMS.register("timeaccelerator", () -> new TimeStateSwitcherItem(TimeState.FAST));
    public static final RegistryObject<TimeStateSwitcherItem> DECELERATOR = ITEMS.register("timedecelerator", () -> new TimeStateSwitcherItem(TimeState.SLOW));
}
