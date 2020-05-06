package org.wpengine.account.resolver

import cats.effect.IO
import io.circe.Json
import org.http4s.dsl.io._
import org.http4s.client.blaze.Http1Client
import cats.effect.IO
import io.circe.Json
import org.http4s.{EntityDecoder, Response, Uri}
import org.http4s.client.blaze.Http1Client
import scala.concurrent.ExecutionContext.global

import org.wpengine.account.resolver.DomainObjects.RemoteAccountRecord

import scala.util.Try

case class SimpleRemoteFetcher(acctsBaseUrl: String = "http://interview.wpengine.io/v1/accounts") extends RemoteAccountFetcher {
  val simpleClient = Http1Client[IO]()
  override def fetchAccount(accountId: Long): Try[RemoteAccountRecord] = {
    val uri = Uri.fromString(s"$acctsBaseUrl/$accountId").right.get
    val response = simpleClient.flatMap {c => c.get[Json](uri = uri)({resp =>
      import io.circe.parser._
      unpackJsonResponse(resp)
    })}.attempt.unsafeRunSync().toTry.flatMap(InputParsing.parseRemoteJsonString)
    response
  }
  // This can extract the JSON object out of the Response
  def unpackJsonResponse(response: Response[IO])(implicit decoder: EntityDecoder[IO, String]): IO[Json] = {
    import io.circe.parser._
    IO {
      val result = decoder.decode(response, false)
      parse(result.value.unsafeRunSync().right.get).right.get
    }
  }
}
