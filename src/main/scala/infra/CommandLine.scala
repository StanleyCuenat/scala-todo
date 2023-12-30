import scala.io.StdIn.readLine
import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Failure

trait BaseCommand
case class Command[I](dto: I, useCase: UseCase[I,_,_]) extends  BaseCommand
case class WrongCommand() extends  BaseCommand
case class NothingCommand() extends BaseCommand

case class  CommandLineInfra(todoRepository: TodoRepository) extends Infra {
    private val CREATE= "CREATE"
    private val UPDATE = "UPDATE"
    private val DELETE = "DELETE"
    private val SET = "SET"
    private val GET = "GET"
    private val SEARCH = "SEARCH"

    private def createCommandFromDto[DTO](dto: Option[DTO], useCase: UseCase[DTO,_,_]): BaseCommand =
        dto match  {
            case Some(e) => Command(e, useCase)
            case None => WrongCommand()
        }

    private def createCommand(cmd: String): BaseCommand =
        val keys = cmd.split("'").filterNot(_.matches("\\s*")).map(_.trim)
        keys.lift(0) match {
            case Some(CREATE) => createCommandFromDto(TodoDtoFactory.createTodoDto(keys.lift(1), keys.lift(2)), CreateTodo(todoRepository))
            case Some(UPDATE) => createCommandFromDto(TodoDtoFactory.updateTodoDto(keys.lift(1), keys.lift(2), keys.lift(3)), UpdateTodo(todoRepository))
            case Some(DELETE) => createCommandFromDto(keys.lift(1), DeleteTodo(todoRepository))
            case Some(GET) => createCommandFromDto(keys.lift(1), GetTodo(todoRepository))
            case Some(SET) => createCommandFromDto(TodoDtoFactory.setTodoDto(keys.lift(1), keys.lift(2)), SetTodo(todoRepository))
            case None => NothingCommand()
            case _ => WrongCommand()
        }

    private def parseInput(input: Option[String]) = input.getOrElse("").split(';')
    
    private def handleSuccess[R, E](result: UseCaseResult[R, E]) =
        result match {
            case UseCaseSuccess(data) =>  data match {
                case e: List[_]  => e.map(println)
                case _ => println(data)
            }
            case UseCaseFail(errors) => errors.map(e => println(e))
        }
    
    private def executeCommand(command: BaseCommand): Future[UseCaseResult[_,_]] =
        command match {
            case Command(dto, useCase) => useCase.execute(dto)
            case NothingCommand() => Future.successful(UseCaseFail(Array("print x to quit")))
            case WrongCommand() => Future.successful(UseCaseFail(Array("COMMAND_NOT_FOUND")))
            case _ => Future.successful(UseCaseFail(Array("")))
        }
    
    private def request(input: Option[String]): Unit =
        parseInput(input)
        .map(createCommand)
        .map(executeCommand)
        .map(f => Await.result(f, Duration.fromNanos(1000000000)))
        .map(handleSuccess)

    def init(): Unit =
        Iterator
        .continually(readLine())
        .takeWhile(_ != "x")
        .foreach(input => {
            request(Option(input))
        })
}