package io.chaws.textutilities.utils;

import io.chaws.textutilities.TextUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Stack;

public class FormattingUtils {
	public static String replaceConfiguredPrefixWithBuiltInPrefix(String value) {
		var config = TextUtilities.getConfig();
		var formattingCodePrefix = config.getFormattingCodePrefix();
		if (formattingCodePrefix == ChatFormatting.PREFIX_CODE) {
			return value;
		}

		return value.replace(
			formattingCodePrefix,
			ChatFormatting.PREFIX_CODE
		);
	}

	public static void replaceConfiguredPrefixWithBuiltInPrefix(String[] values) {
		var config = TextUtilities.getConfig();
		var formattingCodePrefix = config.getFormattingCodePrefix();
		if (formattingCodePrefix == ChatFormatting.PREFIX_CODE) {
			return;
		}

		for (var i = 0; i < values.length; i++) {
			values[i] = values[i].replace(
				config.getFormattingCodePrefix(),
				ChatFormatting.PREFIX_CODE
			);
		}
	}

	public static String replaceBuiltInPrefixWithConfiguredPrefix(String value) {
		var config = TextUtilities.getConfig();
		var formattingCodePrefix = config.getFormattingCodePrefix();
		if (formattingCodePrefix == ChatFormatting.PREFIX_CODE) {
			return value;
		}

		return value.replace(
			ChatFormatting.PREFIX_CODE,
			formattingCodePrefix
		);
	}

	public static void replaceBuiltInPrefixWithConfiguredPrefix(String[] values) {
		var config = TextUtilities.getConfig();
		var formattingCodePrefix = config.getFormattingCodePrefix();
		if (formattingCodePrefix == ChatFormatting.PREFIX_CODE) {
			return;
		}

		for (var i = 0; i < values.length; i++) {
			values[i] = values[i].replace(
				ChatFormatting.PREFIX_CODE,
				config.getFormattingCodePrefix()
			);
		}
	}

	public static Optional<ChatFormatting> getFormattingCode(@Nullable String string, int startIndex) {
		if (string == null) {
			return Optional.empty();
		}

		if (startIndex < 0) {
			return Optional.empty();
		}

		var length = string.length();
		var lastIndex = length - 1;
		var nextIndex = startIndex + 1;

		if (startIndex > lastIndex || nextIndex > lastIndex) {
			return Optional.empty();
		}

		var left = string.charAt(startIndex);
		if (left != ChatFormatting.PREFIX_CODE) {
			return Optional.empty();
		}

		var right = string.charAt(nextIndex);
		var formatting = ChatFormatting.getByCode(right);
		if (formatting == null) {
			return Optional.empty();
		}

		return Optional.of(formatting);
	}

	public static @NotNull Tuple<String, String> splitWithFormatting(String string, int index) {
		var previousIndex = index - 1;
		var formattingCode = getFormattingCode(string, previousIndex);
		if (formattingCode.isPresent()) {
			return new Tuple<>(
				string.substring(0, previousIndex),
				string.substring(previousIndex)
			);
		}

		return new Tuple<>(
			string.substring(0, index),
			string.substring(index)
		);
	}

	public static @NotNull String getLastFormattingCodes(@Nullable String string, int count) {
		if (string == null || string.isEmpty()) {
			return "";
		}

		if (count <= 0) {
			return "";
		}

		var formattingCodes = new Stack<ChatFormatting>();

		for (var rightIndex = string.length() - 1; rightIndex >= 0; rightIndex--) {

            var leftIndex = rightIndex - 1;
			if (leftIndex < 0) {
				break;
			}

			var leftChar = string.charAt(leftIndex);
			if (leftChar != ChatFormatting.PREFIX_CODE) {
				continue;
			}

			var rightChar = string.charAt(rightIndex);
			var formatting = ChatFormatting.getByCode(rightChar);
			if (formatting == null) {
				continue;
			}

			formattingCodes.push(formatting);
		}

		var sb = new StringBuilder();

		while (!formattingCodes.empty()) {
			var formattingCode = formattingCodes.pop();
			sb.append(formattingCode.toString());
		}

		var lastChar = string.charAt(string.length() - 1);
		if (lastChar == ChatFormatting.PREFIX_CODE) {
			sb.append(lastChar);
		}

		return sb.toString();
	}
}
