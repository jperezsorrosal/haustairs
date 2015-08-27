package spraying

import akka.actor.{Props, ActorSystem}

import scala.annotation.tailrec

object main extends App {
  import Commands._

  val system = ActorSystem()

  val stream = system.actorOf(Props(new TwitterStreamActor(TwitterStreamActor.twitterUri) with OAuthTwitterAuthorization))

  @tailrec
  private def commandLoop(): Unit = {
    Console.readLine() match {
      case QuitCommand         => return
      case TrackCommand(query) => stream ! query
      case _                   => println("WTF??!!")
    }

    commandLoop()
  }

  // start processing the commands
  commandLoop()

}

object Commands {

  val QuitCommand   = "quit"
  val TrackCommand = "track (.*)".r
}
