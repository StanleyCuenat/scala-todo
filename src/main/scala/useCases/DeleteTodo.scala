import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

enum DELETE_TODO_ERROR:
    case NOT_FOUND
    case UNKNOWN

case class DeleteTodo(todoRepository: TodoRepository) extends  UseCase[Id, Id, DELETE_TODO_ERROR]:
    private def delete(id:Id): Future[UseCaseSuccess[Id]] =
        todoRepository.delete(id).map({ case _: Id => UseCaseSuccess(id)})

    override def execute(id: Id) =
        todoRepository
        .get(id)
        .flatMap({
            case None => Future.successful(UseCaseFail(Array(DELETE_TODO_ERROR.NOT_FOUND)))
            case Some(todo) => delete(id)
        })
        .recover({
            case _ => UseCaseFail(Array(DELETE_TODO_ERROR.UNKNOWN))
        })
