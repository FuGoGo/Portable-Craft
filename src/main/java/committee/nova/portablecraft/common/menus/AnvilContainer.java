package committee.nova.portablecraft.common.menus;

import com.mojang.logging.LogUtils;
import committee.nova.portablecraft.common.menus.base.AbstractRepairedContainer;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/3/12 22:24
 * Version: 1.0
 */
public class AnvilContainer extends AbstractRepairedContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Property levelCost = Property.create();
    private int repairItemUsage;
    private String newItemName;


    public AnvilContainer(int syncId, PlayerInventory inventory) {
        super(ScreenHandlerType.ANVIL, syncId, inventory);
        this.addProperty(this.levelCost);
    }

    public static int getNextCost(int cost) {
        return cost * 2 + 1;
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isIn(BlockTags.ANVIL);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return (player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-this.levelCost.get());
        }
        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }
        this.levelCost.set(0);
    }

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        this.levelCost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemStack.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }
        ItemStack itemStack2 = itemStack.copy();
        ItemStack itemStack3 = this.input.getStack(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
        j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
        this.repairItemUsage = 0;
        if (!itemStack3.isEmpty()) {
            boolean bl;
            boolean bl2 = bl = itemStack3.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(itemStack3).isEmpty();
            if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
                int m;
                int l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                if (l <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                for (m = 0; l > 0 && m < itemStack3.getCount(); ++m) {
                    int n = itemStack2.getDamage() - l;
                    itemStack2.setDamage(n);
                    ++i;
                    l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
                }
                this.repairItemUsage = m;
            } else {
                if (!(bl || itemStack2.isOf(itemStack3.getItem()) && itemStack2.isDamageable())) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                if (itemStack2.isDamageable() && !bl) {
                    int l = itemStack.getMaxDamage() - itemStack.getDamage();
                    int m = itemStack3.getMaxDamage() - itemStack3.getDamage();
                    int n = m + itemStack2.getMaxDamage() * 12 / 100;
                    int o = l + n;
                    int p = itemStack2.getMaxDamage() - o;
                    if (p < 0) {
                        p = 0;
                    }
                    if (p < itemStack2.getDamage()) {
                        itemStack2.setDamage(p);
                        i += 2;
                    }
                }
                Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
                boolean bl22 = false;
                boolean bl3 = false;
                for (Enchantment enchantment : map2.keySet()) {
                    int r;
                    if (enchantment == null) continue;
                    int q = map.getOrDefault(enchantment, 0);
                    r = q == (r = map2.get(enchantment).intValue()) ? r + 1 : Math.max(r, q);
                    boolean bl4 = enchantment.isAcceptableItem(itemStack);
                    if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                        bl4 = true;
                    }
                    for (Enchantment enchantment2 : map.keySet()) {
                        if (enchantment2 == enchantment || enchantment.canCombine(enchantment2)) continue;
                        bl4 = false;
                        ++i;
                    }
                    if (!bl4) {
                        bl3 = true;
                        continue;
                    }
                    bl22 = true;
                    if (r > enchantment.getMaxLevel()) {
                        r = enchantment.getMaxLevel();
                    }
                    map.put(enchantment, r);
                    int s = 0;
                    switch (enchantment.getRarity()) {
                        case COMMON: {
                            s = 1;
                            break;
                        }
                        case UNCOMMON: {
                            s = 2;
                            break;
                        }
                        case RARE: {
                            s = 4;
                            break;
                        }
                        case VERY_RARE: {
                            s = 8;
                        }
                    }
                    if (bl) {
                        s = Math.max(1, s / 2);
                    }
                    i += s * r;
                    if (itemStack.getCount() <= 1) continue;
                    i = 40;
                }
                if (bl3 && !bl22) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank(this.newItemName)) {
            if (itemStack.hasCustomName()) {
                k = 1;
                i += k;
                itemStack2.removeCustomName();
            }
        } else if (!this.newItemName.equals(itemStack.getName().getString())) {
            k = 1;
            i += k;
            itemStack2.setCustomName(new LiteralText(this.newItemName));
        }
        this.levelCost.set(j + i);
        if (i <= 0) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (k == i && k > 0 && this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
        if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (!itemStack2.isEmpty()) {
            int t = itemStack2.getRepairCost();
            if (!itemStack3.isEmpty() && t < itemStack3.getRepairCost()) {
                t = itemStack3.getRepairCost();
            }
            if (k != i || k == 0) {
                t = AnvilScreenHandler.getNextCost(t);
            }
            itemStack2.setRepairCost(t);
            EnchantmentHelper.set(map, itemStack2);
        }
        this.output.setStack(0, itemStack2);
        this.sendContentUpdates();
    }

    public void setNewItemName(String newItemName) {
        this.newItemName = newItemName;
        if (this.getSlot(2).hasStack()) {
            ItemStack itemStack = this.getSlot(2).getStack();
            if (StringUtils.isBlank(newItemName)) {
                itemStack.removeCustomName();
            } else {
                itemStack.setCustomName(new LiteralText(this.newItemName));
            }
        }
        this.updateResult();
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }
}
