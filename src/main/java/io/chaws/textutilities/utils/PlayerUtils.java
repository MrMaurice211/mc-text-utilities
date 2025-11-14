package io.chaws.textutilities.utils;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import java.util.function.Predicate;

public class PlayerUtils {

	public static boolean isHolding(Player player, InteractionHand hand, Item item) {
		return isHolding(player, hand, holding -> holding.is(item));
	}

	public static boolean isHolding(Player player, InteractionHand hand, Predicate<ItemStack> predicate) {
		var holding = hand == InteractionHand.MAIN_HAND ? player.getMainHandItem() : player.getOffhandItem();
		return predicate.test(holding);
	}

	public static boolean isHoldingSign(Player player) {
		return player.isHolding(x -> x.getItem() instanceof SignItem);
	}

	public static boolean isHoldingSign(Player player, InteractionHand hand) {
		return isHolding(player, hand, x -> x.getItem() instanceof SignItem);
	}

	public static boolean isHoldingDye(Player player, InteractionHand hand) {
		return isHolding(player, hand, x -> x.getItem() instanceof DyeItem);
	}

	public static boolean isHoldingSignChangingItem(Player player, InteractionHand hand) {
		return isHolding(player, hand, x -> x.getItem() instanceof SignApplicator);
	}

}
