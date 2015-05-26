# akka-nested-logging-receive

There's an issue in some of our apps where we've nested too many `akka.event.LoggingReceive` calls so it appears like events aren't being handled, or they're being handled multiple times. Neither of these cases are possible to determine given the nesting of `LoggingReceive` calls around the typical `Receive` partial function in an `Actor`. This repository provides a use case for become's that should be used instead of the pattern that is currently in play.

## Example Logs

This is an example from running the logs. There were no unhandled messages even though we're using LoggingReceive calls in the actors.

```
[DEBUG] [2015-05-26 13:28:20,009] [akka-nested-logging-receive-akka.actor.default-dispatcher-4] c.b.t.HighLevelReceiveActor akka://akka-nested-logging-receive/user/$a: received handled message Start()
[DEBUG] [2015-05-26 13:28:23,009] [akka-nested-logging-receive-akka.actor.default-dispatcher-3] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Start()
[DEBUG] [2015-05-26 13:28:24,009] [akka-nested-logging-receive-akka.actor.default-dispatcher-4] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Switch()
[DEBUG] [2015-05-26 13:28:25,010] [akka-nested-logging-receive-akka.actor.default-dispatcher-2] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Switch()
[DEBUG] [2015-05-26 13:28:26,012] [akka-nested-logging-receive-akka.actor.default-dispatcher-4] c.b.t.AtEachBecomeActor akka.tcp://akka-nested-logging-receive@10.50.2.148:65030/user/$b: Got End()
```

__Before__

Here's an example from before the changes made where we were just nesting `LoggingReceive` around every `Receive` method call we were calling under `context.become`.

```
[DEBUG] [2015-05-26 13:25:52,259] [akka-nested-logging-receive-akka.actor.default-dispatcher-2] c.b.t.HighLevelReceiveActor akka://akka-nested-logging-receive/user/$a: received handled message Start()
[DEBUG] [2015-05-26 13:25:55,252] [akka-nested-logging-receive-akka.actor.default-dispatcher-4] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Start()
[DEBUG] [2015-05-26 13:25:56,252] [akka-nested-logging-receive-akka.actor.default-dispatcher-2] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received unhandled message Switch()
[DEBUG] [2015-05-26 13:25:56,252] [akka-nested-logging-receive-akka.actor.default-dispatcher-2] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Switch()
[DEBUG] [2015-05-26 13:25:57,252] [akka-nested-logging-receive-akka.actor.default-dispatcher-3] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received unhandled message Switch()
[DEBUG] [2015-05-26 13:25:57,252] [akka-nested-logging-receive-akka.actor.default-dispatcher-3] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message Switch()
[DEBUG] [2015-05-26 13:25:58,253] [akka-nested-logging-receive-akka.actor.default-dispatcher-2] c.b.t.AtEachBecomeActor akka://akka-nested-logging-receive/user/$b: received handled message End()
```
