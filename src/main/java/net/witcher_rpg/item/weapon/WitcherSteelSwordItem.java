package net.witcher_rpg.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.more_rpg_classes.effect.MRPGCEffects;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.witcher_rpg.effect.Effects;

import java.util.UUID;

public class WitcherSteelSwordItem extends SpellSwordItem {
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    public WitcherSteelSwordItem(ToolMaterial material, float attackDamage, float attackSpeed, float sign,
                                 float adrenaline, Item.Settings settings) {
        super(material, settings);
        this.attackDamage = (float) attackDamage + material.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID,
                "Weapon modifier", (double) this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID,
                "Weapon modifier", (double) attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        builder.put(MRPGCEntityAttributes.SIGN_INTENSITY, new EntityAttributeModifier(UUID.fromString("d404c4f7-457c-4556-97c3-99ccaf03a7e9"),
                "Weapon modifier", (double) sign, EntityAttributeModifier.Operation.ADDITION));
        builder.put(MRPGCEntityAttributes.ADRENALINE_MODIFIER, new EntityAttributeModifier(UUID.fromString("3c226f50-c0d1-45ec-8495-d4b5aa26f6a5"),
                "Weapon modifier", (double) adrenaline, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = builder.build();
    }
    //10% chance to bleed non undead
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(!target.isUndead()){
            int bleed_duration = 200;
            int bleed_chance = 10;
            int randomrange_bleed = (int) ((Math.random() * (1 + bleed_chance)) + 1);

            if (randomrange_bleed >= bleed_chance ) {
                target.addStatusEffect(new StatusEffectInstance(MRPGCEffects.BLEEDING,bleed_duration,0,false,false,true));
            }
        }
        stack.damage(1, attacker, (e)->{
            e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
        });
        return true;
    }
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
