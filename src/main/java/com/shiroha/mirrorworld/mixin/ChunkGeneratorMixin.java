package com.shiroha.mirrorworld.mixin;

import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 区块生成器Mixin，用于在区块生成时创建镜像世界
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    
    /**
     * 在区块生成完成后，生成镜像世界部分
     */
    @Inject(method = "generateFeatures", at = @At("RETURN"))
    private void onGenerateFeatures(ServerWorld world, Chunk chunk, CallbackInfo ci) {
        // 只在主世界处理镜像生成
        if (!world.getRegistryKey().equals(net.minecraft.world.World.OVERWORLD)) {
            return;
        }
        
        // 检查是否需要生成镜像世界
        if (shouldGenerateMirrorForChunk(chunk)) {
            generateMirrorWorldForChunk(world, chunk);
        }
    }
    
    /**
     * 检查是否需要为该区块生成镜像世界
     */
    private boolean shouldGenerateMirrorForChunk(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        
        // 检查区块是否在可能需要镜像生成的范围内
        // 这里可以根据需求调整逻辑
        return true; // 暂时为所有区块生成镜像
    }
    
    /**
     * 为区块生成镜像世界
     */
    private void generateMirrorWorldForChunk(ServerWorld world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        
        // 遍历区块中的所有方块，生成镜像部分
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
                        // 获取源方块状态
                        var sourceState = world.getBlockState(sourcePos);
                        
                        // 跳过空气方块以提高性能
                        if (sourceState.isAir()) {
                            continue;
                        }
                        
                        // 计算镜像位置
                        BlockPos mirrorPos = MirrorWorldUtils.toMirrorWorldCoords(sourcePos);
                        
                        // 在镜像世界设置方块
                        world.setBlockState(mirrorPos, sourceState, 3);
                    }
                }
            }
        }
        
        // 添加镜像世界的边界标记
        addMirrorBoundaryMarkers(world, chunkPos);
    }
    
    /**
     * 添加镜像世界边界标记
     */
    private void addMirrorBoundaryMarkers(ServerWorld world, ChunkPos chunkPos) {
        int boundaryY = MirrorWorldMod.MIRROR_TRANSITION_Y;
        
        // 在区块的四个角添加发光石标记
        BlockPos[] corners = {
            new BlockPos(chunkPos.getStartX(), boundaryY, chunkPos.getStartZ()),
            new BlockPos(chunkPos.getEndX(), boundaryY, chunkPos.getStartZ()),
            new BlockPos(chunkPos.getStartX(), boundaryY, chunkPos.getEndZ()),
            new BlockPos(chunkPos.getEndX(), boundaryY, chunkPos.getEndZ())
        };
        
        for (BlockPos corner : corners) {
            // 只在空气位置放置标记
            if (world.getBlockState(corner).isAir()) {
                world.setBlockState(corner, net.minecraft.block.Blocks.GLOWSTONE.getDefaultState(), 3);
            }
        }
    }
    
    // 暂时注释掉有问题的方法，因为在1.20.1中方法可能不存在或签名不同
    // /**
    //  * 修改区块生成的高度范围
    //  */
    // @Inject(method = "getWorldHeight", at = @At("HEAD"), cancellable = true)
    // private void onGetWorldHeight(CallbackInfoReturnable<Integer> cir) {
    //     // 返回扩展后的世界高度
    //     cir.setReturnValue(MirrorWorldMod.MIRROR_WORLD_HEIGHT);
    // }
}