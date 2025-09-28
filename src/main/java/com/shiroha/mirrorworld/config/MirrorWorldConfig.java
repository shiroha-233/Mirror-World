package com.shiroha.mirrorworld.config;

import com.shiroha.mirrorworld.MirrorWorldMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * 镜像世界配置类，处理模组的配置文件
 */
public class MirrorWorldConfig {
    
    private static final String CONFIG_FILE_NAME = "mirror-world.properties";
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);
    
    // 配置项
    private static boolean enabled = true;
    private static int mirrorTransitionY = 320;
    private static int mirrorWorldHeight = 640;
    private static boolean gravityEnabled = true;
    private static double gravityStrength = 0.08;
    private static boolean biomeMirroringEnabled = true;
    private static boolean structureMirroringEnabled = true;
    private static boolean boundaryMarkersEnabled = true;
    private static boolean particleEffectsEnabled = true;
    private static boolean transitionEffectsEnabled = true;
    private static boolean debugModeEnabled = false;
    private static boolean performanceOptimizationEnabled = true;
    
    /**
     * 加载配置文件
     */
    public static void loadConfig() {
        Properties properties = new Properties();
        
        // 设置默认值
        setDefaultValues(properties);
        
        // 如果配置文件存在，则加载它
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                properties.load(reader);
                MirrorWorldMod.LOGGER.info("Loaded Mirror World configuration from {}", CONFIG_FILE);
            } catch (IOException e) {
                MirrorWorldMod.LOGGER.error("Failed to load configuration file", e);
            }
        } else {
            // 配置文件不存在，创建默认配置文件
            saveConfig(properties);
        }
        
        // 从属性中读取配置值
        loadValuesFromProperties(properties);
    }
    
    /**
     * 设置默认配置值
     */
    private static void setDefaultValues(Properties properties) {
        properties.setProperty("enabled", String.valueOf(enabled));
        properties.setProperty("mirrorTransitionY", String.valueOf(mirrorTransitionY));
        properties.setProperty("mirrorWorldHeight", String.valueOf(mirrorWorldHeight));
        properties.setProperty("gravityEnabled", String.valueOf(gravityEnabled));
        properties.setProperty("gravityStrength", String.valueOf(gravityStrength));
        properties.setProperty("biomeMirroringEnabled", String.valueOf(biomeMirroringEnabled));
        properties.setProperty("structureMirroringEnabled", String.valueOf(structureMirroringEnabled));
        properties.setProperty("boundaryMarkersEnabled", String.valueOf(boundaryMarkersEnabled));
        properties.setProperty("particleEffectsEnabled", String.valueOf(particleEffectsEnabled));
        properties.setProperty("transitionEffectsEnabled", String.valueOf(transitionEffectsEnabled));
        properties.setProperty("debugModeEnabled", String.valueOf(debugModeEnabled));
        properties.setProperty("performanceOptimizationEnabled", String.valueOf(performanceOptimizationEnabled));
        
        // 添加注释
        properties.setProperty("# enabled", "是否启用镜像世界功能");
        properties.setProperty("# mirrorTransitionY", "镜像世界的过渡Y坐标");
        properties.setProperty("# mirrorWorldHeight", "镜像世界的最大高度");
        properties.setProperty("# gravityEnabled", "是否启用重力翻转功能");
        properties.setProperty("# gravityStrength", "重力强度");
        properties.setProperty("# biomeMirroringEnabled", "是否启用生物群系镜像");
        properties.setProperty("# structureMirroringEnabled", "是否启用结构镜像");
        properties.setProperty("# boundaryMarkersEnabled", "是否启用边界标记");
        properties.setProperty("# particleEffectsEnabled", "是否启用粒子效果");
        properties.setProperty("# transitionEffectsEnabled", "是否启用过渡效果");
        properties.setProperty("# debugModeEnabled", "是否启用调试模式");
        properties.setProperty("# performanceOptimizationEnabled", "是否启用性能优化");
    }
    
    /**
     * 从属性中加载配置值
     */
    private static void loadValuesFromProperties(Properties properties) {
        try {
            enabled = Boolean.parseBoolean(properties.getProperty("enabled", String.valueOf(enabled)));
            mirrorTransitionY = Integer.parseInt(properties.getProperty("mirrorTransitionY", String.valueOf(mirrorTransitionY)));
            mirrorWorldHeight = Integer.parseInt(properties.getProperty("mirrorWorldHeight", String.valueOf(mirrorWorldHeight)));
            gravityEnabled = Boolean.parseBoolean(properties.getProperty("gravityEnabled", String.valueOf(gravityEnabled)));
            gravityStrength = Double.parseDouble(properties.getProperty("gravityStrength", String.valueOf(gravityStrength)));
            biomeMirroringEnabled = Boolean.parseBoolean(properties.getProperty("biomeMirroringEnabled", String.valueOf(biomeMirroringEnabled)));
            structureMirroringEnabled = Boolean.parseBoolean(properties.getProperty("structureMirroringEnabled", String.valueOf(structureMirroringEnabled)));
            boundaryMarkersEnabled = Boolean.parseBoolean(properties.getProperty("boundaryMarkersEnabled", String.valueOf(boundaryMarkersEnabled)));
            particleEffectsEnabled = Boolean.parseBoolean(properties.getProperty("particleEffectsEnabled", String.valueOf(particleEffectsEnabled)));
            transitionEffectsEnabled = Boolean.parseBoolean(properties.getProperty("transitionEffectsEnabled", String.valueOf(transitionEffectsEnabled)));
            debugModeEnabled = Boolean.parseBoolean(properties.getProperty("debugModeEnabled", String.valueOf(debugModeEnabled)));
            performanceOptimizationEnabled = Boolean.parseBoolean(properties.getProperty("performanceOptimizationEnabled", String.valueOf(performanceOptimizationEnabled)));
        } catch (NumberFormatException e) {
            MirrorWorldMod.LOGGER.error("Failed to parse configuration values", e);
        }
    }
    
    /**
     * 保存配置文件
     */
    public static void saveConfig() {
        Properties properties = new Properties();
        setDefaultValues(properties);
        saveConfig(properties);
    }
    
    /**
     * 保存配置文件到文件
     */
    private static void saveConfig(Properties properties) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            properties.store(writer, "Mirror World Mod Configuration");
            MirrorWorldMod.LOGGER.info("Saved Mirror World configuration to {}", CONFIG_FILE);
        } catch (IOException e) {
            MirrorWorldMod.LOGGER.error("Failed to save configuration file", e);
        }
    }
    
    /**
     * 重新加载配置文件
     */
    public static void reloadConfig() {
        loadConfig();
        MirrorWorldMod.LOGGER.info("Reloaded Mirror World configuration");
    }
    
    // Getter方法
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static int getMirrorTransitionY() {
        return mirrorTransitionY;
    }
    
    public static int getMirrorWorldHeight() {
        return mirrorWorldHeight;
    }
    
    public static boolean isGravityEnabled() {
        return gravityEnabled;
    }
    
    public static double getGravityStrength() {
        return gravityStrength;
    }
    
    public static boolean isBiomeMirroringEnabled() {
        return biomeMirroringEnabled;
    }
    
    public static boolean isStructureMirroringEnabled() {
        return structureMirroringEnabled;
    }
    
    public static boolean isBoundaryMarkersEnabled() {
        return boundaryMarkersEnabled;
    }
    
    public static boolean isParticleEffectsEnabled() {
        return particleEffectsEnabled;
    }
    
    public static boolean isTransitionEffectsEnabled() {
        return transitionEffectsEnabled;
    }
    
    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }
    
    public static boolean isPerformanceOptimizationEnabled() {
        return performanceOptimizationEnabled;
    }
    
    // Setter方法
    public static void setEnabled(boolean value) {
        enabled = value;
        saveConfig();
    }
    
    public static void setMirrorTransitionY(int value) {
        mirrorTransitionY = value;
        saveConfig();
    }
    
    public static void setMirrorWorldHeight(int value) {
        mirrorWorldHeight = value;
        saveConfig();
    }
    
    public static void setGravityEnabled(boolean value) {
        gravityEnabled = value;
        saveConfig();
    }
    
    public static void setGravityStrength(double value) {
        gravityStrength = value;
        saveConfig();
    }
    
    public static void setBiomeMirroringEnabled(boolean value) {
        biomeMirroringEnabled = value;
        saveConfig();
    }
    
    public static void setStructureMirroringEnabled(boolean value) {
        structureMirroringEnabled = value;
        saveConfig();
    }
    
    public static void setBoundaryMarkersEnabled(boolean value) {
        boundaryMarkersEnabled = value;
        saveConfig();
    }
    
    public static void setParticleEffectsEnabled(boolean value) {
        particleEffectsEnabled = value;
        saveConfig();
    }
    
    public static void setTransitionEffectsEnabled(boolean value) {
        transitionEffectsEnabled = value;
        saveConfig();
    }
    
    public static void setDebugModeEnabled(boolean value) {
        debugModeEnabled = value;
        saveConfig();
    }
    
    public static void setPerformanceOptimizationEnabled(boolean value) {
        performanceOptimizationEnabled = value;
        saveConfig();
    }
}