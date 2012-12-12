object HighOrderFunctionsTest /*extends App*/ {


  def f1(t: ((String,List[String]))): List[(String,List[String])] = List()

  def f2(t: ((String,List[String]))): List[(String,List[String])] =  List()

  def myfun(tableName: String)(fn: ((String,List[String])) => List[(String,List[String])]):  List[(String,List[String])] = List()


  val res: List[(String,List[String])] = myfun("...")(f1)
  res foreach println
  val res2: List[(String,List[String])] = myfun("...")(f2)
  res2 foreach println
}