# MercurialFTC

## Unstable

This branch is for live testing of new features, and is not reccomended for most users, if you want to use this branch
as a dependency for your FTCRobotController:

1. add `maven { url "https://jitpack.io" } // Needed for mercurialftc` to your repositories block in
   `build.dependencies.gradle`
2. add `implementation 'com.github.Froze-N-Milk:mercurialftc:v0.01'` to the bottom of your
   dependencies block
   in
   `build.dependencies.gradle`
3. run a gradle sync

## Testing

This branch is for live testing of new features, and is not reccomended for most users, if you want to use this branch
as a dependency for your FTCRobotController:

1. add `maven { url "https://jitpack.io" } // Needed for mercurialftc` to your repositories block in
   `build.dependencies.gradle`
2. add `configurations.all {
   resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
   }` below the repositories block but above the dependencies block in `build.dependencies.gradle`
3. add `implementation 'com.github.Froze-N-Milk:mercurialftc:testing-SNAPSHOT'` to the bottom of your dependencies block
   in
   `build.dependencies.gradle`
4. It is hard to convince android studio that changes have been made, to update, change the letter 'F' in 'Froze-N-Milk'
   to lowercase, or vice versa, and then run a gradle sync, this will take a while to do (~1-2 minutes)
    1. running `gradle --refresh-dependencies` can help sometimes
    2. you can check the commit hash in the download info pane of the build panel that can be opened when you run a
       gradle sync
5. You will need to have offline mode enabled in gradle when downloading to the robot

