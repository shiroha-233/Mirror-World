package com.shiroha.mirrorworld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shiroha.mirrorworld.config.MirrorWorldConfig;
import com.shiroha.mirrorworld.world.MirrorWorldGenerator;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;

public class MirrorWorldMod implements ModInitializer {
    public static final String MOD_ID = "mirror-world";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // 世界常量（从配置文件中读取）
    public static int NORMAL_WORLD_HEIGHT;
    public static int MIRROR_WORLD_HEIGHT;
    public static int MIRROR_TRANSITION_Y;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Mirror World Mod...");
        
        // 加载配置文件
        MirrorWorldConfig.loadConfig();
        
        // 从配置文件中读取常量
        NORMAL_WORLD_HEIGHT = MirrorWorldConfig.getMirrorTransitionY();
        MIRROR_WORLD_HEIGHT = MirrorWorldConfig.getMirrorWorldHeight();
        MIRROR_TRANSITION_Y = MirrorWorldConfig.getMirrorTransitionY();
        
        // 检查模组是否启用
        if (!MirrorWorldConfig.isEnabled()) {
            LOGGER.info("Mirror World Mod is disabled in configuration.");
            return;
        }
        
        // 注册区块加载事件，用于生成镜像世界
        ServerChunkEvents.CHUNK_LOAD.register(this::onChunkLoad);
        
        // 注册实体事件，用于处理重力翻转
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (MirrorWorldUtils.isPlayer(entity)) {
                MirrorWorldUtils.setupPlayerGravity(entity);
            }
        });
        
        LOGGER.info("Mirror World Mod initialized successfully!");
        LOGGER.info("Configuration: Transition Y={}, World Height={}, Gravity Enabled={}",
            MIRROR_TRANSITION_Y, MIRROR_WORLD_HEIGHT, MirrorWorldConfig.isGravityEnabled());
    }
    
    /**
     * 区块加载事件处理
     */
    private void onChunkLoad(ServerWorld world, WorldChunk chunk) {
        // 只在主世界处理镜像生成
        if (!world.getRegistryKey().equals(net.minecraft.world.World.OVERWORLD)) {
            return;
        }
        
        // 使用异步方式生成镜像世界，避免阻塞主线程
        MirrorWorldGenerator.generateMirrorChunkAsync(world, chunk);
    }
    
    /**
     * 获取模组配置
     */
    public static MirrorWorldConfig getConfig() {
        // MirrorWorldConfig使用静态方法，不需要实例
        return null; // 直接使用MirrorWorldConfig的静态方法
    }
}