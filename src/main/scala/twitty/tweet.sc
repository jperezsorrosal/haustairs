import com.typesafe.config.ConfigFactory

import scalaj.http._

val conf = ConfigFactory.load()

val consumerToken = Token("vNcz3DSyEvXiSXyjsJAOl7Xhv","AVc2t5F755V9b8RyY2xshLsponjtNvsGSdb5N4rskV6z9juKdL")
val response = Http("https://api.twitter.com/oauth/request_token")
  .postForm(Seq("oauth_callback" -> "oob"))
  .oauth(consumerToken).asToken


response
response.body

