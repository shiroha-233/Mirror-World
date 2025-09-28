package com.shiroha.mirrorworld.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;

public class MirrorWorldModClient implements ClientModInitializer {
    
    private static boolean gravityWarningShown = false;
    
    @Override
    public void onInitializeClient() {
        MirrorWorldMod.LOGGER.info("Initializing Mirror World Client...");
        
        // 注册客户端tick事件，用于处理重力效果
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        
        // 注册世界渲染事件，用于添加视觉效果
        WorldRenderEvents.START.register(context -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                renderMirrorWorldEffects(player);
            }
        });
        
        MirrorWorldMod.LOGGER.info("Mirror World Client initialized!");
    }
    
    /**
     * 客户端tick事件处理
     */
    private void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        
        // 检查玩家是否在镜像世界
        boolean isInMirrorWorld = MirrorWorldUtils.isInMirrorWorld(player);
        
        // 显示重力警告
        if (isInMirrorWorld && !gravityWarningShown) {
            player.sendMessage(Text.literal("警告：您已进入镜像世界，重力已翻转！").formatted(Formatting.RED, Formatting.BOLD), true);
            gravityWarningShown = true;
        } else if (!isInMirrorWorld && gravityWarningShown) {
            player.sendMessage(Text.literal("您已返回正常世界，重力恢复正常。").formatted(Formatting.GREEN), true);
            gravityWarningShown = false;
        }
        
        // 应用客户端重力效果
        if (isInMirrorWorld) {
            applyMirrorWorldGravity(player);
        }
    }
    
    /**
     * 应用镜像世界重力效果
     */
    private void applyMirrorWorldGravity(ClientPlayerEntity player) {
        // 这里可以添加粒子效果、声音等客户端反馈
        // 实际的重力修改将在服务器端的Mixin中处理
    }
    
    /**
     * 渲染镜像世界视觉效果
     */
    private void renderMirrorWorldEffects(ClientPlayerEntity player) {
        // 添加镜像世界的视觉效果，如天空颜色变化、粒子效果等
        if (MirrorWorldUtils.isInMirrorWorld(player)) {
            // 可以在这里添加特殊的渲染效果
        }
    }
}