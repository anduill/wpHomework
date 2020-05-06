package org.wpengine.account.resolver

import org.scalatest.{FlatSpec, Matchers}
import InputParsing._
import cats.effect.IO
import org.http4s.{EntityDecoder, Method, Request, Response, Uri}
import io.circe.{Decoder, Json}
import io.circe.parser._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import io.circe.generic.auto._
import org.http4s.client.blaze.Http1Client
import org.wpengine.account.resolver.DomainObjects.RemoteAccountRecord

import scala.concurrent.ExecutionContext.global
import scala.io.{BufferedSource, Source}
import scala.io.Source._

class InputParserSpec extends FlatSpec with Matchers {
  "parsing test one" should "succeed" in {
    val test_file: BufferedSource = fromResource("test_input.csv")//fromFile("test_input.csv")
    val csvRecords = parseCsvInputStream(test_file)
    csvRecords.foreach {record =>
      println(s"Record: $record")
    }
//    val client = Http1Client[IO]()
//
//    val response = client.flatMap {c => c.get[Json](uri = Uri.uri("http://interview.wpengine.io/v1/accounts/918299"))({resp =>
//      import io.circe.parser._
//      unpackJsonResponse(resp)
//    })}
//    val responseJson = response.unsafeRunSync()
    val fetcher = SimpleRemoteFetcher()
    val decodedResponse = fetcher.fetchAccount(918299)
    println(s"Response: $decodedResponse")

    test_file.close()
    println("yo")
  }
  // This can extract the JSON object out of the Response
  def unpackJsonResponse(response: Response[IO])(implicit decoder: EntityDecoder[IO, String]): IO[Json] = {
    import io.circe.parser._
    val result = decoder.decode(response, false)
    val json = parse(result.value.unsafeRunSync().right.get).right.get
    IO(json)
  }
}
