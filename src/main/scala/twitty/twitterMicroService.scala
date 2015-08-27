package twitty

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

//import scalaj.http.Token

//import scalaj.http._



trait TwitterAuthorization {
  def authorize: HttpRequest => HttpRequest
}

trait OAuthTwitterAuthorization extends TwitterAuthorization {
  import OAuth._

  val conf = ConfigFactory.load()

  val consumer = Consumer(conf.getString("houstairs.twitterConsumerKey"),
    conf.getString("houstairs.twitterConsumerSecret"))

  val token = Token(conf.getString("houstairs.twitterAccessToken"),
    conf.getString("houstairs.twitterAccessTokenSecret"))

  val params = Map(("track", "tsipras"))

  val authorize: (HttpRequest) => HttpRequest = oAuthAuthorizer(consumer, token, params)
}



trait twtService {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
}


object TwitterMicroService extends App with twtService with OAuthTwitterAuthorization {

  this: TwitterAuthorization =>

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()


    lazy val twitterConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
      Http().
        outgoingConnectionTls("stream.twitter.com")

  val twitterUri = Uri("https://stream.twitter.com/1.1/statuses/filter.json")

  println(twitterUri.toString())

  val entity = HttpEntity.Strict(MediaTypes.`application/x-www-form-urlencoded`, ByteString("track=tsipras"))
  val par = List("track=tsipras")
  val request = authorize(HttpRequest(POST, uri=twitterUri))


  println(request.getHeaders().toString )

    def twtRequest(request: HttpRequest): Future[HttpResponse] =
      Source.single(request).via(twitterConnectionFlow).runWith(Sink.head)

    val responseFuture : Future[HttpResponse] = twtRequest(request)

    responseFuture.onComplete {
      case Success(result) =>
        println(s"Result: " + result.toString)
        println("Shutting down client")
        system.shutdown()
      case Failure(e) =>
        println("Failure: " + e.getMessage)
        system.shutdown()
    }
}
