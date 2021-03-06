package org.crandor.game.content.skill.member.fletching.items.bolts;

import org.crandor.game.content.skill.SkillPulse;
import org.crandor.game.content.skill.Skills;
import org.crandor.game.content.skill.member.fletching.Fletching;
import org.crandor.game.node.entity.player.Player;
import org.crandor.game.node.item.Item;

/**
 * Represents the bolt pulse class to make bolts.
 * @author ceik
 */
public final class BoltPulse extends SkillPulse<Item> {

	/**
	 * Represents the feather item.
	 */
	private final Item feather = new Item(314);

	/**
	 * Represents the bolt.
	 */
	private final Fletching.Bolts bolt;

	/**
	 * Represents the sets to do.
	 */
	private int sets;

	/**
	 * Represents if we're using sets.
	 */
	private boolean useSets = false;

	/**
	 * Constructs a new {@code BoltPulse.java} {@code Object}.
	 * @param player the player.
	 * @param node the node.
	 */
	public BoltPulse(Player player, Item node, final Fletching.Bolts bolt, final int sets) {
		super(player, node);
		this.bolt = bolt;
		this.sets = sets;
	}

	@Override
	public boolean checkRequirements() {
		if (bolt.getUnfinished().getId() == 13279) {
			if (!player.getSlayer().getLearned()[0]) {
				player.getDialogueInterpreter().sendDialogue("You need to unlock the ability to create broad bolts.");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.FLETCHING) < bolt.level) {
			player.getDialogueInterpreter().sendDialogue("You need a fletching level of " + bolt.level + " in order to do this.");
			return false;
		}
		if (!player.getInventory().containsItem(feather)) {
			return false;
		}
		if (!player.getInventory().containsItem(bolt.getUnfinished())) {
			return false;
		}
		return true;
	}

	@Override
	public void animate() {
	}

	@Override
	public boolean reward() {
		int featherAmount = player.getInventory().getAmount(feather);
		int boltAmount = player.getInventory().getAmount(bolt.unfinished);
		if (getDelay() == 1) {
			super.setDelay(3);
		}
		final Item unfinished = bolt.getUnfinished();
		if (featherAmount >= 10 && boltAmount >= 10) {
			feather.setAmount(10);
			unfinished.setAmount(10);
			player.getPacketDispatch().sendMessage("You fletch 10 bolts.");
		} else {
			int amount = featherAmount > boltAmount ? boltAmount : featherAmount;
			feather.setAmount(amount);
			unfinished.setAmount(amount);
			player.getPacketDispatch().sendMessage(amount == 1 ? "You attach a feather to a bolt." : "You fletch " + amount + " bolts");
		}
		if (player.getInventory().remove(feather, unfinished)) {
			Item product = bolt.getFinished();
			product.setAmount(feather.getAmount());
			player.getSkills().addExperience(Skills.FLETCHING, product.getAmount() * bolt.experience, true);
			player.getInventory().add(product);
		}
		feather.setAmount(1);
		if (!player.getInventory().containsItem(feather)) {
			return true;
		}
		if (!player.getInventory().containsItem(bolt.getUnfinished())) {
			return true;
		}
		sets--;
		return sets <= 0;
	}

	@Override
	public void message(int type) {
	}

}
