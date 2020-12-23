package io.findify.s3mock.awscli

import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.amazonaws.services.s3.model.AmazonS3Exception

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by shutty on 8/28/16.
  */
class GetObjectTest extends AWSCliTest {
  override def behaviour(fixture: => Fixture) = {
    val s3 = fixture.client
    val port = fixture.port
    it should "receive LastModified header with AWS CLI" in {
      s3.createBucket("awscli-lm")
      s3.putObject("awscli-lm", "foo", "bar")
      val response = Await.result(http.singleRequest(HttpRequest(method = HttpMethods.GET, uri = s"http://127.0.0.1:$port/awscli-lm/foo")), 10.seconds)
      response.headers.find(_.is("last-modified")).map(_.value()) shouldBe Some("Thu, 01 Jan 1970 00:00:00 GMT")
      response.entity.contentLengthOption shouldBe Some(3)
    }
    it should "deal with HEAD requests with AWS CLI" ignore {
      s3.createBucket("awscli-head")
      s3.putObject("awscli-head", "foo2", "bar")
      val response = Await.result(http.singleRequest(HttpRequest(method = HttpMethods.HEAD, uri = s"http://127.0.0.1:$port/awscli-head/foo2")), 10.seconds)
      response.headers.find(_.is("last-modified")).map(_.value()) shouldBe Some("Thu, 01 Jan 1970 00:00:00 GMT")
      response.entity.contentLengthOption shouldBe Some(3)
      Await.result(response.entity.dataBytes.fold(ByteString(""))(_ ++ _).runWith(Sink.head), 10.seconds).utf8String shouldBe ""
    }
    it should "deal with metadata requests with AWS CLI" ignore {
      s3.createBucket("awscli-head2")
      s3.putObject("awscli-head2", "foo", "bar")
      val meta = s3.getObjectMetadata("awscli-head2", "foo")
      meta.getContentLength shouldBe 3
    }
    it should "respond with status 404 if key does not exist with AWS CLI" in {
      s3.createBucket("awscli")
      val exc = intercept[AmazonS3Exception] {
        s3.getObject("awscli", "doesnotexist")
      }
      exc.getStatusCode shouldBe 404
      exc.getErrorCode shouldBe "NoSuchKey"
    }

    it should "respond with status 404 if bucket does not exist with AWS CLI" in {

      val exc = intercept[AmazonS3Exception] {
        s3.getObject("awscli-404", "doesnotexist")
      }
      exc.getStatusCode shouldBe 404
      exc.getErrorCode shouldBe "NoSuchBucket"
    }
  }
}
