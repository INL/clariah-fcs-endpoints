import scala.xml._
import scala.util.matching.Regex
import scala.util.matching.Regex._

/**
  parse the UD cgn mapping file nl-cgn-uposf.md and print info on cgn features that might be good translations of UD features
*/
object blop
{
   val table = new Regex("""(?s)<table[^<>]*>.*<.table>""") // "<table[^<>]*>.*<.table>".r
   case class Feature(name: String, value: String)

   case class Mapping(cgn: String, udPos: String, udFeats: String)   
   {
     lazy val features = if (udFeats.contains("="))
         udFeats.split("\\|").map(x => { val s = x.split("="); Feature(s(0),s(1)) }).toList ++ List(Feature("pos",udPos))
     else  List(Feature("pos",udPos))

     lazy val cgnPos = cgn.replaceAll("\\(.*","")
     lazy val cgnFeatures = cgn.replaceAll(".*?\\(|\\)","").split(",").toList ++ List(cgnPos)
   }

   def aap(f: String) =
   {
     println(table)
     val text = scala.io.Source.fromFile(f).getLines.mkString("\n");
     val tab = table.findFirstIn(text);
     val tabel = XML.loadString(tab.get)
     val tripels = (tabel \\ "tr").map(tr => 
        {
          val cells = (tr \\ "td").map(td => td.text)
          Mapping(cells(0),cells(2),cells(3))
        })
    
     //println(tripels)

     val byFeature = tripels.flatMap(t => t.features.map(f => (f,t))).groupBy(_._1)
     val byCGNFeature = tripels.flatMap(t => t.cgnFeatures.map(f => (f,t))).groupBy(_._1)
     val counts = byFeature.mapValues(l => l.size)
     val cgnCounts = byCGNFeature.mapValues(l => l.size)

     //byFeature.foreach(println)

     val zz = byFeature.mapValues(l => l.flatMap(t => t._2.cgnFeatures.map(f => (f,1,cgnCounts(f)))))
                       .mapValues(l => l.groupBy(_._1).map( {case (cgn,l) => (cgn,l.map(_._2).sum, cgnCounts(cgn) )}))
     zz.foreach(
       {
       case (f,l) =>
         println(s"$f\t${counts(f)}\t${l.toList.sortBy(-1 * _._2)}")
       }
     )
   }

   def main(args: Array[String]) = 
   {
     aap(args(0))
   }
}
