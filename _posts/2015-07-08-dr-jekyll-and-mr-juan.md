---
title: Dr.Jekyll and Mr.Juan
date: 2015-07-08T15:49:21+02:00
author: jperezsorrosal
layout: post
---
This is the first post, just to say that to build this site I used and learn a bit of [Jekyll](http://jekyllrb.com/) on [GitHub Pages](https://pages.github.com).
I used the template [Minimal Mistakes](http://mmistakes.github.io/minimal-mistakes/).

Some Scala syntax highlight test:

{% highlight scala %}
object Main {
  def main(args: Array[String]) {
    try {
      val elems = args map Integer.parseInt
      println("The sum of my arguments is: " + elems.foldRight(0) (_ + _))
    } catch {
      case e: NumberFormatException => 
        println("Usage: scala Main <n1> <n2> ... ")
    }
  }
}
{% endhighlight %}

