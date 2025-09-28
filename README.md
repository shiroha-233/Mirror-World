# Mirror World Mod

## 简介
Mirror World Mod 是一个为 Minecraft 1.20.1 设计的 Fabric 模组，它在头顶生成一个和地面一模一样的镜像世界。当玩家超过 320 格高度后，重力会翻转，让玩家可以在镜像世界中正常行走。

## 功能特性
- 在 Y=320 以上生成镜像世界
- 超过 320 格高度后重力翻转
- 镜像世界的生物群系和结构与正常世界对称
- 可配置的重力强度和世界高度
- 过渡区域的特殊效果
- 命令系统用于测试和配置

## 安装
1. 确保已安装 Minecraft 1.20.1 和 Fabric Loader
2. 下载最新版本的 Mirror World Mod
3. 将模组文件放入 `.minecraft/mods` 文件夹
4. 启动游戏

## 配置
模组会在第一次运行时生成配置文件 `config/mirror-world.properties`，你可以修改以下配置项：
- `enabled`: 是否启用镜像世界功能
- `mirrorTransitionY`: 镜像世界的过渡 Y 坐标
- `mirrorWorldHeight`: 镜像世界的最大高度
- `gravityEnabled`: 是否启用重力翻转功能
- `gravityStrength`: 重力强度
- `biomeMirroringEnabled`: 是否启用生物群系镜像
- `structureMirroringEnabled`: 是否启用结构镜像
- `debugModeEnabled`: 是否启用调试模式

## 命令
- `/mirrorworld test`: 运行所有测试
- `/mirrorworld status`: 显示当前状态
- `/mirrorworld reload`: 重新加载配置
- `/mirrorworld enable`: 启用模组
- `/mirrorworld disable`: 禁用模组
- `/mirrorworld setheight <height>`: 设置世界高度
- `/mirrorworld settransition <transition>`: 设置过渡高度
- `/mirrorworld gravity enable`: 启用重力
- `/mirrorworld gravity disable`: 禁用重力
- `/mirrorworld gravity setstrength <strength>`: 设置重力强度
- `/mirrorworld debug enable`: 启用调试模式
- `/mirrorworld debug disable`: 禁用调试模式
- `/mirrorworld teleport mirror`: 传送到镜像世界
- `/mirrorworld teleport normal`: 传送到正常世界
- `/mirrorworld teleport boundary`: 传送到边界

## 开发
### 构建
```bash
./gradlew build
```

### 运行客户端
```bash
./gradlew runClient
```

## 许可证
MIT License

## 贡献
欢迎提交 Issue 和 Pull Request 来改进这个模组。
