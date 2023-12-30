
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

enum GET_TODO_ERROR:
    case NOT_FOUND
    case UNKNOWN

case class GetTodo(todoRepository: TodoRepository) extends  UseCase[Id, Todo, GET_TODO_ERROR]:
    
    override def execute(id: Id) = 
        todoRepository.get(id).flatMap({
            case None => Future.successful(UseCaseFail(Array(GET_TODO_ERROR.NOT_FOUND)))
            case Some(value) => Future.successful(UseCaseSuccess(value))
        }).recover({
            case _ => UseCaseFail(Array(GET_TODO_ERROR.UNKNOWN))
        })
