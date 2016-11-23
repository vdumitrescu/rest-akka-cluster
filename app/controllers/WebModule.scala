package controllers

import scaldi.Module

class WebModule extends Module {

  binding to injected[Sessions]
  binding to injected[Addresses]
  binding to injected[PaymentMethods]
  binding to injected[Orders]
}
