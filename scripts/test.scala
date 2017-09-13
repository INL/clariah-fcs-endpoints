import scala.xml._
import java.net.URLEncoder._

object FCSTest
{
  val server = "http://localhost:8080/blacklab-sru-server/sru?operation=searchRetrieve&queryType=fcs&query="

  def test(corpus: String, query: String)
  {
    val url = s"$server${encode(query)}&x-fcs-context=$corpus" 
    val doc = XML.load(url)
    //println(doc)
    (doc \\ "Advanced").toList.foreach (
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
        // en daarin weel de spans ...
      }
    )
  }
  
  def main(args: Array[String]) = 
  {
    test(args(0),args(1))
  }
}

//  xmlns:adv="http://clarin.eu/fcs/dataview/advanced"
