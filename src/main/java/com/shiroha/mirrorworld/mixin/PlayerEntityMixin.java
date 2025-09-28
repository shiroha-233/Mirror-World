package com.shiroha.mirrorworld.mixin;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家实体Mixin，用于修改重力和物理行为
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    
    /**
     * 修改玩家重力
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // 检查是否应该应用重力效果
        if (MirrorWorldUtils.shouldApplyGravity(player)) {
            // 检查是否在镜像世界
            if (MirrorWorldUtils.isInMirrorWorld(player)) {
                applyMirrorWorldGravity(player);
            }
        }
    }
    
    /**
     * 修改玩家移动时的物理行为
     */
    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // 在镜像世界中应用反向重力
        if (MirrorWorldUtils.shouldApplyGravity(player) && MirrorWorldUtils.isInMirrorWorld(player)) {
            modifyMovementForMirrorWorld(player, movementInput);
        }
    }
    
    /**
     * 应用镜像世界重力
     */
    private void applyMirrorWorldGravity(PlayerEntity player) {
        // 在镜像世界中，重力向上（向Y=640方向）
        Vec3d currentVelocity = player.getVelocity();
        
        // 计算新的速度，向上加速
        double gravityStrength = MirrorWorldUtils.getGravityStrength(); // 从配置中获取重力强度
        Vec3d newVelocity = new Vec3d(
            currentVelocity.x,
            currentVelocity.y + gravityStrength, // 向上加速
            currentVelocity.z
        );
        
        // 限制最大速度
        double maxVelocity = 0.5;
        if (newVelocity.y > maxVelocity) {
            newVelocity = new Vec3d(newVelocity.x, maxVelocity, newVelocity.z);
        }
        
        player.setVelocity(newVelocity);
        
        // 调试模式输出
        if (MirrorWorldUtils.isDebugModeEnabled()) {
            MirrorWorldMod.LOGGER.debug("Applied mirror gravity to player at Y={}, velocity={}",
                player.getY(), newVelocity);
        }
    }
    
    /**
     * 为镜像世界修改移动行为
     */
    private void modifyMovementForMirrorWorld(PlayerEntity player, Vec3d movementInput) {
        // 在过渡区域添加平滑效果
        if (MirrorWorldUtils.isInTransitionZone(player)) {
            float transitionStrength = MirrorWorldUtils.getTransitionStrength(player);
            
            // 可以在这里添加过渡效果，如：
            // - 视觉效果
            // - 声音效果
            // - 粒子效果
            
            if (MirrorWorldUtils.isDebugModeEnabled()) {
                MirrorWorldMod.LOGGER.debug("Player in transition zone, strength={}", transitionStrength);
            }
        }
    }
    
    /**
     * 防止玩家在镜像世界中掉出世界
     */
    @Inject(method = "tickMovement", at = @At("RETURN"))
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // 检查玩家是否接近镜像世界的顶部边界
        if (player.getY() >= MirrorWorldMod.MIRROR_WORLD_HEIGHT - 5) {
            // 防止玩家掉出世界，将其推回
            Vec3d velocity = player.getVelocity();
            if (velocity.y > 0) { // 如果玩家还在向上移动
                player.setVelocity(velocity.x, -0.1, velocity.z); // 轻微向下推
            }
        }
        
        // 检查玩家是否在正常世界底部
        if (player.getY() <= 0) {
            // 防止玩家掉出虚空
            Vec3d velocity = player.getVelocity();
            if (velocity.y < 0) {
                player.setVelocity(velocity.x, 0.1, velocity.z); // 轻微向上推
            }
        }
    }
}