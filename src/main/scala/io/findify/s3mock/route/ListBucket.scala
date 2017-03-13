package io.findify.s3mock.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import io.findify.s3mock.error.{InternalErrorException, NoSuchBucketException}
import io.findify.s3mock.provider.Provider

import scala.util.{Failure, Success, Try}

/**
  * Created by shutty on 8/19/16.
  */
case class ListBucket(implicit provider:Provider) extends LazyLogging {
  def route(bucket:String) = get {
    parameter('prefix) { prefix =>
      complete {
        logger.info(s"listing bucket $bucket with prefix=$prefix")
        Try(provider.listBucket(bucket, prefix)) match {
          case Success(l) => HttpResponse(StatusCodes.OK, entity = l.toXML.toString)
          case Failure(e: NoSuchBucketException) =>
            HttpResponse(
              StatusCodes.NotFound,
              entity = e.toXML.toString()
            )
          case Failure(t) =>
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = InternalErrorException(t).toXML.toString()
            )
        }
      }
    } ~ complete {
      logger.info(s"listing bucket $bucket")
      Try(provider.listBucket(bucket, "")) match {
        case Success(l) => HttpResponse(StatusCodes.OK, entity = l.toXML.toString)
        case Failure(e: NoSuchBucketException) =>
          HttpResponse(
            StatusCodes.NotFound,
            entity = e.toXML.toString()
          )
        case Failure(t) =>
          HttpResponse(
            StatusCodes.InternalServerError,
            entity = InternalErrorException(t).toXML.toString()
          )
      }

    }
  }
}
