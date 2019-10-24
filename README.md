# common-lib

一些通用的工具库。为了避免二次开发、以及多端维护，这里将一些公共的库抽离出来，进行统一维护。

## 如何发布
先用gradlew打包生成jar，然后上传至lib目录。

说明点：
- 项目编译采用的`./gradlew clean build publish`发布即可

## 如何引用

第一步， 在你的项目中添加`github repository`到你的项目中

将其添加至`repositories`内:
```gradle
repositories {
  maven {
    url 'https://github.com/cntehang/common-lib/raw/master/lib'
  }
}

```
第二步：添加引用
```gradle
dependencies {
  // 引入特航公共包
  compile('com.cntehang:common-lib:1.1.0')
}
```
