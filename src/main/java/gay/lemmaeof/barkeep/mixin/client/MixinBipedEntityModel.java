package gay.lemmaeof.barkeep.mixin.client;

import gay.lemmaeof.barkeep.hook.PoseOverride;
import gay.lemmaeof.barkeep.util.PoseFunction;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements PoseOverride<T> {
	@Shadow @Final public ModelPart rightArm;
	@Shadow @Final public ModelPart leftArm;
	private PoseFunction<T> rightArmPose;
	private PoseFunction<T> leftArmPose;

	@Override
	public void setRightArmPose(@Nullable PoseFunction<T> pose) {
		this.rightArmPose = pose;
	}

	@Override
	public void setLeftArmPose(@Nullable PoseFunction<T> pose) {
		this.leftArmPose = pose;
	}

	@Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
	private void overrideRightArmPose(T entity, CallbackInfo info) {
		if (rightArmPose != null) {
			rightArmPose.poseArm(rightArm, leftArm, entity, Arm.RIGHT);
			info.cancel();
		}
	}

	@Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
	private void overrideLeftArmPose(T entity, CallbackInfo info) {
		if (leftArmPose != null) {
			leftArmPose.poseArm(rightArm, leftArm, entity, Arm.LEFT);
			info.cancel();
		}
	}

}
