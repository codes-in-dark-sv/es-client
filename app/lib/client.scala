package lib


import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import play.api.Play.current
import play.api.Logger
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.util.CharsetUtil._
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import com.twitter.finagle.ServiceClosedException


object Client{


  val hosts = current.configuration.getString("elasticsearch.hosts").get
  val client: Service[HttpRequest, HttpResponse] = Http.newService( hosts )

  def sendToElastic(request: DefaultHttpRequest): Future[HttpResponse] = {
  	
    //val client = clientFactory.apply(request)
    Logger.debug("Request to send is %s" format request)
    val httpResponse: Future[HttpResponse] = client(request)

    httpResponse.onSuccess{
      response =>
        Logger.debug("Received response: " + response)
        //client.close()
        //client.release()
    }.onFailure{ err: Throwable =>
        Logger.error(err.toString)      
        //client.close()
        //client.release()
    }

    Await.ready(httpResponse)

  }

  def requestBuilderGet(path: List[String], json: JsValue): DefaultHttpRequest = {
    val payload = ChannelBuffers.copiedBuffer( Json.stringify(json) , UTF_8)
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, _path)
    /*
    request.headers().add("User-Agent", "Finagle - Play")
    request.headers().add("Host", hosts)
    request.headers().add(CONTENT_TYPE, "application/x-www-form-urlencoded")
    request.headers().add(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));
    */
    request.headers().add(CONTENT_TYPE, "application/json");
    request.headers().add(ACCEPT, "application/json");
    request.headers().add(USER_AGENT, "Netty 3.2.3.Final");
    request.headers().add(HOST, "localhost:9000");
    request.headers().add(CONNECTION, "keep-alive");
    request.headers().add(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));

    println("\n*** requestBuilderGet 1 ***\n")

    try {
      request.setContent(payload)
    } 
    catch {      
      case ex: ServiceClosedException => ex.printStackTrace()
    }
    //request.setContent(payload)

    println("\n*** requestBuilderGet 2 ***\n")

    Logger.debug("Sending request:\n%s".format(request))
    Logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  def documentSearch(json: JsValue): Future[HttpResponse] ={
    val req = requestBuilderGet(List("santix", "items", "_search"), json)
    sendToElastic(req)
  }


}