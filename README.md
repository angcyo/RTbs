# RTbs
腾讯tbs浏览器x5服务

### 2018-2-7
版本：v3.5.0.1004

#### 腾讯X5 
腾讯X5 不提供 64位so
所以 项目需要用 32位模式编译.

腾讯只提供了 `armeabi/liblbs.so`

```
android {
    defaultConfig {
        ndk {
            // 设置支持的SO库架构
            abiFilters 'armeabi' 
        }
    }
}
```

https://x5.tencent.com/tbs/technical.html#/detail/sdk/1/34cf1488-7dc2-41ca-a77f-0014112bcab7