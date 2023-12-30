import scala.concurrent.Future
import java.security.Policy.Parameters

sealed trait UseCaseResult[+S, +E]
case class UseCaseSuccess[S](data: S) extends UseCaseResult[S, Nothing]
case class UseCaseFail[E](errors: Array[E]) extends UseCaseResult[Nothing, E]

trait BaseUseCase

trait UseCase[I, S, E] extends BaseUseCase:
    def execute(inputs: I): Future[UseCaseResult[S, E]]