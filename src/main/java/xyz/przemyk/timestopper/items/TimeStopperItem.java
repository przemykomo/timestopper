package xyz.przemyk.timestopper.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.entities.thrown.ThrownTimeStopperEntity;

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
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!playerIn.abilities.isCreativeMode) {
            itemStack.shrink(1);
        }

        if (worldIn.isRemote()) {
            return ActionResult.resultPass(itemStack);
        } else {
            ThrownTimeStopperEntity thrownStopper = new ThrownTimeStopperEntity(worldIn, playerIn);
            thrownStopper.setItem(itemStack);
            thrownStopper.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(thrownStopper);
            return ActionResult.resultConsume(itemStack);
        }
    }
}
