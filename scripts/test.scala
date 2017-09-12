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
        val segmentIds = ((a \\ "Segment") \ "@id").toList
        val layers = (a \\ "Layer").map(l => (l \ "@id", (l \\ "Span").map(s => (s \ "@ref" -> s.text))))
        println(layers) 
        
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
