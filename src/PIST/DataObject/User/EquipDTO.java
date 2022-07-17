package PIST.DataObject.User;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EquipDTO {
	ItemStack ring_1 = null;
	ItemStack ring_2 = null;
	ItemStack pendant_1 = null;
	ItemStack pendant_2 = null;
	ItemStack earring_1 = null;
	ItemStack earring_2 = null;
	ItemStack riding = null;
	ItemStack baseWeapon = null;
	ItemStack artifact_1 = null;
	ItemStack artifact_2 = null;
	ItemStack creature = null;
	ItemStack rune = null;

	public ItemStack getRing_1() {
		return ring_1;
	}

	public void setRing_1(ItemStack ring_1) {
		this.ring_1 = ring_1;
	}

	public ItemStack getRing_2() {
		return ring_2;
	}

	public void setRing_2(ItemStack ring_2) {
		this.ring_2 = ring_2;
	}

	public ItemStack getPendant_1() {
		return pendant_1;
	}

	public void setPendant_1(ItemStack pendant_1) {
		this.pendant_1 = pendant_1;
	}

	public ItemStack getPendant_2() {
		return pendant_2;
	}

	public void setPendant_2(ItemStack pendant_2) {
		this.pendant_2 = pendant_2;
	}

	public ItemStack getEarring_1() {
		return earring_1;
	}

	public void setEarring_1(ItemStack earring_1) {
		this.earring_1 = earring_1;
	}

	public ItemStack getEarring_2() {
		return earring_2;
	}

	public void setEarring_2(ItemStack earring_2) {
		this.earring_2 = earring_2;
	}

	public ItemStack getRiding() {
		return riding;
	}

	public void setRiding(ItemStack riding) {
		this.riding = riding;
	}

	public ItemStack getBaseWeapon() {
		return baseWeapon;
	}

	public void setBaseWeapon(ItemStack baseWeapon) {
		this.baseWeapon = baseWeapon;
	}

	public ItemStack getArtifact_1() {
		return artifact_1;
	}

	public void setArtifact_1(ItemStack artifact_1) {
		this.artifact_1 = artifact_1;
	}

	public ItemStack getArtifact_2() {
		return artifact_2;
	}

	public void setArtifact_2(ItemStack artifact_2) {
		this.artifact_2 = artifact_2;
	}

	public ItemStack getCreature() {
		return creature;
	}

	public void setCreature(ItemStack creature) {
		this.creature = creature;
	}

	public ItemStack getRune() {
		return rune;
	}

	public void setRune(ItemStack rune) {
		this.rune = rune;
	}
	public EquipDTO(ItemStack ring_1, ItemStack ring_2, ItemStack pendant_1, ItemStack pendant_2, ItemStack earring_1,
			ItemStack earring_2, ItemStack riding, ItemStack baseWeapon, ItemStack artifact_1, ItemStack artifact_2,
			ItemStack creature, ItemStack rune) {
		super();
		this.ring_1 = ring_1;
		this.ring_2 = ring_2;
		this.pendant_1 = pendant_1;
		this.pendant_2 = pendant_2;
		this.earring_1 = earring_1;
		this.earring_2 = earring_2;
		this.riding = riding;
		this.baseWeapon = baseWeapon;
		this.artifact_1 = artifact_1;
		this.artifact_2 = artifact_2;
		this.creature = creature;
		this.rune = rune;
	}
	public EquipDTO() {

	}
}
