package com.shiroha.mirrorworld.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.config.MirrorWorldConfig;

/**
 * 镜像世界工具类，提供坐标转换和世界检测功能
 */
public class MirrorWorldUtils {
    
    /**
     * 检查实体是否为玩家
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }
    
    /**
     * 检查玩家是否在镜像世界
     */
    public static boolean isInMirrorWorld(Entity entity) {
        return entity.getY() > MirrorWorldMod.MIRROR_TRANSITION_Y;
    }
    
    /**
     * 检查玩家是否在过渡区域
     */
    public static boolean isInTransitionZone(Entity entity) {
        double y = entity.getY();
        return y >= MirrorWorldMod.MIRROR_TRANSITION_Y - 10 && y <= MirrorWorldMod.MIRROR_TRANSITION_Y + 10;
    }
    
    /**
     * 将正常世界坐标转换为镜像世界坐标
     */
    public static BlockPos toMirrorWorldCoords(BlockPos pos) {
        int mirrorY = MirrorWorldMod.MIRROR_WORLD_HEIGHT - 1 - pos.getY();
        return new BlockPos(pos.getX(), mirrorY, pos.getZ());
    }
    
    /**
     * 将镜像世界坐标转换为正常世界坐标
     */
    public static BlockPos toNormalWorldCoords(BlockPos pos) {
        int normalY = MirrorWorldMod.MIRROR_WORLD_HEIGHT - 1 - pos.getY();
        return new BlockPos(pos.getX(), normalY, pos.getZ());
    }
    
    /**
     * 检查坐标是否在镜像世界范围内
     */
    public static boolean isMirrorWorldY(int y) {
        return y > MirrorWorldMod.MIRROR_TRANSITION_Y && y < MirrorWorldMod.MIRROR_WORLD_HEIGHT;
    }
    
    /**
     * 检查坐标是否在正常世界范围内
     */
    public static boolean isNormalWorldY(int y) {
        return y >= 0 && y <= MirrorWorldMod.MIRROR_TRANSITION_Y;
    }
    
    /**
     * 获取镜像世界的重力方向
     */
    public static Vec3d getMirrorWorldGravityDirection() {
        return new Vec3d(0, 1, 0); // 向上的重力（在镜像世界中）
    }
    
    /**
     * 获取正常世界的重力方向
     */
    public static Vec3d getNormalWorldGravityDirection() {
        return new Vec3d(0, -1, 0); // 向下的重力（在正常世界中）
    }
    
    /**
     * 根据玩家位置获取当前重力方向
     */
    public static Vec3d getCurrentGravityDirection(Entity entity) {
        if (isInMirrorWorld(entity)) {
            return getMirrorWorldGravityDirection();
        } else {
            return getNormalWorldGravityDirection();
        }
    }
    
    /**
     * 设置玩家重力
     */
    public static void setupPlayerGravity(Entity entity) {
        if (!isPlayer(entity)) return;
        
        // 重力设置将在Mixin中处理
        // 这里可以添加额外的初始化逻辑
    }
    
    /**
     * 计算镜像世界的距离
     */
    public static double getDistanceToMirrorWorld(Entity entity) {
        return Math.max(0, entity.getY() - MirrorWorldMod.MIRROR_TRANSITION_Y);
    }
    
    /**
     * 计算正常世界的距离
     */
    public static double getDistanceToNormalWorld(Entity entity) {
        return Math.max(0, MirrorWorldMod.MIRROR_TRANSITION_Y - entity.getY());
    }
    
    /**
     * 检查区块是否需要生成镜像部分
     */
    public static boolean needsMirrorGeneration(WorldChunk chunk) {
        // 检查模组是否启用
        if (!MirrorWorldConfig.isEnabled()) {
            return false;
        }
        
        // 检查区块在正常世界范围内的方块是否需要镜像生成
        // 只有当区块在正常世界范围内时才需要生成镜像
        ChunkPos chunkPos = chunk.getPos();
        int chunkStartX = chunkPos.getStartX();
        int chunkStartZ = chunkPos.getStartZ();
        
        // 检查区块的四个角是否在正常世界范围内
        BlockPos[] corners = {
            new BlockPos(chunkStartX, 0, chunkStartZ),
            new BlockPos(chunkStartX + 15, 0, chunkStartZ),
            new BlockPos(chunkStartX, 0, chunkStartZ + 15),
            new BlockPos(chunkStartX + 15, 0, chunkStartZ + 15)
        };
        
        for (BlockPos corner : corners) {
            if (isNormalWorldY(corner.getY())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取镜像世界的过渡强度（0.0到1.0）
     */
    public static float getTransitionStrength(Entity entity) {
        double y = entity.getY();
        double transitionStart = MirrorWorldMod.MIRROR_TRANSITION_Y - 5;
        double transitionEnd = MirrorWorldMod.MIRROR_TRANSITION_Y + 5;
        
        if (y <= transitionStart) return 0.0f;
        if (y >= transitionEnd) return 1.0f;
        
        return (float) ((y - transitionStart) / (transitionEnd - transitionStart));
    }
    
    /**
     * 检查是否应该应用重力效果
     */
    public static boolean shouldApplyGravity(Entity entity) {
        return MirrorWorldConfig.isGravityEnabled() && isPlayer(entity);
    }
    
    /**
     * 获取配置的重力强度
     */
    public static double getGravityStrength() {
        return MirrorWorldConfig.getGravityStrength();
    }
    
    /**
     * 检查是否应该生成边界标记
     */
    public static boolean shouldGenerateBoundaryMarkers() {
        return MirrorWorldConfig.isBoundaryMarkersEnabled();
    }
    
    /**
     * 检查是否应该启用粒子效果
     */
    public static boolean shouldEnableParticleEffects() {
        return MirrorWorldConfig.isParticleEffectsEnabled();
    }
    
    /**
     * 检查是否应该启用过渡效果
     */
    public static boolean shouldEnableTransitionEffects() {
        return MirrorWorldConfig.isTransitionEffectsEnabled();
    }
    
    /**
     * 检查是否启用了调试模式
     */
    public static boolean isDebugModeEnabled() {
        return MirrorWorldConfig.isDebugModeEnabled();
    }
    
    /**
     * 检查是否启用了性能优化
     */
    public static boolean isPerformanceOptimizationEnabled() {
        return MirrorWorldConfig.isPerformanceOptimizationEnabled();
    }
}