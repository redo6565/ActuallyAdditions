/*
 * This file ("ItemLaserWrench.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items;

import de.ellpeck.actuallyadditions.mod.items.base.ItemBase;
import de.ellpeck.actuallyadditions.mod.misc.LaserRelayConnectionHandler;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityLaserRelay;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.StringUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemLaserWrench extends ItemBase{

    public ItemLaserWrench(String name){
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing par7, float par8, float par9, float par10){
        if(!world.isRemote){
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityLaserRelay){
                if(ItemPhantomConnector.getStoredPosition(stack) == null){
                    ItemPhantomConnector.storeConnection(stack, pos.getX(), pos.getY(), pos.getZ(), world);
                    player.addChatComponentMessage(new ChatComponentText(StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+".laser.stored.desc")));
                }
                else{
                    BlockPos savedPos = ItemPhantomConnector.getStoredPosition(stack);
                    if(ItemPhantomConnector.getStoredWorld(stack) == world && world.getTileEntity(savedPos) instanceof TileEntityLaserRelay && LaserRelayConnectionHandler.getInstance().addConnection(savedPos, pos)){
                        ItemPhantomConnector.clearStorage(stack);

                        ((TileEntityLaserRelay)world.getTileEntity(savedPos)).sendUpdate();
                        ((TileEntityLaserRelay)world.getTileEntity(pos)).sendUpdate();

                        player.addChatComponentMessage(new ChatComponentText(StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+".laser.connected.desc")));
                    }
                    else{
                        player.addChatComponentMessage(new ChatComponentText(StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+".laser.cantConnect.desc")));
                        ItemPhantomConnector.clearStorage(stack);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean getShareTag(){
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5){
        if(ItemPhantomConnector.getStoredPosition(stack) == null){
            ItemPhantomConnector.clearStorage(stack);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean isHeld){
        BlockPos coords = ItemPhantomConnector.getStoredPosition(stack);
        if(coords != null){
            list.add(StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+".boundTo.desc")+":");
            list.add("X: "+coords.getX());
            list.add("Y: "+coords.getY());
            list.add("Z: "+coords.getZ());
            list.add(EnumChatFormatting.ITALIC+StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+".clearStorage.desc"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack){
        return EnumRarity.EPIC;
    }
}