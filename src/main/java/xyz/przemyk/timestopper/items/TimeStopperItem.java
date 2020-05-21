package xyz.przemyk.timestopper.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity;

public class TimeStopperItem extends Item {

    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    public TimeStopperItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).group(TimeStopperMod.TIME_STOPPER_ITEM_GROUP));
        setRegistryName("timestopper");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);

        if (!playerIn.abilities.isCreativeMode) {
            itemStack.shrink(1);
        }

        if (worldIn.isRemote) {
            return ActionResult.resultPass(itemStack);
        } else {
            worldIn.addEntity(new ThrownTimeStopperEntity(worldIn, playerIn.getPositionVec()));
            return ActionResult.resultConsume(itemStack);
        }
    }
}
