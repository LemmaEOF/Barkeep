package gay.lemmaeof.barkeep.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@FunctionalInterface
public interface PoseFunction<T extends LivingEntity> {
	void poseArm(ModelPart rightArm, ModelPart leftArm, T entity, Arm arm);
}
