package com.lennyverse.rcblock;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.resources.Identifier;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class FishingRcblock implements ModInitializer {
	public static final String MOD_ID = "fishing-rcblock";

	private  List<String> messages = new ArrayList<>();
	private boolean canUseItem = true;

	public FishingRcblock() {
		messages.add("You caught a Carrot King!");
		messages.add("You caught a Water Hydra!");
		messages.add("Sending to server");
	}


	@Override
	public void onInitialize() {

		ClientReceiveMessageEvents.ALLOW_GAME.register((message, a) -> {
			// Get the plain text of the message
			String rawMessage = message.getString();

			LocalDateTime timeDelay = LocalDateTime.now().plusSeconds(3);
			Date dateDelay = Date.from(timeDelay.atZone(ZoneId.systemDefault()).toInstant());

			if(isMessageInList(rawMessage)) {
				System.out.println("ITEM USAGE BLOCKED FOR 3 SECONDS");
				canUseItem = false;
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run(){
						canUseItem = true;
						System.out.println("CAN USE ITEM AGAIN");
					}
				}, dateDelay);
			}

			return true;
		});

		UseItemCallback.EVENT.register((level, player, hand) -> {

			if(canUseItem){
				return InteractionResult.PASS;
			}

			return InteractionResult.FAIL;
		});

		UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {

			if(canUseItem){
				return InteractionResult.PASS;
			}

			return InteractionResult.FAIL;
		});

	}

	private boolean isMessageInList(String message) {
		for (String entry : messages) {
			if (message.contains(entry)) {
				return true;
			}
		}
		return false;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
