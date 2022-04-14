object MatchingBrackets {
  private val openClosePairs: Map[Char, Char] = Map(
    ')' -> '(',
    '}' -> '{',
    ']' -> '['
  )

  def isPaired(string: String, s: List[Char] = List()): Boolean = {
    // If the input is empty, an empty stack indicates successful bracket matching
    // An Opening bracket is pushed to the top of the stack
    // A closing bracket causes a pop if the top of the stack is the related opening bracket , otherwise a false return
    // For Everything else, nothing needs to be pushed or popped
    val eitherResultOrStack = string.headOption match {
      case None => Left(s.isEmpty)
      case Some(openBracket) if openClosePairs.values.toSeq.contains(openBracket) => Right(openBracket::s)
      case Some(closedBracket) if openClosePairs.keys.toSeq.contains(closedBracket) => s match {
        case head::tail if head == openClosePairs(closedBracket) => Right(tail)
        case _ => Left(false)
      }
      case Some(_) => Right(s)
    }

    eitherResultOrStack.fold(a => return a, b => isPaired(string.tail, b))
  }

 }