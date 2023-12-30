
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

enum UPDATE_TODO_ERROR:
    case WRONG_TITLE
    case WRONG_CONTENT
    case NOT_FOUND
    case UNKNOWN

case class UpdateTodo(todoRepository: TodoRepository) extends UseCase[(UpdateTodoDTo), Todo, UPDATE_TODO_ERROR]:

    private  def addIsEmptyError(str: String, error: UPDATE_TODO_ERROR): Option[UPDATE_TODO_ERROR] = 
        if(str.length() > 0)
        then None 
        else Option(error)

    private def updateTodo(inputs: UpdateTodoDTo, status: TODO_STATUS): Future[UseCaseResult[Todo, UPDATE_TODO_ERROR]]  =
        val todo = Todo(inputs.id, inputs.title, inputs.content: String, status)
        todoRepository
        .update(todo)
        .flatMap(todo => Future.successful(UseCaseSuccess(todo)))

    private def validateTodo(inputs: UpdateTodoDTo): Option[Array[UPDATE_TODO_ERROR]] =
        val errors = Array (
            addIsEmptyError(inputs.title, UPDATE_TODO_ERROR.WRONG_TITLE),
            addIsEmptyError(inputs.content, UPDATE_TODO_ERROR.WRONG_CONTENT)
        ).flatten
        if (errors.length > 0)
        then Some(errors)
        else None
            

    override def execute(inputs: UpdateTodoDTo) =
        todoRepository
        .get(inputs.id)
        .map({
            case None => UseCaseFail(Array(UPDATE_TODO_ERROR.NOT_FOUND))
            case Some(s) => UseCaseSuccess(s)
        })
        .flatMap({
            case UseCaseSuccess(data) => 
                validateTodo(inputs) match 
                    case None => updateTodo(inputs, data.status)
                    case Some(value) => Future.successful(UseCaseFail(value))
            case other => Future.successful(other)
        }).recover({
            case _ => UseCaseFail(Array(UPDATE_TODO_ERROR.UNKNOWN))
        })
