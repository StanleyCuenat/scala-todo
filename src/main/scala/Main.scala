@main def hello: Unit =
  val infra: Infra = CommandLineInfra(TodoRepositoryInMemory)
  infra.init()
