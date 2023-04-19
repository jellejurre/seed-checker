# SeedChecker 1.16.1

This is a repostiory which allows you to run minecraft world generation and perform checks on it in a fraction of the time launching a world yourself would. The example tests show how to do this.

## Install Instructions

Add the following to your build.gradle repositories block
```    
    maven {url "https://jitpack.io"}
```
and the following to your build.gradle dependencies block:
```
    implementation('com.github.jellejurre:seed-checker:bc523487ee'){transitive=false}
```

p.s. If you want this code to run faster, you can add -Xmx4096M as a JVM option.
## Credit
Made by [jellejurre](https://github.com/jellejurre) (discord: @jellejurre#8585) and [dragontamerfred](https://github.com/KalleStruik) (discord: @dragontamerfred#2779)  
Published with help from WearBlackAllDay
