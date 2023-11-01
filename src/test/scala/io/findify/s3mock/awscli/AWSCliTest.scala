package io.findify.s3mock.awscli

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.{ActorMaterializer, Materializer}
import io.findify.s3mock.S3MockTest

/** Created by shutty on 8/28/16.
  */
trait AWSCliTest extends S3MockTest {
  implicit val system: ActorSystem = ActorSystem.create("awscli")
  implicit val mat: Materializer = Materializer.createMaterializer(system)
  val http: HttpExt = Http(system)
}
