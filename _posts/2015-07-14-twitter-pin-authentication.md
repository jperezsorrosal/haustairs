---
layout: post
title: Twitter PIN Authentication
modified:
categories: 
excerpt:
tags: []
image:
  feature:
date: 2015-07-14T19:16:47+02:00
author: jperezsorrosal
---
> I am always doing that which I cannot do, in order that I may learn how to do it.  
-- Pablo Picasso --

The objective for the second week about the application was mainly to connect to a twitter live stream
so I gooled and found this [example](http://technikology.blogspot.de/2014/07/simple-code-to-connect-to-twitter.html).  

I had first to read about [OAuth v1](http://tools.ietf.org/html/rfc5849) to understand a bit what those tokens and things were but still
it took some time to finally understand what was going on in the example and had to check some twitter help pages:

* [Beginners Guide to OAuth](http://hueniverse.com/oauth/)
* [Twitter Stream APIs](https://dev.twitter.com/streaming/overview)
* [Public streams](https://dev.twitter.com/streaming/public)
* [Connecting to a stream endpoint](https://dev.twitter.com/streaming/overview/connecting)
* [Authorizing Requests](https://dev.twitter.com/oauth/overview/authorizing-requests)
* [sign-in guide](https://dev.twitter.com/web/sign-in/implementing)  

But I was even more lost with all this information until I realized the example I was following was using 
[**PIN-Based OAuth**](https://dev.twitter.com/oauth/pin-based). Then I could adapt the example
understanding what it was doing.  

I cannot use an [application-only authentication](https://dev.twitter.com/oauth/application-only) because it does not
allow to connect to Streaming endpoints.  

I used the library [scalaj-http](https://github.com/scalaj/scalaj-http) as http client library and 
reading the [HttpRequest](http://scalaj.github.io/scalaj-http/1.1.0/#scalaj.http.HttpRequest)
and [HttpResponse](http://scalaj.github.io/scalaj-http/1.1.0/#scalaj.http.HttpResponse) scala doc.

The code in git hub is tagged as [pint-auth](https://github.com/jperezsorrosal/houstairs/tree/pin-auth).

The Twitter is a small scala object in the file
[twitterStreamExample.scala](https://github.com/jperezsorrosal/houstairs/blob/pin-auth/src/main/scala/twitty/twitterStreamExample.scala)
which I explain now.

{% highlight scala %}
package twitty

import com.typesafe.config.ConfigFactory

import scala.util.parsing.json.JSON
import scalaj.http._

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
{% endhighlight %}

I construct a token using the *consumerKey* and *consumerKeySecret* I obtain after creating a twitter application in
twitter's [application management](https://apps.twitter.com/). These *tokens* are used to identify the *application client*
on the twitter server.

{% highlight scala %}
    val consumerToken = Token(conf.getString("houstairs.twitterConsumerKey"),
      conf.getString("houstairs.twitterConsumerSecret"))
{% endhighlight %}

<figure class="half">
	<a href="{{site.url}}/images/posts/pin-auth/twitter-application-manager.png">
		<img src="{{site.url}}/images/posts/pin-auth/twitter-application-manager.png">
	</a>
    <a href="{{site.url}}/images/posts/pin-auth/twitter-manager-access-keys.png">
            <img src="{{site.url}}/images/posts/pin-auth/twitter-manager-access-keys.png">
    </a>
    <figcaption>
        <a href="https://apps.twitter.com/"
            title="Twitter's Application management, access keys">Twitter's Application management, access, keys</a>.
    </figcaption>
</figure>

Then I ask twitter server for a token in order to authenticate the calls coming from the application, but I set the
value for the `oauth_callback` to `oob` (meaning *out-of-band* pin mode) during the
[POST oauth / request_token](https://dev.twitter.com/oauth/reference/post/oauth/request_token) call.
This parameter is usually used to specify the URL a user is redirected to should they approve the application's access
to the account.

With this call we receive a token to later ask for an authorization to twitter to access the resources (my account and the stream).

{% highlight scala %}
    val response = Http("https://api.twitter.com/oauth/request_token")
      .postForm(Seq("oauth_callback" -> "oob"))
      .oauth(consumerToken).asToken
{% endhighlight %}


Then I ask to go with a web browser to request a verifier using the token we received.

{% highlight scala %}
println("To authorize this client app, go to https://api.twitter.com/oauth/authorize?oauth_token=" + response.body.key)
{% endhighlight %}

We click on the button to authorize the app and a verifier code is displayed. We should paste that code in the console.

<figure class="third">
        <a href="{{site.url}}/images/posts/pin-auth/twitter-pin-authorize.png">
            <img src="{{site.url}}/images/posts/pin-auth/twitter-pin-authorize.png">
        </a>
        <a href="{{site.url}}/images/posts/pin-auth/twitter-pin-verifier-code.png">
            <img src="{{site.url}}/images/posts/pin-auth/twitter-pin-verifier-code.png">
        </a>
        <a href="{{site.url}}/images/posts/pin-auth/twitter-console-pin-verifier.png">
            <img src="{{site.url}}/images/posts/pin-auth/twitter-console-pin-verifier.png">
        </a>
</figure>


After I request an **access token** using the **consumer token**, the **authentication_token** and the **validation code**

{% highlight scala %}
    val accessToken = Http("https://api.twitter.com/oauth/access_token")
      .postForm
      .oauth(consumerToken, response.body, verifier).asToken
{% endhighlight %}

Finally I perform another request to the stream using the **consumer token** and the **access token** I received.
I pass some parameters to set the filters, selecting the topics I want to filter for example.
And then I print then out to the console.
{% highlight scala %}
    val request = Http("https://stream.twitter.com/1.1/statuses/filter.json")
      .param("track", "PlutoFlyby, scala, Grecia, Greece")
      .oauth(consumerToken, accessToken.body)
      .options(HttpOptions.connTimeout(500000), HttpOptions.readTimeout(500000))
{% endhighlight %}


<figure class="half">
	<a href="{{site.url}}/images/posts/pin-auth/twitter-console-pin-tweets.png">
		<img src="{{site.url}}/images/posts/pin-auth/twitter-console-pin-tweets.png">
	</a>
</figure>

Next step will be to refactor this so I do not use this **PIN authentication**, parse the tweets with Json and
asynchronously process the tweets.
