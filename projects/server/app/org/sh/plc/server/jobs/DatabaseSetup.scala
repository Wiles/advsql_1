package org.sh.plc.server.jobs

import anorm._
import anorm.SqlParser._
import play.api._
import play.api.Play._
import play.api.db._

import scala.collection.immutable.Map

object DatabaseSetup {
  private val tables = Map(
    "plc" ->
      """
	  id long primary key auto_increment,
	  name varchar(256) not null
	""",
	
    "plc_status" ->
      """
	  id long primary key auto_increment,
	  name varchar(128) not null
	""",
	
    "plc_event" ->
      """
	  id long primary key auto_increment,
	  plc long references plc(id),
	  status long references plc_status(id),
	  start date not null,
	  end date not null
	""",
	
    "plc_settings" ->
      """
	  key varchar(64) primary key,
	  value varchar(256)
	"""
  )

  def onStart(app: Application): Unit = {
    createNonExistantTableSql(tables)
  }

  def createNonExistantTableSql(tables: Map[String, String]): String = {
    val template = """
      create table %s
      (
    		%s
      );
      
    """

    DB.withConnection { implicit c =>
      val ddl = tables
        .withFilter {
          case (key, value) =>
            val count = SQL("""
	            select count(*) as c from information_schema.tables 
	            where table_schema={table_schema} and table_name={table_name}
	        """)
	        .on("table_name" -> key,
	            "table_schema" -> value
            )
	        .as(scalar[Long].single)
	        
	        val tableExists = count < 1
	        tableExists
        }
        .map { 
          case (key, value) => 
            template.format(key, value)
        }
        .mkString("", "\n", "\n")
        
        ddl
    }
  }
}