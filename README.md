# ZxingScan_SOL
扫码工具类

>此工具可扫描一维码（Code 128）、二维码（QRCode）
> 识别图片中的码
> 还可生成二维码图片

**效果展示**

![1.gif](./img/1.gif)


## 使用方式
### Step 1. Add the JitPack repository to your build file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### Step 2. Add the dependency
```
dependencies {
        implementation 'com.github.SeedsOfLove:ZxingScan_SOL:1.0.0'
	}
```
