package io.chaws.textutilities.handlers;

import io.chaws.textutilities.TextUtilities;
import io.chaws.textutilities.config.TextUtilitiesConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static io.chaws.textutilities.utils.PlayerUtils.isHoldingSign;
import static io.chaws.textutilities.utils.PlayerUtils.isHoldingSignChangingItem;

public class ClickThroughHandler {
    public static void initialize() {
        UseBlockCallback.EVENT.register(ClickThroughHandler::handleUseBlock);
        UseEntityCallback.EVENT.register(ClickThroughHandler::handleUseEntity);
    }

    private static InteractionResult handleUseBlock(
            final Player player,
            final Level world,
            final InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        var config = TextUtilities.getConfig();

        var clickedSide = hitResult.getDirection();
        var clickedBlockPos = hitResult.getBlockPos();

        return tryClickThrough(world, player, config, clickedBlockPos, clickedSide, hand)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    private static InteractionResult handleUseEntity(
            final Player player,
            final Level world,
            final InteractionHand hand,
            final Entity entity,
            final @Nullable EntityHitResult entityHitResult
    ) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (entityHitResult == null) {
            return InteractionResult.PASS;
        }

        var config = TextUtilities.getConfig();

        if (!canClickThroughEntity(config, entity)) {
            return InteractionResult.PASS;
        }

        var entityFacing = entity.getDirection();

        if (entity instanceof HangingEntity decorationEntity) {
            var decorationBlockPos = decorationEntity.getPos().relative(entityFacing.getOpposite());

            useBlock(world, player, decorationBlockPos, entityFacing, hand);

            return InteractionResult.SUCCESS;
        }

        //HACK: Need to find a better way of detecting the side of the entity that was clicked
        var exactHitPosition = entityHitResult.getLocation();
        var hitPosition = new Vec3i(
                (int) Math.round(exactHitPosition.x),
                (int) Math.round(exactHitPosition.y),
                (int) Math.round(exactHitPosition.z)
        );

        var clickedSide = player.getDirection().getOpposite();
        var clickedEntityPos = new BlockPos(hitPosition);

        return tryClickThrough(world, player, config, clickedEntityPos, clickedSide, hand)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    private static boolean tryClickThrough(
            final Level world,
            final Player player,
            final TextUtilitiesConfig config,
            final BlockPos clickedBlockPos,
            final Direction clickedBlockSide,
            final InteractionHand hand
    ) {
        //? if <1.21.2 {
        /*var playerFacing = player.getDirection().getNormal();
        *///?} else {
        var playerFacing = player.getDirection().getUnitVec3i();
         //?}
        var attachedBlockPos = clickedBlockPos.offset(playerFacing);
        var blockState = world.getBlockState(clickedBlockPos);

        var blockEntity = world.getBlockEntity(clickedBlockPos);
        if (blockEntity != null && canClickThroughBlockEntity(player, config, blockEntity)) {
            useBlock(world, player, attachedBlockPos, clickedBlockSide, hand);
            return true;
        }

        var block = blockState.getBlock();
        if (canClickThroughBlock(config, block)) {
            useBlock(world, player, attachedBlockPos, clickedBlockSide, hand);
            return true;
        }

        return false;
    }

    private static boolean canClickThroughBlock(
            final TextUtilitiesConfig config,
            final Block block
    ) {
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        return config.additionalClickThroughIdentifiers.contains(blockId.toString());
    }

    private static boolean canClickThroughBlockEntity(
            final Player player,
            final TextUtilitiesConfig config,
            final BlockEntity blockEntity
    ) {
        if (blockEntity instanceof SignBlockEntity) {
            // Dyes and Ink Sacs can be applied to signs directly when they are in the main hand
            return
                    config.signClickThroughEnabled &&
                            !isHoldingSignChangingItem(player, InteractionHand.MAIN_HAND) &&
                            // TODO: Remove this once we know sign entity inherits SignChangingItem
                            !isHoldingSign(player, InteractionHand.MAIN_HAND);
        }

        var blockEntityType = blockEntity.getType();
        var blockIdentifier = BlockEntityType.getKey(blockEntityType);
        if (blockIdentifier == null) {
            return false;
        }

        return config.additionalClickThroughIdentifiers.contains(blockIdentifier.toString());
    }

    private static boolean canClickThroughEntity(
            final TextUtilitiesConfig config,
            final Entity entity
    ) {
        if (entity instanceof ItemFrame itemFrameEntity) {
            if (!config.itemFrameClickThroughEnabled) {
                return false;
            }

            // If the item frame has no item attached to it,
            // attach the item and don't click through to the chest.
            var attachedItem = itemFrameEntity.getItem();
            return !attachedItem.is(Items.AIR);
        }

        var entityType = entity.getType();
        var entityIdentifier = EntityType.getKey(entityType);
        if (entityIdentifier == null) {
            return false;
        }

        return config.additionalClickThroughIdentifiers.contains(entityIdentifier.toString());
    }

    private static void useBlock(
            final Level world,
            final Player player,
            final BlockPos attachedBlockPos,
            final Direction clickedSide,
            final InteractionHand hand
    ) {
        var attachedBlockState = world.getBlockState(attachedBlockPos);
        var attachedBlockHitResult = new BlockHitResult(
                attachedBlockPos.getCenter(),
                clickedSide,
                attachedBlockPos,
                false
        );

        //? if >=1.20.5 {
        attachedBlockState.useWithoutItem(world, player, attachedBlockHitResult);
        //?} else {
         /*attachedBlockState.use(world, player, hand, attachedBlockHitResult);
        *///?}
    }
}
