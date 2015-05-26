package com.banno.test
import akka.actor.{Actor, PoisonPill}
import akka.event.LoggingReceive

class HighLevelReceiveActor extends Actor {
  def receive = LoggingReceive(awaitInitialMessage())

  private[this] def awaitInitialMessage(): Receive = {
    case Start() => context.become(awaitSwitch())
  }

  private[this] def awaitSwitch(): Receive = awaitEnd() orElse {
    case Switch() => context.become(awaitSwitch())
  }

  private[this] def awaitEnd(): Receive = {
    case End() => self ! PoisonPill
  }
}

class AtEachBecomeActor extends Actor {
  def receive = awaitInitialMessage()

  private[this] def awaitInitialMessage(): Receive = LoggingReceive {
    case Start() => context.become(awaitSwitch())
  }

  private[this] def awaitSwitch(): Receive = LoggingReceive {
    awaitEnd() orElse {
      case Switch() => context.become(awaitSwitch())
    }
  }

  private[this] def awaitEnd(): Receive = LoggingReceive {
    case End() => self ! PoisonPill
  }
}
