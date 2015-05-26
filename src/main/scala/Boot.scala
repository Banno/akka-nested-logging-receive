package com.banno.test
import akka.actor.{ActorSystem, ActorRef, Props}

object Boot extends App {
  implicit lazy val system = ActorSystem("akka-nested-logging-receive")

  lazy val highLevelActor = system.actorOf(Props[HighLevelReceiveActor])
  lazy val atEachBecomeActor = system.actorOf(Props[AtEachBecomeActor])

  println("==== Starting Boot")
  println("Interacting with high-level LoggingReceive actor")
  interact(highLevelActor)

  println("Interacting with at-each-become LoggingReceive actor")
  interact(atEachBecomeActor)

  sys.exit(0)

  private[this] def interact(ref: ActorRef): Unit = {
    ref ! Start()
    Thread.sleep(1000)
    ref ! Switch()
    Thread.sleep(1000)
    ref ! Switch()
    Thread.sleep(1000)
    ref ! End()
  }

  Thread.sleep(10000)
}
