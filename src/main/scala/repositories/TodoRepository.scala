import scala.concurrent.Future

trait TodoRepository:
    def create(todoDto: CreateTodoDTo)(using ec: concurrent.ExecutionContext): Future[Todo]
    def update(todo: Todo)(using ec: concurrent.ExecutionContext): Future[Todo]
    def get(id: Id)(using ec: scala.concurrent.ExecutionContext): Future[Option[Todo]]
    def delete(id: Id)(using ec: scala.concurrent.ExecutionContext): Future[Id]