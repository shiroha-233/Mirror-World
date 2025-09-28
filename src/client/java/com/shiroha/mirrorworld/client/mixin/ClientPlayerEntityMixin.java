package com.shiroha.mirrorworld.client.mixin;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 客户端玩家实体Mixin，用于处理客户端特定的镜像世界效果
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    
    private static boolean gravityWarningShown = false;
    private static float lastTransitionStrength = 0.0f;
    
    /**
     * 客户端tick处理，用于显示警告和效果
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        
        // 检查是否在镜像世界
        boolean isInMirrorWorld = MirrorWorldUtils.isInMirrorWorld(player);
        
        // 显示重力警告
        if (isInMirrorWorld && !gravityWarningShown) {
            player.sendMessage(
                net.minecraft.text.Text.literal("警告：您已进入镜像世界，重力已翻转！")
                    .formatted(net.minecraft.text.Formatting.RED, net.minecraft.text.Formatting.BOLD),
                true
            );
            gravityWarningShown = true;
        } else if (!isInMirrorWorld && gravityWarningShown) {
            player.sendMessage(
                net.minecraft.text.Text.literal("您已返回正常世界，重力恢复正常。")
                    .formatted(net.minecraft.text.Formatting.GREEN),
                true
            );
            gravityWarningShown = false;
        }
        
        // 处理过渡效果
        float currentTransitionStrength = MirrorWorldUtils.getTransitionStrength(player);
        if (Math.abs(currentTransitionStrength - lastTransitionStrength) > 0.01f) {
            applyTransitionEffects(player, currentTransitionStrength);
            lastTransitionStrength = currentTransitionStrength;
        }
        
        // 应用客户端重力效果
        if (isInMirrorWorld) {
            applyClientGravityEffects(player);
        }
    }
    
    /**
     * 应用过渡效果
     */
    private void applyTransitionEffects(ClientPlayerEntity player, float transitionStrength) {
        // 在这里添加过渡效果，如：
        // - 屏幕颜色变化
        // - 粒子效果
        // - 声音效果
        // - 视觉扭曲
        
        // 例如：根据过渡强度调整屏幕颜色
        if (transitionStrength > 0.0f && transitionStrength < 1.0f) {
            // 可以在这里添加屏幕颜色混合效果
            // 这需要访问Minecraft的渲染系统
        }
    }
    
    /**
     * 应用客户端重力效果
     */
    private void applyClientGravityEffects(ClientPlayerEntity player) {
        // 在这里添加客户端特定的重力效果
        // 例如：
        // - 视觉效果
        // - 粒子效果
        // - 声音效果
        
        // 添加向上飘浮的粒子效果来表示反向重力
        if (player.getWorld().random.nextFloat() < 0.1f) {
            spawnGravityParticles(player);
        }
    }
    
    /**
     * 生成重力粒子效果
     */
    private void spawnGravityParticles(ClientPlayerEntity player) {
        // 在玩家周围生成向上飘浮的粒子
        Vec3d pos = player.getPos();
        double offsetX = (player.getWorld().random.nextDouble() - 0.5) * 2.0;
        double offsetZ = (player.getWorld().random.nextDouble() - 0.5) * 2.0;
        
        player.getWorld().addParticle(
            net.minecraft.particle.ParticleTypes.END_ROD,
            pos.x + offsetX,
            pos.y + player.getHeight() / 2.0,
            pos.z + offsetZ,
            0.0,
            0.5, // 向上的速度
            0.0
        );
    }
    
    /**
     * 修改相机视角以适应镜像世界
     */
    @Inject(method = "updateCameraAngles", at = @At("HEAD"))
    private void onUpdateCameraAngles(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        
        if (MirrorWorldUtils.isInMirrorWorld(player)) {
            // 可以在这里添加相机视角调整
            // 例如：轻微的视角翻转或调整
        }
    }
    
    /**
     * 修改玩家在镜像世界中的声音效果
     */
    @Inject(method = "playSound", at = @At("HEAD"))
    private void onPlaySound(
        net.minecraft.sound.SoundEvent sound,
        net.minecraft.sound.SoundCategory category,
        float volume,
        float pitch,
        CallbackInfo ci
    ) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        
        if (MirrorWorldUtils.isInMirrorWorld(player)) {
            // 可以在这里修改声音效果
            // 例如：改变音调或添加回声效果
        }
    }
}