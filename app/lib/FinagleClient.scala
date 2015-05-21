package lib


import com.twitter.finagle.ServiceFactory
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.{Http, Service}
import com.twitter.conversions.time._
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.util.CharsetUtil._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.util.CharsetUtil
import com.twitter.util.Future
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.Play.current
import com.twitter.util.Await


object FinagleClient{


  val hosts = current.configuration.getString("elasticsearch.hosts").get

  val client: Service[HttpRequest, HttpResponse] = Http.newService(hosts)  

  /**
   * The path to the elastic search table (index) and the json to send
   */
  def documentSave(path: List[String], json: JsValue) ={
    Logger.debug("json is %s" format json)
    val req = requestBuilderPut(path, json)
    sendToElastic(req)
  }

  /**
   * Generate a request to send to ElasticSearch
   * @param path the path to your document, as a list
   * @param json ths JsValue representing the payload, i.e. ("id" -> "1") ~ ("part_number" -> "02k7011")
   * @return a request object
   */
  def requestBuilderPut(path: List[String], json: JsValue): DefaultHttpRequest = {
    val payload = ChannelBuffers.copiedBuffer( Json.stringify(json) , UTF_8)
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, _path)
    request.headers().add(USER_AGENT, "Finagle - Play")
    request.headers().add(HOST, hosts) // the host.openOr("localhost") can be replace for host.openOr("default value here")
    request.headers().add(CONTENT_TYPE, "application/json")
    request.headers().add(CONNECTION, "keep-alive")
    request.headers().add(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));
    request.headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost")//santo
    request.setContent(payload)
    Logger.debug("Sending request:\n%s".format(request))
    Logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Generate a request to search the Elastic Search instance
   * @param path the path to your document, as a list
   * @param json ths JsValue representing the payload, i.e. ("id" -> "1") ~ ("part_number" -> "02k7011")
   * @return a request object
   */
  def requestBuilderGet(path: List[String], json: JsValue): DefaultHttpRequest = {
    val payload = ChannelBuffers.copiedBuffer( Json.stringify(json) , UTF_8)
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, _path)
    request.headers().add(USER_AGENT, "Finagle - Play")
    request.headers().add(HOST, "http://localhost")
    request.headers().add(ORIGIN, "http://localhost")//santo
    request.headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*")//santo
    request.headers().add(ALLOW, "*")//santo
    request.headers().add(ACCESS_CONTROL_ALLOW_METHODS, "PUT, GET, POST, DELETE, OPTIONS")//santo
    request.headers().add(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Referrer, User-Agent")//santo    
    request.headers().add(CONTENT_TYPE, "application/x-www-form-urlencoded")
    request.headers().add(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));
    request.setContent(payload)
    Logger.debug("Sending request:\n%s".format(request))
    Logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Generate a request to delete data
   * @param path the path to your document, as a list
   * @return a request object
   */
  def requestBuilderDelete(path: List[String]): DefaultHttpRequest = {
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, _path)
    request.headers().add(USER_AGENT, "Finagle - Play")
    request.headers().add(HOST, hosts)
    request.headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost")//santo
    Logger.debug("Sending request:\n%s".format(request))
    Logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Take a request ans send it
   * @param request The request
   * @return
   */
  def sendToElastic(request: DefaultHttpRequest): Future[HttpResponse] = {
    //val client = clientFactory.apply()()
    //val client = clientFactory
    Logger.debug("Request to send is %s" format request)
    val httpResponse = client(request)

    httpResponse.onSuccess{
      response =>
        Logger.debug("Received response: " + response)
    }.onFailure{ err: Throwable =>
        Logger.error(err.toString)
    }
  }

  /**
   * Deletes all the indeces from elastic search
   * @return
   */
  def unsafeDeleteAllIndeces() ={
    val req = requestBuilderDelete(List())
    sendToElastic(req)
  }

  def documentSearch(json: JsValue): Future[HttpResponse] ={
    val req = requestBuilderGet(List("santix", "items", "_search"), json)
    sendToElastic(req)
  }

  def documentSearch(index: String, indexType: String, json: JsValue): Future[HttpResponse] ={
    val req = requestBuilderGet(List( index, indexType, "_search"), json)
    sendToElastic(req)
  }


}