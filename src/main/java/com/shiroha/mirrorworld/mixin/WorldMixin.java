package com.shiroha.mirrorworld.mixin;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 世界Mixin，用于修改世界高度和边界行为
 */
@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {
    
    /**
     * 修改世界高度，返回扩展后的高度
     */
    @Inject(method = "getTopY", at = @At("HEAD"), cancellable = true)
    private void onGetTopY(CallbackInfoReturnable<Integer> cir) {
        // 返回镜像世界的最大高度
        cir.setReturnValue(MirrorWorldMod.MIRROR_WORLD_HEIGHT);
    }
    
    
    /**
     * 处理镜像世界边界
     */
    private void handleMirrorWorldBoundary(Entity entity) {
        double y = entity.getY();
        
        // 如果实体接近镜像世界的顶部
        if (y >= MirrorWorldMod.MIRROR_WORLD_HEIGHT - 2) {
            // 防止实体超出边界
            Vec3d velocity = entity.getVelocity();
            if (velocity.y > 0) {
                entity.setVelocity(velocity.x, -0.1, velocity.z);
            }
        }
        
        // 如果实体在过渡区域
        if (MirrorWorldUtils.isInTransitionZone(entity)) {
            // 可以在这里添加过渡效果
            applyTransitionEffects(entity);
        }
    }
    
    /**
     * 应用过渡效果
     */
    private void applyTransitionEffects(Entity entity) {
        // 可以在这里添加：
        // - 粒子效果
        // - 声音效果
        // - 视觉效果
        // - 临时状态效果
    }
}