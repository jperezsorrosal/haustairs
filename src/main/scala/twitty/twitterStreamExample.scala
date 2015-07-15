package twitty

import com.typesafe.config.ConfigFactory

import scala.util.parsing.json.JSON
import scalaj.http._


/**
 * Created by jperez on 10/07/15.
 */
object twitterStreamExample {
  def main(args: Array[String]) {

    val conf = ConfigFactory.load()

    val consumerToken = Token(conf.getString("houstairs.twitterConsumerKey"),
      conf.getString("houstairs.twitterConsumerSecret"))

    val accessToken = Token(conf.getString("houstairs.twitterAccessToken"),
      conf.getString("houstairs.twitterAccessTokenSecret"))

    println(Http("https://api.twitter.com/1.1/account/settings.json").oauth(consumerToken, accessToken).asString)

    val request = Http("https://stream.twitter.com/1.1/statuses/filter.json")
      .param("track", "PlutoFlyby, scala, Grecia, Greece")
      .oauth(consumerToken, accessToken)
      .options(HttpOptions.connTimeout(5000000), HttpOptions.readTimeout(5000000))

    request.execute(parser = { inputStream =>
      (Stream.continually(inputStream.read()).takeWhile(_ != -1)
        .foreach( i => print(i.asInstanceOf[Char])))
    })
  }
}
