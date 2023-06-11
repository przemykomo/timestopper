package xyz.przemyk.timestopper.setup;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
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
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TimeStopperMod.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<TimeStateSwitcherItem> STOPPER = ITEMS.register("timestopper", () -> new TimeStateSwitcherItem(TimeState.STOPPED));
    public static final RegistryObject<TimeStateSwitcherItem> ACCELERATOR = ITEMS.register("timeaccelerator", () -> new TimeStateSwitcherItem(TimeState.FAST));
    public static final RegistryObject<TimeStateSwitcherItem> DECELERATOR = ITEMS.register("timedecelerator", () -> new TimeStateSwitcherItem(TimeState.SLOW));

    public static final RegistryObject<CreativeModeTab> TIME_STOPPER_TAB = CREATIVE_MODE_TABS.register("time_stopper_tab", () -> CreativeModeTab.builder()
            .icon(() -> STOPPER.get().getDefaultInstance())
            .title(Component.translatable(TimeStopperMod.MODID + ".time_stopper_tab"))
            .displayItems((properties, output) -> {
                output.accept(STOPPER.get());
                output.accept(ACCELERATOR.get());
                output.accept(DECELERATOR.get());
            }).build());
}
