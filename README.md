# SeedChecker 1.17.1-pre1

## Install Instructions

Due to technical limitations (java 16 >:( ), we can't publish our project to jitpack, so this project will require a few setup steps.   
###Method 1: importing this project via gradle:  
1: generate your github secret token at https://github.com/settings/tokens  
2: add this to your gradle repositories block:
```    
maven {
    url "https://maven.pkg.github.com/jellejurre/seed-checker"
    credentials {
        username = [YOUR GITHUB NAME HERE]
        password = [YOUR GITHUB SECRET TOKEN HERE]
    }
}
```
and the following to your gradle dependencies block:
```
implementation("nl.jellejurre:seed-checker:1.0-1.17.1-pre1"){transitive=false}
```

3: congratulations, you can now use this code in your repository


### Method 2: working from this project:  
1: clone this repository: https://github.com/jellejurre/yarn  
2: open a terminal in the cloned folder   
3: type "gradlew publishToMavenLocal"   
4: clone this SeedChecker repository  
5: Congratulations, you can now code in this seedChecker repository

p.s. At the start of this program, you should probably run `SeedCheckerSettings.initialise();`.  This is not 100% required, but it might fix weird heisenbugs.

p.p.s. If you want this code to run faster, you can add -Xmx4096M as a JVM option.
## Credit
Made by [jellejurre](https://github.com/jellejurre) (discord: @jellejurre#8585) and [dragontamerfred](https://github.com/KalleStruik) (discord: @dragontamerfred#2779)