package codeartifact

import sbt.*
import software.amazon.awssdk.services.codeartifact.CodeartifactClient
import software.amazon.awssdk.services.codeartifact.model.GetAuthorizationTokenRequest

import scala.concurrent.duration.*

object CodeArtifact {

  def mkCredentials(token: String)(repo: CodeArtifactRepo): Credentials = Credentials(
    userName = "aws",
    realm = repo.realm,
    host = repo.host,
    passwd = token
  )

  private def getAuthorizationTokenRequest(domain: String, owner: String) =
    GetAuthorizationTokenRequest
      .builder()
      .domain(domain)
      .domainOwner(owner)
      .durationSeconds(15.minutes.toSeconds)
      .build()

  private def getAuthTokenFromRequest(req: GetAuthorizationTokenRequest): String = {
    try {
      CodeartifactClient
        .create()
        .getAuthorizationToken(req)
        .authorizationToken()
    } catch {
      case _: Exception =>
        println(
          "FAILED TO CONTACT CodeArtifact. You will be restricted to local dependencies for this session."
        )
        "**INVALID TOKEN**"
    }
  }

  def getAuthToken(repo: CodeArtifactRepo): String =
    getAuthTokenFromRequest(getAuthorizationTokenRequest(repo.domain, repo.owner))

  object Defaults {
    val READ_TIMEOUT: Int = 1.minutes.toMillis.toInt
    val CONNECT_TIMEOUT: Int = 5.seconds.toMillis.toInt
  }
}
