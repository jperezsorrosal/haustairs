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

    val response = Http("https://api.twitter.com/oauth/request_token")
      .postForm(Seq("oauth_callback" -> "oob"))
      .oauth(consumerToken).asToken

    println(response.body)


    println("To authorize this client app, go to https://api.twitter.com/oauth/authorize?oauth_token=" + response.body.key)

    val verifier = Console.readLine("Enter verifier: ").trim
    println(verifier)

    val accessToken = Http("https://api.twitter.com/oauth/access_token")
      .postForm
      .oauth(consumerToken, response.body, verifier).asToken

    println(Http("https://api.twitter.com/1.1/account/settings.json").oauth(consumerToken, accessToken.body).asString)

    val request = Http("https://stream.twitter.com/1.1/statuses/filter.json")
      .param("track", "PlutoFlyby, scala, Grecia, Greece")
      .oauth(consumerToken, accessToken.body)
      .options(HttpOptions.connTimeout(500000), HttpOptions.readTimeout(500000))

    //streaming api read it continuously and do whatever with it
    //i am just printing it

    request.execute(parser = { inputStream =>
      (Stream.continually(inputStream.read()).takeWhile(_ != -1)
        .foreach( i => print(i.asInstanceOf[Char])))
    })
  }
}
