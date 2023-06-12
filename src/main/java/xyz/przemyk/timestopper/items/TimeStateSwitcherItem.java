package xyz.przemyk.timestopper.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;
import xyz.przemyk.timestopper.capabilities.item.PearlEnergyProvider;
import xyz.przemyk.timestopper.capabilities.item.PearlEnergyStorage;

public class TimeStateSwitcherItem extends Item {

    public final TimeState OTHER_STATE;
    public final int ENERGY_CAPACITY;

    public TimeStateSwitcherItem(Properties properties, TimeState timeState, int energyCapacity) {
        super(properties);
        OTHER_STATE = timeState;
        ENERGY_CAPACITY = energyCapacity;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new PearlEnergyProvider(new PearlEnergyStorage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF8FF2D5;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getCapability(PearlEnergyProvider.PEARL_ENERGY_STORAGE_CAP).map(energy ->
                Math.round(13.0F - (float)(ENERGY_CAPACITY - energy.getEnergy()) * 13.0F / (float)ENERGY_CAPACITY)).orElse(0);
    }

    @Override
    public boolean isBarVisible(ItemStack p_150899_) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        boolean success = player.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h ->
                player.getItemInHand(hand).getCapability(PearlEnergyProvider.PEARL_ENERGY_STORAGE_CAP).map(energyStorage -> {
                    if (energyStorage.getEnergy() > 0) {
                        if (h.timeState == TimeState.NORMAL) {
                            TimeStopperMod.setTimeState(player, OTHER_STATE, h);
                            return true;
                        }
                        if (h.timeState == OTHER_STATE) {
                            TimeStopperMod.setTimeState(player, TimeState.NORMAL, h);
                            return true;
                        }
                        player.displayClientMessage(Component.translatable("info." + TimeStopperMod.MODID + ".cannot_change_state"), true);
                    } else {
                        player.displayClientMessage(Component.translatable("info." + TimeStopperMod.MODID + ".no_energy"), true);
                    }

                    return false;
        }).orElse(false)).orElse(false);

        return success ? InteractionResultHolder.success(player.getItemInHand(hand)) : InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        // todo: remove crafting slots
        if (player.getInventory().clearOrCountMatchingItems(itemStack -> itemStack.getItem() == this, 0, player.inventoryMenu.getCraftSlots()) == 1) {
            player.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).ifPresent(h -> {
                if (h.timeState == OTHER_STATE) {
                    TimeStopperMod.setTimeState(player, TimeState.NORMAL, h);
                }
            });
        }
        return true;
    }
}
