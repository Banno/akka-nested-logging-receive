package com.banno.test
import akka.actor.{Actor, ActorLogging, PoisonPill}
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

class AtEachBecomeActor extends Actor with ActorLogging {
  def receive = awaitInitialMessage()

  private[this] def awaitInitialMessage(): Receive = LoggingReceive {
    case Start() => context.become(awaitSwitch())
  }

  private[this] def awaitSwitch(): Receive = {
    awaitEnd() orElse LoggingReceive {
      case Switch() => context.become(awaitSwitch())
    }
  }

  private[this] def awaitEnd(): Receive = {
    case End() =>
      log.debug(s"Got End()")
      self ! PoisonPill
  }
}
