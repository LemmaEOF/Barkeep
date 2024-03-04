package gay.lemmaeof.barkeep.hook;

import gay.lemmaeof.barkeep.util.PoseFunction;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface PoseOverride<T extends LivingEntity> {
	void setRightArmPose(@Nullable PoseFunction<T> pose);
	void setLeftArmPose(@Nullable PoseFunction<T> pose);
}
