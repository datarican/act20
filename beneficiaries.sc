
println("Running beneficiaries analysis...\n=================================")

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
println(s"About to process csv ${filename}")
val data = loadCSV(filename)
println(s"Loaded ${data.length} rows")

// Display all posible benficial/incentive laws from Puerto Rico's government
val uniqueLaws = data
  .groupBy {row => row.law}
  .keySet
println(s"A total of ${uniqueLaws.size} incentives exist in Puerto Rico:")
uniqueLaws.foreach(r => println(s"\t${r}"))

// Get all Act 20 beneficieries
val act20Rows = data.filter {row => 
  row.law.contains("Act 20")
}
//act20Rows.foreach(println)
println(s"A total of ${act20Rows.length} beneficiaries found.")
// 1924

// Get all Act 20 approved in 2019
val act20RowsFrom2019 = act20Rows.filter {row =>
  row.approvalDate.year == 2019
}
println(s"A total of ${act20RowsFrom2019.length} Act 20 beneficiaries approved in 2019.")

// Get all Act 20 approved by year
val act20RowsByUnsortedYear = act20Rows.groupBy {
  row => row.approvalDate.year
}
import scala.collection.immutable.ListMap
println("The following are the number Act 20 recipients by year:")
val act20RowsBySortedYear = ListMap(
  act20RowsByUnsortedYear.toSeq.sortWith(_._1 < _._1):_*
).foreach {tuple => 
  println(s"\t${tuple._1}: ${tuple._2.length}")
}

// Get all Act 20 approved orgs that match 'tech' related terms
val techKeywords = List(
  "digital",
  "tech",
  "solution",
  "software",
  "data",
  "cyber",
  "solucion",
  "code",
  "codigo",
  "dev",
  "desarrollo",
  "programm",
)
val act20OrgMatch = act20Rows
  .sortBy {_.org}
  .filter {row => 
    val matches = techKeywords.map {word => row.org.toLowerCase.contains(word) }
    matches.exists {b => b == true}
  }
  .map {_.org}
println(s"Act 20 recipients who's companies name matches tech keywords: ${techKeywords}")
act20OrgMatch
  .zipWithIndex
  .foreach { pair => println(s"\t${pair._2 + 1}. ${pair._1}") }