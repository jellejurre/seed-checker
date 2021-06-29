# SeedChecker 1.17.1-pre1

## Install Instructions

Due to technical and legal limitations, we can't publish our project with mapped minecraft code, so this project will require a few setup steps.  
1: clone this repository: https://github.com/jellejurre/yarn  
2: open a terminal in the cloned folder   
3: type "gradlew publishToMavenLocal"   
4: clone this SeedChecker repository  
5: code in this repository (sadly, publishing it and importing it will remove class names, which is possible to work with, but very difficult)

p.s. At the start of this program, you should run `SeedCheckerSettings.initialise();`.  This is not 100% required, but it might fix weird heisenbugs.

p.p.s. If you are getting an OutOfMemoryError, you might have to give the JVM more heap space by using -Xmx1024M as a JVM option. If you multithread, using this option will definitely increase speed as well.  
Another way to fix this issue is to use fewer threads.
## Credit
Made by [jellejurre](https://github.com/jellejurre) (discord: @jellejurre#8585) and [dragontamerfred](https://github.com/KalleStruik) (discord: @dragontamerfred#2779)