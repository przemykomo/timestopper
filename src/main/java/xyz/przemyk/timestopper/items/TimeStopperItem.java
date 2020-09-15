package xyz.przemyk.timestopper.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.capabilities.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.TimeState;
import xyz.przemyk.timestopper.network.PacketChangeTimeState;
import xyz.przemyk.timestopper.network.TimeStopperPacketHandler;

public class TimeStopperItem extends Item {

    public TimeStopperItem() {
        super(new Properties().rarity(Rarity.EPIC).group(TimeStopperMod.TIME_STOPPER_ITEM_GROUP).maxStackSize(1));
        setRegistryName("timestopper");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //TODO: use translated text component
        boolean success = playerIn.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).map(h -> {
            if (h.getState() == TimeState.NORMAL) {
                h.setState(TimeState.STOPPED);
                playerIn.sendStatusMessage(new StringTextComponent(h.getState().name()), true);
                if (!worldIn.isRemote) {
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(h.getState(), playerIn.getUniqueID()));
                }
                return true;
            } else if (h.getState() == TimeState.STOPPED) {
                h.setState(TimeState.NORMAL);
                playerIn.sendStatusMessage(new StringTextComponent(h.getState().name()), true);
                if (!worldIn.isRemote) {
                    TimeStopperPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketChangeTimeState(h.getState(), playerIn.getUniqueID()));
                }
                return true;
            }
            return false;
        }).orElse(false);

        return success ? ActionResult.resultSuccess(playerIn.getHeldItem(handIn)) : ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }
}
