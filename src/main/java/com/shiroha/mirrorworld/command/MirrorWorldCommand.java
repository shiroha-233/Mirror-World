package com.shiroha.mirrorworld.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.shiroha.mirrorworld.MirrorWorldMod;
import com.shiroha.mirrorworld.config.MirrorWorldConfig;
import com.shiroha.mirrorworld.debug.MirrorWorldDebugger;
import com.shiroha.mirrorworld.util.MirrorWorldUtils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 镜像世界命令类，提供命令行接口来测试和控制镜像世界功能
 */
public class MirrorWorldCommand {
    
    /**
     * 注册命令
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("mirrorworld")
            .requires(source -> source.hasPermissionLevel(2)) // 需要OP权限
            .then(CommandManager.literal("test")
                .executes(MirrorWorldCommand::runAllTests)
            )
            .then(CommandManager.literal("status")
                .executes(MirrorWorldCommand::showStatus)
            )
            .then(CommandManager.literal("reload")
                .executes(MirrorWorldCommand::reloadConfig)
            )
            .then(CommandManager.literal("enable")
                .executes(MirrorWorldCommand::enableMod)
            )
            .then(CommandManager.literal("disable")
                .executes(MirrorWorldCommand::disableMod)
            )
            .then(CommandManager.literal("setheight")
                .then(CommandManager.argument("height", IntegerArgumentType.integer(400, 1024))
                    .executes(MirrorWorldCommand::setWorldHeight)
                )
            )
            .then(CommandManager.literal("settransition")
                .then(CommandManager.argument("transition", IntegerArgumentType.integer(200, 500))
                    .executes(MirrorWorldCommand::setTransitionHeight)
                )
            )
            .then(CommandManager.literal("gravity")
                .then(CommandManager.literal("enable")
                    .executes(MirrorWorldCommand::enableGravity)
                )
                .then(CommandManager.literal("disable")
                    .executes(MirrorWorldCommand::disableGravity)
                )
                .then(CommandManager.literal("setstrength")
                    .then(CommandManager.argument("strength", IntegerArgumentType.integer(1, 20))
                        .executes(context -> setGravityStrength(context, 
                            IntegerArgumentType.getInteger(context, "strength") / 100.0))
                    )
                )
            )
            .then(CommandManager.literal("debug")
                .then(CommandManager.literal("enable")
                    .executes(MirrorWorldCommand::enableDebug)
                )
                .then(CommandManager.literal("disable")
                    .executes(MirrorWorldCommand::disableDebug)
                )
            )
            .then(CommandManager.literal("teleport")
                .then(CommandManager.literal("mirror")
                    .executes(MirrorWorldCommand::teleportToMirrorWorld)
                )
                .then(CommandManager.literal("normal")
                    .executes(MirrorWorldCommand::teleportToNormalWorld)
                )
                .then(CommandManager.literal("boundary")
                    .executes(MirrorWorldCommand::teleportToBoundary)
                )
            )
        );
    }
    
    /**
     * 运行所有测试
     */
    private static int runAllTests(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFeedback(
                () -> Text.literal("This command can only be run by a player.")
                    .formatted(Formatting.RED),
                true
            );
            return 0;
        }
        
        MirrorWorldDebugger.runAllTests(player);
        return 1;
    }
    
    /**
     * 显示状态
     */
    private static int showStatus(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        
        String status = String.format(
            "Mirror World Status - Enabled: %s, Transition Y: %d, World Height: %d, Gravity: %s, Debug: %s",
            MirrorWorldConfig.isEnabled(),
            MirrorWorldConfig.getMirrorTransitionY(),
            MirrorWorldConfig.getMirrorWorldHeight(),
            MirrorWorldConfig.isGravityEnabled(),
            MirrorWorldConfig.isDebugModeEnabled()
        );
        
        context.getSource().sendFeedback(
            () -> Text.literal(status).formatted(Formatting.GREEN),
            false
        );
        
        if (player != null) {
            MirrorWorldDebugger.logPlayerWorldStatus(player);
        }
        
        return 1;
    }
    
    /**
     * 重新加载配置
     */
    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.reloadConfig();
        
        // 更新模组中的常量
        MirrorWorldMod.NORMAL_WORLD_HEIGHT = MirrorWorldConfig.getMirrorTransitionY();
        MirrorWorldMod.MIRROR_WORLD_HEIGHT = MirrorWorldConfig.getMirrorWorldHeight();
        MirrorWorldMod.MIRROR_TRANSITION_Y = MirrorWorldConfig.getMirrorTransitionY();
        
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World configuration reloaded.")
                .formatted(Formatting.GREEN),
            true
        );
        
        return 1;
    }
    
    /**
     * 启用模组
     */
    private static int enableMod(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setEnabled(true);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World enabled.")
                .formatted(Formatting.GREEN),
            true
        );
        return 1;
    }
    
    /**
     * 禁用模组
     */
    private static int disableMod(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setEnabled(false);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World disabled.")
                .formatted(Formatting.RED),
            true
        );
        return 1;
    }
    
    /**
     * 设置世界高度
     */
    private static int setWorldHeight(CommandContext<ServerCommandSource> context) {
        int height = IntegerArgumentType.getInteger(context, "height");
        MirrorWorldConfig.setMirrorWorldHeight(height);
        
        // 更新模组中的常量
        MirrorWorldMod.MIRROR_WORLD_HEIGHT = height;
        
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World height set to " + height + ".")
                .formatted(Formatting.GREEN),
            true
        );
        return 1;
    }
    
    /**
     * 设置过渡高度
     */
    private static int setTransitionHeight(CommandContext<ServerCommandSource> context) {
        int transition = IntegerArgumentType.getInteger(context, "transition");
        MirrorWorldConfig.setMirrorTransitionY(transition);
        
        // 更新模组中的常量
        MirrorWorldMod.NORMAL_WORLD_HEIGHT = transition;
        MirrorWorldMod.MIRROR_TRANSITION_Y = transition;
        
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World transition height set to " + transition + ".")
                .formatted(Formatting.GREEN),
            true
        );
        return 1;
    }
    
    /**
     * 启用重力
     */
    private static int enableGravity(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setGravityEnabled(true);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World gravity enabled.")
                .formatted(Formatting.GREEN),
            true
        );
        return 1;
    }
    
    /**
     * 禁用重力
     */
    private static int disableGravity(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setGravityEnabled(false);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World gravity disabled.")
                .formatted(Formatting.RED),
            true
        );
        return 1;
    }
    
    /**
     * 设置重力强度
     */
    private static int setGravityStrength(CommandContext<ServerCommandSource> context, double strength) {
        MirrorWorldConfig.setGravityStrength(strength);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World gravity strength set to " + strength + ".")
                .formatted(Formatting.GREEN),
            true
        );
        return 1;
    }
    
    /**
     * 启用调试模式
     */
    private static int enableDebug(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setDebugModeEnabled(true);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World debug mode enabled.")
                .formatted(Formatting.YELLOW),
            true
        );
        return 1;
    }
    
    /**
     * 禁用调试模式
     */
    private static int disableDebug(CommandContext<ServerCommandSource> context) {
        MirrorWorldConfig.setDebugModeEnabled(false);
        context.getSource().sendFeedback(
            () -> Text.literal("Mirror World debug mode disabled.")
                .formatted(Formatting.YELLOW),
            true
        );
        return 1;
    }
    
    /**
     * 传送到镜像世界
     */
    private static int teleportToMirrorWorld(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFeedback(
                () -> Text.literal("This command can only be run by a player.")
                    .formatted(Formatting.RED),
                true
            );
            return 0;
        }
        
        // 计算镜像世界中的位置
        double x = player.getX();
        double y = MirrorWorldMod.MIRROR_TRANSITION_Y + 50; // 传送到过渡区域上方
        double z = player.getZ();
        
        player.teleport(x, y, z);
        context.getSource().sendFeedback(
            () -> Text.literal("Teleported to Mirror World.")
                .formatted(Formatting.GREEN),
            true
        );
        
        return 1;
    }
    
    /**
     * 传送到正常世界
     */
    private static int teleportToNormalWorld(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFeedback(
                () -> Text.literal("This command can only be run by a player.")
                    .formatted(Formatting.RED),
                true
            );
            return 0;
        }
        
        // 计算正常世界中的位置
        double x = player.getX();
        double y = 100; // 传送到正常世界的安全高度
        double z = player.getZ();
        
        player.teleport(x, y, z);
        context.getSource().sendFeedback(
            () -> Text.literal("Teleported to Normal World.")
                .formatted(Formatting.GREEN),
            true
        );
        
        return 1;
    }
    
    /**
     * 传送到边界
     */
    private static int teleportToBoundary(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFeedback(
                () -> Text.literal("This command can only be run by a player.")
                    .formatted(Formatting.RED),
                true
            );
            return 0;
        }
        
        // 计算边界位置
        double x = player.getX();
        double y = MirrorWorldMod.MIRROR_TRANSITION_Y + 5; // 传送到过渡区域
        double z = player.getZ();
        
        player.teleport(x, y, z);
        context.getSource().sendFeedback(
            () -> Text.literal("Teleported to Mirror World boundary.")
                .formatted(Formatting.GREEN),
            true
        );
        
        return 1;
    }
}