package gay.lemmaeof.barkeep.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class DrinkBottleBlockEntity extends ComponentSavingBlockEntity {
	public DrinkBottleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
