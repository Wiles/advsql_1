/**
 * FILE: AnormExtensions.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 15, 2012
 * DESCRIPTION:
 *
 */
package org.sh.plc.server.util

import org.joda.time._
import org.joda.time.format._
import anorm._

/**
 * Anorm is missing date and timestamp support, so had to add this implicit to automatically convert between the types.
 * 
 * The code was taken from:
 * 		http://stackoverflow.com/questions/11388301/joda-datetime-field-on-play-framework-2-0s-anorm
 * 
 * Next time go for a better SQL framework...
 */
object AnormExtensions {

  val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSS");

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
      case d: java.sql.Date       => Right(new DateTime(d.getTime))
      case str: java.lang.String  => Right(dateFormatGeneration.parseDateTime(str))
      case _                      => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass))
    }
  }

  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
      s.setTimestamp(index, new java.sql.Timestamp(aValue.withMillisOfSecond(0).getMillis()))
    }
  }
}