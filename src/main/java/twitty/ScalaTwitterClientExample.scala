package twitty

import com.typesafe.config.{Config, ConfigFactory}
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

object ScalaTwitterClientExample {
  def main(args : Array[String]): Unit = {

    val conf = ConfigFactory.load()

    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)

      .setOAuthConsumerKey(conf.getString("haustairs.twitterConsumerKey"))
      .setOAuthConsumerSecret(conf.getString("haustairs.twitterConsumerSecret"))
      .setOAuthAccessToken(conf.getString("haustairs.twitterAccessToken"))
      .setOAuthAccessTokenSecret(conf.getString("haustairs.twitterAccessTokenSecret"))

    val tf = new TwitterFactory(cb.build())
    val twitter = tf.getInstance()

    val statuses = twitter.getHomeTimeline()

    System.out.println("Showing  timeline.")
    val it = statuses.iterator()
    while (it.hasNext()) {
      val status = it.next()
      println(status.getUser().getName() + " :::: " +
        status.getText());
      println()
    }
  }

}
