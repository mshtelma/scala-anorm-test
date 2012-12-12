import java.sql.Connection
import scala._
import scalikejdbc.ConnectionPool
import anorm._
import anorm.SqlParser._



object MsSqlSrvDependencies /*extends App*/ {

  def getAllCodes(): List[String] = SQL(
    """
         select t.TABLE_NAME as code
         from INFORMATION_SCHEMA.TABLES t
    """
  ).as(get[String]("code") *)



  val refConstr = """
       select distinct t.TABLE_NAME as name
       from INFORMATION_SCHEMA.TABLES t
       join INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc on t.TABLE_NAME=tc.TABLE_NAME
       join INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc on tc.CONSTRAINT_NAME=rc.CONSTRAINT_NAME
       join INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc2 on rc.UNIQUE_CONSTRAINT_NAME=tc2.CONSTRAINT_NAME
       where tc2.TABLE_NAME=upper({table_name})
       order by t.TABLE_NAME
                  """


  def getSimpleConstrDependenciesListByStr(q: String, t: (String, List[String])): List[(String, List[String])] =
    Sql.sql(q).on("table_name" -> t._1).as(get[String]("name") *).map((_, t._1 :: t._2))

  def getRefConstrDependenciesList(q: String, tableName: String): List[(String, List[String])] =
    getRefConstrDependenciesList(q, List((tableName.toUpperCase, List())))

  def getRefConstrDependenciesList(q: String, l: List[(String, List[String])]): List[(String, List[String])] = {
    val ll: List[String] = l.map(_._1)
    if (l != null && l.size > 0)
      l union getRefConstrDependenciesList(q, l.map(getSimpleConstrDependenciesListByStr(q, _)).flatten.filterNot(x => {
        ll.contains(x._1)
      }))
    else List()
  }

  def transform(l: List[String]): List[(String, String)] =
    if (l.isEmpty || l.size < 2)
      List()
    else {
      val first = l.head
      val tail = l.tail
      val second = tail.head
      (first, second) :: transform(tail)
    }


  def getDeleteClause(t: (String, List[(String, String)]), ids: List[String], schema: String): String = {
    val last = if (t._2.isEmpty)
      t._1
    else
      t._2.last._2
    "delete " + t._1 + " from " + schema + "." + t._1 + " " + t._1 + " \n" +
      t._2.map(x => {
        "join " + schema + "." + x._2 + " on " + x._1 + "." + x._2 + "_FK = " + x._2 + ".ID \n"
      }).mkString("") +
      "where " + last + ".ID in (" + ids.mkString(",") + ") \n" +
      "GO \n"
  }

  Class.forName("net.sourceforge.jtds.jdbc.Driver")
  ConnectionPool.singleton("jdbc:jtds:sqlserver://localhost:1433/MM;user=mm;password=111", "", "")
  implicit val conn: Connection = ConnectionPool.borrow()


  val res: List[(String, List[String])] = getRefConstrDependenciesList(refConstr, "flight")
  //res foreach println
  res map (x => {
    (x._1, x._1 :: x._2)
  }) map (x => {
    getDeleteClause((x._1, transform(x._2)), List("1", "2"), "dbo")
  }) foreach println

}

