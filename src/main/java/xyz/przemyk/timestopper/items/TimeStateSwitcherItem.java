package xyz.przemyk.timestopper.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;

public class TimeStateSwitcherItem extends Item {

    public final TimeState OTHER_STATE;

    public TimeStateSwitcherItem(TimeState timeState) {
        super(new Properties().rarity(Rarity.EPIC).tab(TimeStopperMod.TIME_STOPPER_ITEM_GROUP).stacksTo(1));
        OTHER_STATE = timeState;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        boolean success = player.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).map(h -> {
            if (h.timeState == TimeState.NORMAL) {
                TimeStopperMod.setTimeState(player, OTHER_STATE, h);
                return true;
            }
            if (h.timeState == OTHER_STATE) {
                TimeStopperMod.setTimeState(player, TimeState.NORMAL, h);
                return true;
            }
            player.displayClientMessage(Component.translatable("info." + TimeStopperMod.MODID + ".cannot_change_state"), true);
            return false;
        }).orElse(false);

        return success ? InteractionResultHolder.success(player.getItemInHand(hand)) : InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        // I'm not sure what this method does, but I guess that it counts items so that's ideal for this purpose
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
