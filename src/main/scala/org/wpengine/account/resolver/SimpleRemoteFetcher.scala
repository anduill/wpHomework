package org.wpengine.account.resolver

import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.Json
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Response, Uri}
import org.wpengine.account.resolver.DomainObjects.RemoteAccountRecord

import scala.util.Try

case class SimpleRemoteFetcher(simpleClient: Client[IO], acctsBaseUrl: String = "http://interview.wpengine.io/v1/accounts") extends RemoteAccountFetcher {

  override def fetchAccount(accountId: Long): Try[RemoteAccountRecord] = {
    val uri = Uri.fromString(s"$acctsBaseUrl/$accountId").right.get
    val response = simpleClient.get[Json](uri = uri)({resp =>
      unpackJsonResponse(resp)
    })
    response.attempt.unsafeRunSync().toTry.flatMap(InputParsing.parseRemoteJsonObject)
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
