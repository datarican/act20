
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
        RowDate(dateParts(0).toInt, dateParts(1).toInt, dateParts(2).toInt),
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

println("Running beneficiaries analysis...\n=================================")
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
val act20RowsBySortedYear = act20Rows
  .sortWith {(a, b) => a.approvalDate.year > b.approvalDate.year}
  .groupBy {row =>
    row.approvalDate.year
  }
println("The following are the number Act 20 recipients by year:")
import scala.collection.immutable.SortedMap
val act20RowsBySortedYearMap = SortedMap(act20RowsBySortedYear.toArray:_*)
act20RowsBySortedYearMap
  .foreach {case (year, rows) => println(s"\t${year}: ${rows.length}")}

// Get all Act 20 approved orgs that match 'tech' related terms
val techKeywords = List(
  "ai",
  "app",
  "blockchain",
  "cloud",
  "code",
  "codigo",
  "crypto",
  "cyber",
  "data",
  "digital",
  "desarrollo",
  "dev",
  "info",
  "intel",
  "ml",
  "online",
  "platform",
  "program",
  "security",
  "sistema",
  "solucion",
  "solution",
  "software",
  "sys",
  "tech",
  "ui",
  "ux",
  "web",
)
val act20OrgMatch = act20Rows
  .sortWith {(a, b) => a.approvalDate.year > b.approvalDate.year}
  .filter {row => 
    val matches = techKeywords.map {word => row.org.toLowerCase.contains(word) }
    matches.exists {b => b == true}
  }
  .groupBy {_.approvalDate.year}

val act20OrgMatchBySortedYear = SortedMap(act20OrgMatch.toArray:_*)
println(s"Act 20 recipients who's companies name matches tech keywords: ${techKeywords}")
act20OrgMatchBySortedYear
  .foreach {case (year, rows) => {
    println(s"\t${year}: ${rows.length}")
    // Uncomment to display company names
    //rows.foreach {row => println(s"\t\t${row.org}")}
  }}