package lib


import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.{Future, Promise}


object Santix{


    def twitter2Scala[A](f: TwitterFuture[A]): Future[A] = {
        val p = Promise[A]
        f.onSuccess(p.success)
        f.onFailure(p.failure)
        p.future
    }


}