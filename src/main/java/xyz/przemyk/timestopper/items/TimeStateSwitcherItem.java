package xyz.przemyk.timestopper.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.capabilities.control.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.control.TimeState;

public class TimeStateSwitcherItem extends Item {

    public final TimeState OTHER_STATE;

    public TimeStateSwitcherItem(TimeState timeState) {
        super(new Properties().rarity(Rarity.EPIC).group(TimeStopperMod.TIME_STOPPER_ITEM_GROUP).maxStackSize(1));
        OTHER_STATE = timeState;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        boolean success = playerIn.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).map(h -> {
            if (h.getState() == TimeState.NORMAL) {
                TimeStopperMod.setTimeState(playerIn, OTHER_STATE, h);
                return true;
            }
            if (h.getState() == OTHER_STATE) {
                TimeStopperMod.setTimeState(playerIn, TimeState.NORMAL, h);
                return true;
            }
            playerIn.sendStatusMessage(new TranslationTextComponent("info." + TimeStopperMod.MODID + ".cannot_change_state"), true);
            return false;
        }).orElse(false);

        return success ? ActionResult.resultSuccess(playerIn.getHeldItem(handIn)) : ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
        // I'm not sure what this method does, but I guess that it counts items so that's ideal for this purpose
        if (player.inventory.func_234564_a_(itemStack -> itemStack.getItem() == this, 0, player.container.func_234641_j_()) == 1) {
            player.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h -> {
                if (h.getState() == OTHER_STATE) {
                    TimeStopperMod.setTimeState(player, TimeState.NORMAL, h);
                }
            });
        }
        return true;
    }
}
