# SeedChecker 1.17.1-pre1

## Install Instructions

Add the following to your build.gradle repositories block
```    
    maven {url "https://jitpack.io"}
```
and the following to your build.gradle dependencies block:
```
    implementation('com.github.jellejurre:seed-checker:1.17.1-pre1-SNAPSHOT'){transitive=false}
```
p.s. At the start of this program, you should probably run `SeedCheckerSettings.initialise();`.  This is not 100% required, but it might fix weird heisenbugs.

p.p.s. If you want this code to run faster, you can add -Xmx4096M as a JVM option.
## Credit
Made by [jellejurre](https://github.com/jellejurre) (discord: @jellejurre#8585) and [dragontamerfred](https://github.com/KalleStruik) (discord: @dragontamerfred#2779)  
Published with help from WearBlackAllDay