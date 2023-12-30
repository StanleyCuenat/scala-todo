import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

enum CREATE_TODO_ERROR:
    case WRONG_TITLE
    case WRONG_CONTENT
    case UNKNOWN

case class CreateTodo(todoRepository: TodoRepository) extends  UseCase[(CreateTodoDTo), Todo, CREATE_TODO_ERROR]:

    private  def addIsEmptyError(str: String, error: CREATE_TODO_ERROR): Option[CREATE_TODO_ERROR] = 
        if(str.length() > 0)
        then None 
        else Option(error)
    
    override def execute(inputs: (CreateTodoDTo)) = 
        val errors = Array (
            addIsEmptyError(inputs.title, CREATE_TODO_ERROR.WRONG_TITLE),
            addIsEmptyError(inputs.content, CREATE_TODO_ERROR.WRONG_CONTENT)
        ).flatten
        if (errors.length > 0) 
        then Future.successful(UseCaseFail(errors))
        else todoRepository.create(inputs).transform({
            case Success(data) => Success(UseCaseSuccess(data))
            case Failure(exception) => Success(UseCaseFail(Array(CREATE_TODO_ERROR.UNKNOWN)))
        })
