package chapter1_effects.samples

// The side effect we want to delay, unsafeRun is our thunk
final case class MyIO[A](unsafeRun: () => A) {
  def map[B](f: A => B): MyIO[B] =
    MyIO(() => f(unsafeRun()))

  def flatMap[B](f: A => MyIO[B]): MyIO[B] =
    MyIO(() => {
      val myIOB: MyIO[B] = f(unsafeRun())
      val b = myIOB.unsafeRun() // to get my B
      b
    })
}

object MyIO {
  // the printing effect
  def putStr(s: => String): MyIO[Unit] = MyIO(() => println(s))
}

object Printing extends App {
  // described as MyIO but it has not been executed yet
  val hello = MyIO.putStr("Hello!")
  val world = MyIO.putStr("world!")

  val helloWorld = for {
    _ <- hello
    _ <- world
  } yield ()

  helloWorld.unsafeRun()
}