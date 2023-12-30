import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

enum SET_TODO_ERROR:
    case NOT_FOUND
    case UNKNOWN

case class SetTodo(todoRepository: TodoRepository) extends  UseCase[SetTodoDTo, Todo, SET_TODO_ERROR]:
    
    override def execute(inputs: SetTodoDTo) = 
        todoRepository.get(inputs.id).flatMap({
            case None => Future.successful(UseCaseFail(Array(SET_TODO_ERROR.NOT_FOUND)))
            case Some(value) => 
                todoRepository
                .update(Todo(value.id, value.title, value.content, inputs.status))
                .flatMap({ case e: Todo => Future.successful(UseCaseSuccess(e)) })
        })
        .recover( {case _ => UseCaseFail(Array(SET_TODO_ERROR.UNKNOWN))} )
