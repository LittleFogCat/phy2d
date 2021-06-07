# phy2d

一个kotlin写的物理引擎。

无聊兴起，手痒之作。本来是想写个小游戏的，谁承想最后写了一个物理引擎出来。

模拟真实世界的物理量，实现碰撞检测、受力分析、模拟运动等。运动改变基于冲量，按照每帧为一单位时间计算。

包含模块：

| 模块 | 简介 |
---- | ----
math | 数学工具模块，主要包含了向量和点的计算
phy2d | 物理引擎模块
billiards | 用phy2d引擎制作的桌球碰撞demo
bouncedemo | 用phy2d引擎制作的重力条件下小球弹跳碰撞demo
common | 一些通用类，包含仿王者荣耀操纵杆的JoystickView
aircraftwar | 飞机大战demo，使用phy2d引擎检测碰撞，JoystickView操作

# 演示（bouncedemo）

![bouncedemo](bouncedemo.gif)