package com.shiroha.mirrorworld.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;

import java.util.concurrent.CompletableFuture;

/**
 * 镜像世界生成器，负责在头顶生成镜像世界
 */
public class MirrorWorldGenerator {
    
    /**
     * 异步生成镜像区块
     */
    public static void generateMirrorChunkAsync(ServerWorld world, WorldChunk chunk) {
        CompletableFuture.runAsync(() -> {
            try {
                generateMirrorChunk(world, chunk);
            } catch (Exception e) {
                MirrorWorldMod.LOGGER.error("Error generating mirror chunk at {}", chunk.getPos(), e);
            }
        });
    }
    
    /**
     * 生成镜像区块
     */
    public static void generateMirrorChunk(ServerWorld world, WorldChunk chunk) {
        if (!MirrorWorldUtils.needsMirrorGeneration(chunk)) {
            return;
        }
        
        ChunkPos chunkPos = chunk.getPos();
        MirrorWorldMod.LOGGER.debug("Generating mirror chunk at {}", chunkPos);
        
        // 遍历区块中的所有方块
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < MirrorWorldMod.NORMAL_WORLD_HEIGHT; y++) {
                    BlockPos sourcePos = new BlockPos(
                        chunkPos.getStartX() + x,
                        y,
                        chunkPos.getStartZ() + z
                    );
                    
                    // 只处理正常世界范围内的方块
                    if (MirrorWorldUtils.isNormalWorldY(y)) {
                        BlockState sourceState = world.getBlockState(sourcePos);
                        
                        // 跳过空气方块以提高性能
                        if (sourceState.isAir()) {
                            continue;
                        }
                        
                        // 计算镜像位置
                        BlockPos mirrorPos = MirrorWorldUtils.toMirrorWorldCoords(sourcePos);
                        
                        // 在镜像世界设置方块
                        world.setBlockState(mirrorPos, sourceState, Block.NOTIFY_ALL);
                    }
                }
            }
        }
        
        // 生成镜像世界的生物群系装饰
        MirrorBiomeHandler.processMirrorBiomeFeatures(world, chunk);
        
        // 处理镜像世界的结构生成
        MirrorBiomeHandler.processMirrorStructures(world, chunk);
        
        // 添加镜像世界的特殊装饰
        MirrorBiomeHandler.addMirrorWorldDecorations(world, chunk);
        
        MirrorWorldMod.LOGGER.debug("Mirror chunk generation completed at {}", chunkPos);
    }
    
    
    /**
     * 检查是否应该生成镜像世界
     */
    public static boolean shouldGenerateMirrorWorld(ServerWorld world) {
        // 只在主世界生成镜像世界
        return world.getRegistryKey().equals(net.minecraft.world.World.OVERWORLD);
    }
    
    /**
     * 获取镜像世界的高度范围
     */
    public static int[] getMirrorWorldHeightRange() {
        return new int[]{
            MirrorWorldMod.MIRROR_TRANSITION_Y + 1,
            MirrorWorldMod.MIRROR_WORLD_HEIGHT - 1
        };
    }
    
    /**
     * 计算镜像世界的生成优先级
     */
    public static int getGenerationPriority(WorldChunk chunk) {
        // 根据区块与过渡区域的距离计算优先级
        ChunkPos chunkPos = chunk.getPos();
        int centerY = (MirrorWorldMod.MIRROR_TRANSITION_Y + MirrorWorldMod.MIRROR_WORLD_HEIGHT) / 2;
        
        // 简单的距离计算，可以根据需要调整
        int distance = Math.abs(chunkPos.x * 16 + 8 - 0) + Math.abs(chunkPos.z * 16 + 8 - 0);
        return distance;
    }
    
    /**
     * 优化镜像世界生成性能
     */
    public static void optimizeGeneration(ServerWorld world, WorldChunk chunk) {
        // 使用更高效的算法来生成镜像世界
        // 例如：只处理非空气方块，使用批量操作等
        
        // 可以在这里添加具体的优化逻辑：
        // 1. 批量设置方块状态以减少更新次数
        // 2. 使用更高效的数据结构来存储和处理方块信息
        // 3. 对于大区块，可以考虑分批处理
        // 4. 缓存常用的计算结果
    }
}