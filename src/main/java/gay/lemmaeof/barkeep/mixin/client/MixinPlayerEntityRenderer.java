package gay.lemmaeof.barkeep.mixin.client;

import gay.lemmaeof.barkeep.hook.PoseOverride;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.util.PoseFunction;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	//TODO: any more fine-tuning?
	private static final PoseFunction<AbstractClientPlayerEntity> POSE_FUNC = (rightArm, leftArm, entity, arm) -> {
		float progress = entity.getItemUseTime();
		ModelPart upperArm = arm == Arm.RIGHT? leftArm : rightArm;
		ModelPart lowerArm = arm == Arm.RIGHT? rightArm : leftArm;
		upperArm.yaw = (float) ((Math.sin(progress) * 0.3f) + 0.7F) * (arm == Arm.RIGHT? 1 : -1);
		upperArm.pitch = (float) (Math.sin(progress) * 0.3f) - 1.5f;
		lowerArm.yaw = arm == Arm.RIGHT? -0.8F : 0.8F;
		lowerArm.pitch = (float) (Math.sin(progress) * 0.3f) - 0.65f;
	};

	public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "setModelPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", ordinal = 0))
	private void addShakerOverride(AbstractClientPlayerEntity player, CallbackInfo info) {
		PoseOverride<AbstractClientPlayerEntity> override = ((PoseOverride<AbstractClientPlayerEntity>) this.getModel());
		PoseFunction<AbstractClientPlayerEntity> mainPoseFunc = getPoseFunc(player, Hand.MAIN_HAND);
		PoseFunction<AbstractClientPlayerEntity> offPoseFunc = getPoseFunc(player, Hand.OFF_HAND);
		if (player.getMainArm() == Arm.RIGHT) {
			override.setRightArmPose(mainPoseFunc);
			override.setLeftArmPose(offPoseFunc);
		} else {
			override.setRightArmPose(offPoseFunc);
			override.setLeftArmPose(mainPoseFunc);
		}
	}

	@Nullable
	private PoseFunction<AbstractClientPlayerEntity> getPoseFunc(AbstractClientPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(BarkeepItems.SHAKER) && player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
			return POSE_FUNC;
		}
		return null;
	}
}
