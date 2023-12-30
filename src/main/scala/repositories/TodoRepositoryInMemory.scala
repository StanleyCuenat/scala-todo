import scala.concurrent.Future
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Promise
import scala.concurrent.ExecutionContextExecutor

implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

object TodoRepositoryInMemory extends TodoRepository:
    private val list: ConcurrentHashMap[Id, Todo] = new ConcurrentHashMap[String, Todo]()

    override def create(todo: CreateTodoDTo)(using ec: scala.concurrent.ExecutionContext): Future[Todo] =
        val promise: Promise[Todo] = Promise()
        ec.execute { () =>
            val id: Id = uuid
            Thread.sleep(100)
            list.putIfAbsent(id, Todo(id, todo.title, todo.content, TODO_STATUS.Todo))
            promise.success(list.get(id))
        }
        promise.future  

    override def get(id: Id)(using ec: scala.concurrent.ExecutionContext): Future[Option[Todo]] = 
        val promise: Promise[Option[Todo]] = Promise()
        ec.execute { () =>
            Thread.sleep(100)
            promise.success(Option(list.get(id)))
        }
        promise.future
    
    override def delete(id: Id)(using ec: scala.concurrent.ExecutionContext): Future[Id] = 
        val promise: Promise[Id] = Promise()
        ec.execute { () =>
            Thread.sleep(100)
            list.remove(id)
            promise.success(id)
        }
        promise.future

    override def update(todo: Todo)(using ec: scala.concurrent.ExecutionContext): Future[Todo] =
        val promise: Promise[Todo] = Promise()
        ec.execute { () => 
            list.put(todo.id, todo)
            promise.success(todo)
        }
        promise.future
        
        
    