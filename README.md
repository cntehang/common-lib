# common-lib

## 简介

- 后端服务通用类库。

- 除了 tmc-services，其他服务强烈推荐使用，我们也会定期将 tmc-services 和这里面的方法进行同步。

- 好处：为了避免二次开发、以及多端维护。

## 当前服务如何发布

1. 在 build_setups.gradle 文件中，增加版本号；

2. 编译 + 发布：运行命令`./gradlew clean build publish`；

## 消费者服务如何引用

搭建私有仓库后，仍保留 `./lib` 下的原有 jar 文件，兼容原来的老的 jar 包获取方式

- 老的 jar 包获取方式

```text
    maven {
        url 'https://github.com/cntehang/common-lib/raw/master/lib'
    }
 
  // 引入特航公共包
  compile('com.cntehang:common-lib:1.2.13')
```

- 私有仓库的jar包获取方式为：

```text
    maven {
        url 'http://nexus.itehang.cn/repository/maven-releases/'
    }

  // 引入特航公共包
  compile('com.tehang.common:common-lib:1.2.18')
```