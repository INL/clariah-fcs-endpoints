import scala.xml._
import java.net.URLEncoder._

object FCSTest
{
  val server = "http://localhost:8080/blacklab-sru-server/sru?operation=searchRetrieve&queryType=fcs&query="
  
  case class Feature(name: String, value: String)
  {
    lazy val cqp = s"[$name='$value']"
  }

  lazy val allFeatures = scala.io.Source.fromFile("doc/ud_features.txt").getLines.map(
    l => 
    {
      val c = l.split("\\t")
      Feature(c(0),c(1)) 
    }
  )

  def testAll(corpus:String) = allFeatures.foreach(f => test(corpus, f.cqp))

  def possen(l: List[Map[String,String]]) = l.map(m => s"""${m("pos")}""").toSet

  def test(corpus: String, query: String) 
  {
    val l = hits(corpus,query)
    val p = possen(l)
    println(s"$query $p")
  }

  def hits(corpus: String, query: String) =
  {
    val url = s"$server${encode(query)}&x-fcs-context=$corpus" 
    val doc = XML.load(url)
    //println(doc)
    val hits = (doc \\ "Advanced").toList.map (
      a =>
      {
        val segmentIds:List[String] = ((a \\ "Segment") \\ "@id").toList.map(_.toString)
        val highlightSegments =  ((a \\ "Span").filter(s => (s \\ "@highlight").nonEmpty) \\ "@ref").toSet.map((x:Node) => x.toString).map(sid => segmentIds.indexOf(sid))

        //println(highlightSegments)
        //println(segmentIds)

        val layers = (a \\ "Layer").map(l => ((l \ "@id").text, (l \\ "Span").map(s => ((s \ "@ref").text -> s.text)).toMap))

        //layers.foreach(println)
        val layerMap = layers.map (
          {
            case (id,m) =>
              (id.replaceAll(".*/","") -> segmentIds.map(m))
          } 
        ).toMap
        
        val index = highlightSegments.toStream.head
        //println(layerMap)
        val hToken = layerMap.mapValues(l => l(index))
        println(hToken)
        hToken
        // en daarin weel de spans ...
      }
    )
    // hits.foreach(println)
    hits
  }
  
  def main(args: Array[String]) = 
  {
    //test(args(0),args(1))
    testAll(args(0))
  }
}

//  xmlns:adv="http://clarin.eu/fcs/dataview/advanced"
