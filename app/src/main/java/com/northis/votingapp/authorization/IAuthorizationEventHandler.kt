package com.northis.votingapp.authorization

interface IAuthorizationEventHandler {
  fun onAuthorizationBegin()
  fun onTokenAcquired()
  fun onTokenSuccess()
}
