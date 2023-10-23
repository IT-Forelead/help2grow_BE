package help2grow

import org.http4s.dsl.impl.QueryParamDecoderMatcher

package object routes {
  object linkCode extends QueryParamDecoderMatcher[String]("t")

}
