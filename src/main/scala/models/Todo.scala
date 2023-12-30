enum TODO_STATUS:
    case Completed, Progress, Todo

object TODO_STATUS_STR {
    val Completed = "COMPLETED"
    val Progress = "PROGRESS"
    val Todo = "TODO"
}

sealed case class Todo(val id: Id, val title: String, val content: String, val status: TODO_STATUS)

sealed case class CreateTodoDTo(val title: String, val content: String, val status: TODO_STATUS = TODO_STATUS.Todo)
sealed case class UpdateTodoDTo(val id: Id, val title: String, val content: String, val status: TODO_STATUS = TODO_STATUS.Todo)
sealed case class SetTodoDTo(val id: Id, val status: TODO_STATUS = TODO_STATUS.Todo)

object TodoDtoFactory {

    def createTodoDto(title: Option[String], content: Option[String]): Option[CreateTodoDTo] =
        for {
            _title <- title
            _content <- content
        } yield CreateTodoDTo(_title, _content)
    
    def updateTodoDto(id: Option[String], title: Option[String], content: Option[String]): Option[UpdateTodoDTo] =
        for {
            _id <- id
            _title <- title
            _content <- content
        } yield UpdateTodoDTo(_id, _title, _content)

    def setTodoDto(id: Option[String], status: Option[String]) = 
        for {
            _id <- id
            _status <- status match {
                case Some(TODO_STATUS_STR.Completed) => Some(TODO_STATUS.Completed)
                case Some(TODO_STATUS_STR.Todo) => Some(TODO_STATUS.Todo)
                case Some(TODO_STATUS_STR.Progress) => Some(TODO_STATUS.Progress)
                case _ => None
            }
        } yield SetTodoDTo(_id, _status)
}
