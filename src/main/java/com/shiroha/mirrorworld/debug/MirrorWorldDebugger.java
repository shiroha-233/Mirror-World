package com.shiroha.mirrorworld.debug;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.config.MirrorWorldConfig;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 镜像世界调试器，提供测试和调试功能
 */
public class MirrorWorldDebugger {
    
    private static final Map<UUID, Long> lastDebugMessage = new HashMap<>();
    private static final long DEBUG_MESSAGE_COOLDOWN = 5000; // 5秒冷却时间
    
    /**
     * 向玩家发送调试信息
     */
    public static void sendDebugInfo(PlayerEntity player, String message) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();
        
        // 检查冷却时间
        if (lastDebugMessage.containsKey(playerId)) {
            long lastTime = lastDebugMessage.get(playerId);
            if (currentTime - lastTime < DEBUG_MESSAGE_COOLDOWN) {
                return;
            }
        }
        
        // 发送调试信息
        player.sendMessage(
            Text.literal("[Mirror World Debug] " + message)
                .formatted(Formatting.YELLOW, Formatting.ITALIC),
            false
        );
        
        // 更新最后发送时间
        lastDebugMessage.put(playerId, currentTime);
    }
    
    /**
     * 输出玩家的世界状态信息
     */
    public static void logPlayerWorldStatus(PlayerEntity player) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        String worldType = MirrorWorldUtils.isInMirrorWorld(player) ? "Mirror World" : "Normal World";
        double y = player.getY();
        boolean inTransition = MirrorWorldUtils.isInTransitionZone(player);
        float transitionStrength = MirrorWorldUtils.getTransitionStrength(player);
        
        String status = String.format(
            "Player Status - World: %s, Y: %.2f, In Transition: %s, Transition Strength: %.2f",
            worldType, y, inTransition, transitionStrength
        );
        
        MirrorWorldMod.LOGGER.info(status);
        sendDebugInfo(player, status);
    }
    
    /**
     * 测试重力效果
     */
    public static void testGravity(PlayerEntity player) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        boolean gravityEnabled = MirrorWorldConfig.isGravityEnabled();
        double gravityStrength = MirrorWorldConfig.getGravityStrength();
        boolean inMirrorWorld = MirrorWorldUtils.isInMirrorWorld(player);
        
        String testResult = String.format(
            "Gravity Test - Enabled: %s, Strength: %.2f, In Mirror World: %s",
            gravityEnabled, gravityStrength, inMirrorWorld
        );
        
        MirrorWorldMod.LOGGER.info(testResult);
        sendDebugInfo(player, testResult);
    }
    
    /**
     * 测试坐标转换
     */
    public static void testCoordinateConversion(PlayerEntity player) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        net.minecraft.util.math.BlockPos playerPos = player.getBlockPos();
        net.minecraft.util.math.BlockPos mirrorPos = MirrorWorldUtils.toMirrorWorldCoords(playerPos);
        net.minecraft.util.math.BlockPos convertedBack = MirrorWorldUtils.toNormalWorldCoords(mirrorPos);
        
        String conversionResult = String.format(
            "Coordinate Conversion Test - Original: %s, Mirror: %s, Converted Back: %s",
            playerPos, mirrorPos, convertedBack
        );
        
        MirrorWorldMod.LOGGER.info(conversionResult);
        sendDebugInfo(player, conversionResult);
    }
    
    /**
     * 测试配置系统
     */
    public static void testConfiguration(PlayerEntity player) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        String configInfo = String.format(
            "Configuration Test - Enabled: %s, Transition Y: %d, World Height: %d, Gravity Enabled: %s",
            MirrorWorldConfig.isEnabled(),
            MirrorWorldConfig.getMirrorTransitionY(),
            MirrorWorldConfig.getMirrorWorldHeight(),
            MirrorWorldConfig.isGravityEnabled()
        );
        
        MirrorWorldMod.LOGGER.info(configInfo);
        sendDebugInfo(player, configInfo);
    }
    
    /**
     * 运行所有测试
     */
    public static void runAllTests(PlayerEntity player) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            player.sendMessage(
                Text.literal("Debug mode is disabled. Enable it in the configuration file.")
                    .formatted(Formatting.RED),
                false
            );
            return;
        }
        
        sendDebugInfo(player, "Running all Mirror World tests...");
        
        logPlayerWorldStatus(player);
        testGravity(player);
        testCoordinateConversion(player);
        testConfiguration(player);
        
        sendDebugInfo(player, "All tests completed.");
    }
    
    /**
     * 清理调试器数据
     */
    public static void cleanup() {
        lastDebugMessage.clear();
        MirrorWorldMod.LOGGER.info("Mirror World Debugger cleaned up.");
    }
    
    /**
     * 获取调试器状态
     */
    public static String getDebuggerStatus() {
        return String.format(
            "Debugger Status - Debug Mode: %s, Active Players: %d",
            MirrorWorldUtils.isDebugModeEnabled(),
            lastDebugMessage.size()
        );
    }
    
    /**
     * 向所有在线玩家广播调试信息
     */
    public static void broadcastDebugInfo(MinecraftServer server, String message) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendDebugInfo(player, "[Broadcast] " + message);
        }
        
        MirrorWorldMod.LOGGER.info("[Broadcast Debug] " + message);
    }
    
    /**
     * 测试世界生成
     */
    public static void testWorldGeneration(ServerWorld world) {
        if (!MirrorWorldUtils.isDebugModeEnabled()) {
            return;
        }
        
        String generationInfo = String.format(
            "World Generation Test - World: %s, Height: %d, Transition Y: %d",
            world.getRegistryKey().getValue(),
            MirrorWorldConfig.getMirrorWorldHeight(),
            MirrorWorldConfig.getMirrorTransitionY()
        );
        
        MirrorWorldMod.LOGGER.info(generationInfo);
        broadcastDebugInfo(world.getServer(), generationInfo);
    }
}