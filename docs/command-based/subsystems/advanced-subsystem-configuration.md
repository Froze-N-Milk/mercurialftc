# Advanced Subsystem Configuration

### setDefaultCommand()

You can call this method to change the default command of the subsystem, which may be useful if you want an [#initialise](../commands/#initialise "mention") or [#end](../commands/#end "mention") component on the default command. This method should probably not be called more than once in the control flow of your OpMode.

Note that [#defaultcommandexecute](./#defaultcommandexecute "mention") becomes redundant once this is called, unless you use it in your new default command. It is considered best practice to still define the execute method of the default command as `defaultCommandExecute()` if possible.&#x20;

## In-Built LambdaCommands

It can be very nice to write methods which exist on your subsystem which return new commands that are built using the [lambda-commands.md](../commands/lambda-commands.md "mention") builder, which can reduce the number of files you need to write, and reduce the public footprint of your subsystem.&#x20;

These methods are fine construct new commands, as they should only be called once when you get the command to bind it in [#registerbindings](../opmodeex/#registerbindings "mention")
