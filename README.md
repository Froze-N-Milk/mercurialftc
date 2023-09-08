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
    1. or `implementation 'com.github.Froze-N-Milk:mercurialftc:<COMMIT HASH>'` if you want to use a specific commit
4. It takes about 10-15 minutes for a new commit on the branch to become buildable
    1. It can help to change the "F" or "f" in "Froze-N-Milk" to the opposite case, gradle will be more willing to look
       for something new
    2. It can help to run `gradle --refresh-dependencies`
    3. To check that you are getting the latest version, you can check the commit hash in the download info pane of the
       build panel that can be opened when you run a
       gradle sync
    4. overall very fiddly.
    5. using commit hashes can help speed up this process for realtime updates
5. You will need to have offline mode enabled in gradle when installing your code to the robot

