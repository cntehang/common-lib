# common-lib

## 简介

- 后端服务通用类库。

- 除了 tmc-services，其他服务强烈推荐使用，我们也会定期将 tmc-services 和这里面的方法进行同步。

- 好处：为了避免二次开发、以及多端维护。

## 当前服务如何发布

1. 在 application_version.gradle 文件中，增加版本号；

2. 在 build_setups.gradle 文件中，增阿基版本号，号码和上一步的一致；

3. 编译 + 发布：运行命令`./gradlew clean build publish`；

## 消费者服务如何引用

### 1. 添加 maven 仓库

在目标服务中添加 common-lib 的仓库地址：
```gradle
repositories {
  maven {
    url 'https://github.com/cntehang/common-lib/raw/master/lib'
  }
}
```

### 2. 添加依赖包

在目标服务中依赖 common-lib 包：
```gradle
dependencies {
  // 引入特航公共包
  compile('com.cntehang:common-lib:1.1.0')
}
```