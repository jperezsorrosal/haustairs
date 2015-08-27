package twitty

import com.typesafe.config.ConfigFactory

import scalaj.http._

object twitterStreamExample {
  def main(args: Array[String]) {

    val conf = ConfigFactory.load()

    val consumerToken = Token(conf.getString("houstairs.twitterConsumerKey"),
      conf.getString("houstairs.twitterConsumerSecret"))

    val accessToken = Token(conf.getString("houstairs.twitterAccessToken"),
      conf.getString("houstairs.twitterAccessTokenSecret"))

    val request = Http("https://stream.twitter.com/1.1/statuses/filter.json")
      .param("track", "GreeceCrisis, syriza, tsipras, Grexit,")
      .oauth(consumerToken, accessToken)
      .options(HttpOptions.connTimeout(500000), HttpOptions.readTimeout(500000))

    println("*** URL: " + request.asString)

    request.execute(parser = { inputStream =>
      (Stream.continually(inputStream.read()).takeWhile(_ != -1)
        .foreach( i => print(i.asInstanceOf[Char])))

    })
  }
}
