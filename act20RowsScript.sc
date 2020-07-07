
case class RowDate(day: Int, month: Int, year: Int)
case class Row(law: String, org: String, approvalDate: RowDate)

def loadCSV(filename: String): Array[Row] = {

  val file = scala.io.Source.fromFile(filename)
  val lines = file.getLines.toArray

  def toRow(arr: Array[String]): Seq[Row] = {
    val dateParts = arr(2).split("/")
    Seq(
      Row(
        arr(0),
        arr(1),
        RowDate(
          dateParts(0).toInt,
          dateParts(1).toInt,
          dateParts(2).toInt
        )
      )
    )
  }

  def createRows(lines: Array[String]): Array[Row] =
    lines.flatMap {line => 
      val parts = line
        .split("\",\"") // dataset has column values which contain comma
        .map(s => s.replaceAll("\"", ""))
 
      toRow(parts)
    }

  createRows(lines.drop(1))
}

val filename = "beneficiarios.csv"
val data = loadCSV(filename)

// Display all posible benficial/incentive laws from Puerto Rico's government
val uniqueLaws = data
  .groupBy {row => row.law}
  .keySet
uniqueLaws.foreach(r => println(s"\t${r}"))

// Get all Act 20 beneficieries
val act20Rows = data.filter {row => 
  row.law.contains("Act 20")
}
val quote = "\""
// Send to stdout all Act 20 lines
act20Rows.foreach {row => 
  val date = row.approvalDate.month + "/" + row.approvalDate.day + "/" + row.approvalDate.year
  println(s"${quote}${row.org}${quote},${quote}${date}${quote}")
}