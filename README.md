# MercurialFTC

This branch is for live testing of new features, and is not reccomended for most users, if you want to use this branch as a dependency for your FTCRobotController:
1. add "maven { url "https://jitpack.io" } // Needed for MercurialFTC" to your repositories block in build.dependencies.gradle
2. add "implementation 'com.github.Froze-N-Milk:MercurialFTC:testing-SNAPSHOT'" to your dependencies block in build.dependencies.gradle
3. run a gradle sync whenever you want to update, this will take a while to do (~1m)
