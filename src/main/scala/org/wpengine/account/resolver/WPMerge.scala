package org.wpengine.account.resolver

import cats.effect.IO
import org.http4s.client.blaze.Http1Client

object WPMerge extends App {
  val fetcher = SimpleRemoteFetcher(Http1Client[IO]().unsafeRunSync())
  println("Hello There!!")
}
