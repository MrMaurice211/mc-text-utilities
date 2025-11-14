package io.chaws.textutilities.handlers;

import io.chaws.textutilities.TextUtilities;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import static io.chaws.textutilities.utils.PlayerUtils.*;

public class SignEditHandler {
	public static void initialize() {
		UseBlockCallback.EVENT.register(SignEditHandler::onUseSignBlock);
	}

	private static InteractionResult onUseSignBlock(
		final Player player,
		final Level world,
		final InteractionHand hand,
		final BlockHitResult hitResult
	) {
		// We only want to listen to the server side event
		if (world.isClientSide()) {
			return InteractionResult.PASS;
		}

		if (player.isCrouching()) {
			return InteractionResult.PASS;
		}

		if (!TextUtilities.getConfig().signEditingEnabled) {
			return InteractionResult.PASS;
		}

		var blockPos = hitResult.getBlockPos();
		var blockEntity = world.getBlockEntity(blockPos);

		if (!(blockEntity instanceof SignBlockEntity signBlock)) {
			return InteractionResult.PASS;
		}

		if (signBlock.isWaxed()) {
			player.displayClientMessage(Component.literal("Waxed signs cannot be edited."), true);
			return InteractionResult.PASS;
		}

		if (!isHoldingSign(player)) {
			return InteractionResult.PASS;
		}

		// Dyes, Ink Sacs, etc can be applied to signs directly when they are in the main hand
		if (isHoldingSignChangingItem(player, InteractionHand.MAIN_HAND)) {
			return InteractionResult.PASS;
		}

		var editorId = signBlock.getPlayerWhoMayEdit();
		var playerId = player.getUUID();

		if (editorId != null && editorId != playerId) {
			player.displayClientMessage(Component.literal("Sign is being edited by someone else."), true);
			return InteractionResult.FAIL;
		}

		// Set the editor to the player to allow them to edit the sign
		signBlock.setAllowedPlayerEditor(playerId);
		player.openTextEdit(signBlock, signBlock.isFacingFrontText(player));
		return InteractionResult.SUCCESS;
	}
}
