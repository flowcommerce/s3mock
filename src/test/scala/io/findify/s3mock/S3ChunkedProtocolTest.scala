package io.findify.s3mock

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by shutty on 8/11/16.
  */
class S3ChunkedProtocolTest extends AnyFlatSpec with Matchers {
  implicit val system = ActorSystem.create("test")
  implicit val mat = Materializer.createMaterializer(system)

  "s3 chunk protocol" should "work with simple ins" in {
    val in = "3;chunk-signature=1234567890123456789012345678901234567890123456789012345678901234\r\nfoo\r\n3;chunk-signature=1234567890123456789012345678901234567890123456789012345678901234\r\nbar\r\n".grouped(10).map(ByteString(_)).toList
    val result = Await.result(Source(in).via(new S3ChunkedProtocolStage).map(_.utf8String).runWith(Sink.seq), 10.seconds)
    result.mkString shouldBe "foobar"
  }
  it should "not drop \\r\\n chars" in {
    val in = "5;chunk-signature=1234567890123456789012345678901234567890123456789012345678901234\r\nfoo\r\n\r\n3;chunk-signature=1234567890123456789012345678901234567890123456789012345678901234\r\nbar\r\n".grouped(10).map(ByteString(_)).toList
    val result = Await.result(Source(in).via(new S3ChunkedProtocolStage).map(_.utf8String).runWith(Sink.seq), 10.seconds)
    result.mkString shouldBe "foo\r\nbar"
  }
}
