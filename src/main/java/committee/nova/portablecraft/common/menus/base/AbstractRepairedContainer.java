package committee.nova.portablecraft.common.menus.base;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/3/12 22:18
 * Version: 1.0
 */
public abstract class AbstractRepairedContainer extends ScreenHandler {
    protected final CraftingResultInventory output = new CraftingResultInventory();
    protected final PlayerEntity player;
    protected final Inventory input = new SimpleInventory(2) {

        @Override
        public void markDirty() {
            super.markDirty();
            onContentChanged(this);
        }
    };

    public AbstractRepairedContainer(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
        super(type, syncId);
        int i;
        this.player = playerInventory.player;
        this.addSlot(new Slot(this.input, 0, 27, 47));
        this.addSlot(new Slot(this.input, 1, 76, 47));
        this.addSlot(new Slot(this.output, 2, 134, 47) {

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return canTakeOutput(playerEntity, this.hasStack());
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                onTakeOutput(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    protected abstract boolean canTakeOutput(PlayerEntity var1, boolean var2);

    protected abstract void onTakeOutput(PlayerEntity var1, ItemStack var2);

    protected abstract boolean canUse(BlockState var1);

    public abstract void updateResult();

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            this.updateResult();
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.dropInventory(player, output);
        this.dropInventory(player, input);
        PlayerInventory Inventory = player.getInventory();
        if (!Inventory.getMainHandStack().isEmpty()) {
            player.dropItem(Inventory.getMainHandStack(), false);
            Inventory.addPickBlock(ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    protected boolean isUsableAsAddition(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index == 0 || index == 1) {
                if (!this.insertItem(itemStack2, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 3 && index < 39) {
                int i;
                int n = i = this.isUsableAsAddition(itemStack) ? 1 : 0;
                if (!this.insertItem(itemStack2, i, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }


}
